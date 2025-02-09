package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.database.json.ObligationJsonProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.formatters.ObligationFormatter;

import java.util.Set;
import java.util.stream.Collectors;

public class ObligationService implements ObligationServiceAPI {
    private final ObligationJsonProcessor processor;
    private final ObligationFormatter obligationFormatter;

    // Key: fromUser, Value: Map<toUser, Double> (positive sum)
    // for O(1) search
    private final Map<String, Map<String, Double>> obligations;

    public ObligationService(UserServiceAPI userService, ObligationJsonProcessor processor) {
        this.processor = processor;
        this.obligationFormatter = new ObligationFormatter(userService);
        this.obligations = processor.loadData();
    }

    @Override
    public Map<String, Map<String, Double>> getAllObligations() {
        return Collections.unmodifiableMap(obligations);
    }

    @Override
    public double findObligationBetweenUsers(String fromUser, String toUser) {
        return obligations
            .getOrDefault(fromUser, Collections.emptyMap())
            .getOrDefault(toUser, obligations
                .getOrDefault(toUser, Collections.emptyMap())
                .getOrDefault(fromUser, 0.0));
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
        // the idea is recalculating the obligation between two users
        // Alex -> Maria 10 and then Maria -> Alex 5 => Alex -> Maria 5 (in one record)
        String user1 = fromUser.compareTo(toUser) < 0 ? fromUser : toUser;
        String user2 = fromUser.compareTo(toUser) < 0 ? toUser : fromUser;
        obligations.putIfAbsent(user1, new HashMap<>());
        Map<String, Double> innerMap = obligations.get(user1);
        double oldAmount = innerMap.getOrDefault(user2, 0.0);
        if (fromUser.equals(user1)) {
            innerMap.put(user2, oldAmount + amount);
        } else {
            double newBalance = oldAmount - amount;
            if (newBalance > 0) {
                innerMap.put(user2, newBalance);
            } else if (newBalance < 0) {
                innerMap.remove(user2);
                obligations.putIfAbsent(user2, new HashMap<>());
                obligations.get(user2).put(user1, -newBalance);
            } else {
                innerMap.remove(user2);
            }
        }
        processor.saveData(obligations);
    }

    @Override
    public void payObligation(String fromUser, String toUser, double amount) {

        Map<String, Double> innerMap = obligations.get(fromUser);
        double oldValue = innerMap.get(toUser);
        double newVal = oldValue - amount;
        if (newVal == 0) {
            innerMap.remove(toUser);
            if (innerMap.isEmpty()) {
                obligations.remove(fromUser);
            }
        } else {
            innerMap.put(toUser, newVal);
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
        return "Friends: [ " + friendList + " ] \n" + obligationFormatter.formatObligations(friendsWhoOweMe, friendsWhoIOwe, loggedUsername);
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
