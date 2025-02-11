package bg.sofia.uni.fmi.mjt.splitwise.server.currencyconverter;

import bg.sofia.uni.fmi.mjt.splitwise.server.currencyconverter.api.fetcher.ConversationRates;
import bg.sofia.uni.fmi.mjt.splitwise.server.currencyconverter.api.fetcher.ExchangeRateFetcher;

public class CurrencyConverter {
    private final ExchangeRateFetcher fetcher;
    private final ConversationRates rates;

    public CurrencyConverter(ExchangeRateFetcher fetcher, String baseCurrency) {
        this.fetcher = fetcher;
        this.rates = fetcher.fetchRates(baseCurrency);
    }

    public double convertFromBGN(String toCurrency, double amountBGN) {
        Double rate = rates.conversionRates().get(toCurrency);
        if (rate == null) {
            throw new IllegalArgumentException("Unsupported currency: " + toCurrency);
        }
        return amountBGN * rate;
    }

    public double convertToBGN(String fromCurrency, double amount) {
        Double rate = rates.conversionRates().get(fromCurrency);
        if (rate == null) {
            throw new IllegalArgumentException("Unsupported currency: " + fromCurrency);
        }
        return amount / rate;
    }
}
