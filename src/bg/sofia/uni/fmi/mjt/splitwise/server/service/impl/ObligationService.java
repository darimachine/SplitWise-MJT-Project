package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.currencyconverter.CurrencyConverter;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.model.enums.Currency;
import bg.sofia.uni.fmi.mjt.splitwise.server.database.json.ObligationJsonProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.servicerecords.ObligationDirection;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.formatters.ObligationFormatter;

import java.util.Set;

public class ObligationService implements ObligationServiceAPI {
    private final ObligationJsonProcessor processor;
    private final UserServiceAPI userService;
    private final CurrencyConverter currencyConverter;
    private final ObligationFormatter obligationFormatter;

    private static final double EPSILON = 1e-9;
    // Key: fromUser, Value: Map<toUser, Double> (positive sum)
    // for O(1) search
    //Key : User who owes money, Value : Map<user to whom money is owed, amount>
    private final Map<String, Map<String, Double>> obligations;

    public ObligationService(UserServiceAPI userService, CurrencyConverter currencyConverter,
                             ObligationJsonProcessor processor) {
        this.userService = userService;
        this.processor = processor;
        this.currencyConverter = currencyConverter;
        this.obligationFormatter = new ObligationFormatter(userService, currencyConverter);
        this.obligations = processor.loadData();
    }

    @Override
    public Map<String, Map<String, Double>> getAllObligations() {
        return Collections.unmodifiableMap(obligations);
    }

    @Override
    public ObligationDirection findObligationBetweenUsers(String fromUser, String toUser) {
        double forward = obligations.getOrDefault(fromUser, Collections.emptyMap())
            .getOrDefault(toUser, 0.0);
        User user = userService.getUser(fromUser);
        Currency userCurrency = user.getPreferredCurrency();
        if (forward > 0) {
            double converted = currencyConverter.convertFromBGN(userCurrency.getCurrency(), forward);
            return new ObligationDirection(fromUser, toUser, converted, userCurrency.getCurrency());
        }

        double reverse = obligations.getOrDefault(toUser, Map.of()).getOrDefault(fromUser, 0.0);
        if (reverse > 0) {
            double converted = currencyConverter.convertFromBGN(userCurrency.getCurrency(), reverse);
            return new ObligationDirection(toUser, fromUser, converted, userCurrency.getCurrency());
        }
        return null;
    }

    @Override
    public Map<String, Double> getAllObligationsForUser(String username) {

        Map<String, Double> result = obligations.get(username);
        if (result == null) {
            return Collections.emptyMap();
        }
        return new HashMap<>(result); // copy
    }

    @Override
    public void addObligation(String fromUser, String toUser, double amount) {
        obligations.putIfAbsent(fromUser, new HashMap<>());
        Map<String, Double> forwardMap = obligations.get(fromUser);

        if (obligations.containsKey(toUser)) {
            double reverseAmount = obligations.get(toUser).getOrDefault(fromUser, 0.0);
            if (reverseAmount > 0) {
                handleReverseDebt(fromUser, toUser, amount, reverseAmount, forwardMap);
                return;
            }
        }

        // no existing reverse or reverse=0 => accumulate
        addForwardDebt(forwardMap, toUser, amount);
    }

    private void handleReverseDebt(String fromUser, String toUser, double amount, double reverseAmount,
                                   Map<String, Double> forwardMap) {
        Map<String, Double> reverseMap = obligations.get(toUser);

        //Check if converted works
        double converted =
            currencyConverter.convertToBGN(userService.getUser(toUser).getPreferredCurrency().getCurrency(),
                amount);
        double net = reverseAmount - converted;

        if (Math.abs(net) < EPSILON) {
            reverseMap.remove(fromUser);
        } else if (net > 0) {
            reverseMap.put(fromUser, net);
        } else {
            reverseMap.remove(fromUser);
            forwardMap.put(toUser, Math.abs(net));
        }

        if (reverseMap.isEmpty()) {
            obligations.remove(toUser);
        }
        if (forwardMap.isEmpty()) {
            obligations.remove(fromUser);
        }
        processor.saveData(obligations);
    }

