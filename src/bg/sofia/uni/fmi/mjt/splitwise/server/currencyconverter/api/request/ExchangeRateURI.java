package bg.sofia.uni.fmi.mjt.splitwise.server.currencyconverter.api.request;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.api.RequiredBaseCurrenycyMissingException;

import java.net.URI;

public class ExchangeRateURI {
    private static final String API_SCHEME = "https";
    private static final String API_HOST = "v6.exchangerate-api.com";
    private static final String API_PATH = "/v6/";
    private static final String API_KEY = System.getenv("Exchange_Rate_API_KEY");
    private static final String LATEST = "/latest/";

    private final String baseCurrency;

    private ExchangeRateURI(ExchangeRateUriBuilder builder) {
        this.baseCurrency = builder.baseCurrency;
    }

    public static ExchangeRateUriBuilder newBuilder(String baseCurrency) {
        if (baseCurrency == null || baseCurrency.isEmpty()) {
            throw new RequiredBaseCurrenycyMissingException("baseCurrency are required and cannot be null or empty.");
        }
        return new ExchangeRateUriBuilder(baseCurrency);
    }

    public URI getUri() {
        try {
            return new URI(API_SCHEME, API_HOST, API_PATH + API_KEY + LATEST + baseCurrency, null, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create URI", e);
        }
    }

    public static class ExchangeRateUriBuilder {

        private final String baseCurrency;

        private ExchangeRateUriBuilder(String baseCurrency) {
            this.baseCurrency = baseCurrency;
        }

        public ExchangeRateURI build() {
            return new ExchangeRateURI(this);
        }
    }
}
