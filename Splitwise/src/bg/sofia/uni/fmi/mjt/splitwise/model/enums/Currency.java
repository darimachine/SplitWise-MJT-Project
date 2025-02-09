package bg.sofia.uni.fmi.mjt.splitwise.model.enums;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCurrencyException;

public enum Currency {
    BGN("BGN"),
    USD("USD"),
    EUR("EUR");

    private final String currency;

    Currency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public static Currency fromString(String category) {
        for (Currency c : Currency.values()) {
            if (c.getCurrency().equalsIgnoreCase(category)) {
                return c;
            }
        }
        throw new InvalidCurrencyException("Invalid category: " + category);
    }
}