    private void addForwardDebt(Map<String, Double> forwardMap, String toUser, double amount) {
        double oldAmount = forwardMap.getOrDefault(toUser, 0.0);
        double converted =
            currencyConverter.convertToBGN(userService.getUser(toUser).getPreferredCurrency().getCurrency(),
                amount);
        double newAmount = oldAmount + converted;
        forwardMap.put(toUser, newAmount);

        if (Math.abs(forwardMap.get(toUser)) < EPSILON) {
            forwardMap.remove(toUser);
        }
        processor.saveData(obligations);
    }

    @Override
    public void payObligation(String fromUser, String toUser, double amount) {

        Map<String, Double> innerMap = obligations.get(fromUser);
        double oldValue = innerMap.get(toUser);
        double newVal = oldValue - amount;
        double converted =
            currencyConverter.convertToBGN(userService.getUser(fromUser).getPreferredCurrency().getCurrency(),
                newVal);
        if (converted == 0) {
            innerMap.remove(toUser);
            if (innerMap.isEmpty()) {
                obligations.remove(fromUser);
            }
        } else {
            innerMap.put(toUser, converted);
        }
        processor.saveData(obligations);
    }

    @Override
    public String getMyFriendsObligations(String loggedUsername, Set<String> friends) {
        Map<String, Double> friendsWhoOweMe = new HashMap<>();
        Map<String, Double> friendsWhoIOwe = new HashMap<>();

        Map<String, Double> allPeopleWhoOweMe = getPeopleWhoOweMe(loggedUsername);
        Map<String, Double> allPeopleWhoIOwe = getPeopleWhoIOwe(loggedUsername);
        for (var entry : allPeopleWhoOweMe.entrySet()) {
            if (friends.contains(entry.getKey())) {
                friendsWhoOweMe.put(entry.getKey(), entry.getValue());
            }
        }
        for (var entry : allPeopleWhoIOwe.entrySet()) {
            if (friends.contains(entry.getKey())) {
                friendsWhoIOwe.put(entry.getKey(), entry.getValue());
            }
        }
        String friendList = String.join(", ", friends);
        return "Friends: [ " + friendList + " ] \n" +
            obligationFormatter.formatObligations(friendsWhoOweMe, friendsWhoIOwe, loggedUsername);
    }

    @Override
    public String getMyGroupObligations(String loggedUsername, Map<String, Set<String>> groups) {

        Map<String, Double> allPeopleWhoOweMe = getPeopleWhoOweMe(loggedUsername);
        Map<String, Double> allPeopleWhoIOwe = getPeopleWhoIOwe(loggedUsername);

        StringBuilder sb = new StringBuilder();
        for (String groupName : groups.keySet()) {
            Set<String> groupMembers = groups.get(groupName);

            Map<String, Double> groupWhoOweMe = new HashMap<>();
            Map<String, Double> groupWhoIOwe = new HashMap<>();

            for (String member : groupMembers) {
                if (!member.equals(loggedUsername)) {
                    addIfPresent(groupWhoOweMe, member, allPeopleWhoOweMe);
                    addIfPresent(groupWhoIOwe, member, allPeopleWhoIOwe);
                }
            }

            //Format output using a separate ObligationFormatter
            sb.append("==== Group: ").append(groupName).append(" ===\n")
                .append(obligationFormatter.formatObligations(groupWhoOweMe, groupWhoIOwe, loggedUsername));
        }

        return sb.isEmpty() ? "You are not in any group, or there are no group obligations." : sb.toString();
    }

    private void addIfPresent(Map<String, Double> targetMap, String member, Map<String, Double> sourceMap) {
        if (sourceMap.containsKey(member)) {
            targetMap.put(member, sourceMap.get(member));
        }
    }

    private Map<String, Double> getPeopleWhoOweMe(String loggedUsername) {
        Map<String, Double> peopleWhoOweMe = new HashMap<>();
        for (String fromUser : obligations.keySet()) {
            Map<String, Double> innermap = obligations.get(fromUser);
            for (String toUser : innermap.keySet()) {
                if (toUser.equals(loggedUsername)) {
                    peopleWhoOweMe.put(fromUser, innermap.get(toUser));
                }
            }
        }
        return peopleWhoOweMe;
    }

    private Map<String, Double> getPeopleWhoIOwe(String loggedUsername) {
        return obligations.getOrDefault(loggedUsername, Collections.emptyMap());
    }

}
