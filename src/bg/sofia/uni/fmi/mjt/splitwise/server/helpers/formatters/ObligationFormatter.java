package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.formatters;

import bg.sofia.uni.fmi.mjt.splitwise.server.currencyconverter.CurrencyConverter;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.model.enums.Currency;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;

import java.util.Map;

public class ObligationFormatter {

    private final UserServiceAPI userService;
    private final CurrencyConverter currencyConverter;

    public ObligationFormatter(UserServiceAPI userService, CurrencyConverter currencyConverter) {
        this.userService = userService;
        this.currencyConverter = currencyConverter;
    }

    public String formatObligations(Map<String, Double> peopleWhoOweMe,
                                    Map<String, Double> peopleWhoIOwe,
                                    String loggedUsername) {

        StringBuilder sb = new StringBuilder();
        User user = userService.getUser(loggedUsername);
        Currency userCurrency = user.getPreferredCurrency();
        appendPeopleWhoOweMe(sb, peopleWhoOweMe, userCurrency);
        appendPeopleWhoIOwe(sb, peopleWhoIOwe, userCurrency);

        return sb.toString();
    }

    private void appendPeopleWhoOweMe(StringBuilder sb, Map<String, Double> peopleWhoOweMe,
                                      Currency currency) {
        if (peopleWhoOweMe.isEmpty()) {
            sb.append("  - Nobody owes me money.\n");
            return;
        }
        for (var entry : peopleWhoOweMe.entrySet()) {
            User user = userService.getUser(entry.getKey());
            double amount = entry.getValue();
            double converted = currencyConverter.convertFromBGN(currency.getCurrency(), amount);
            sb.append(String.format("  * %s (%s): Owes you %.2f %s\n",
                user.getUsername(), user.getFirstName(), converted, currency.getCurrency()));
        }
    }

    private void appendPeopleWhoIOwe(StringBuilder sb, Map<String, Double> peopleWhoIOwe,
                                     Currency currency) {
        if (peopleWhoIOwe.isEmpty()) {
            sb.append("  - You don't owe money to anyone.\n");
            return;
        }
        for (var entry : peopleWhoIOwe.entrySet()) {
            User user = userService.getUser(entry.getKey());
            double amount = entry.getValue();
            double converted = currencyConverter.convertFromBGN(currency.getCurrency(), amount);
            sb.append(String.format("  * %s (%s): You owe %.2f %s\n",
                user.getUsername(), user.getFirstName(), converted, currency.getCurrency()));
        }
    }
}
