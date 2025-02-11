package bg.sofia.uni.fmi.mjt.splitwise.model.enums;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCurrencyException;

public enum Currency {
    BGN("BGN"),
    USD("USD"),
    EUR("EUR"),
    GBP("GBP");
    private final String currency;

    Currency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public static Currency fromString(String currency) {
        for (Currency c : Currency.values()) {
            if (c.getCurrency().equalsIgnoreCase(currency)) {
                return c;
            }
        }
        throw new InvalidCurrencyException("Invalid category: " + currency);
    }
}
