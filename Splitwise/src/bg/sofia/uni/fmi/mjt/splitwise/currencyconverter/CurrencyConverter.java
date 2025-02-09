package bg.sofia.uni.fmi.mjt.splitwise.currencyconverter;

import bg.sofia.uni.fmi.mjt.splitwise.model.enums.Currency;

import java.util.HashMap;
import java.util.Map;

public class CurrencyConverter {
  // tazi informaciq shte se vzeme ot API, primeren KLAS!!!!! koito shte se modificira
    private static final Map<Currency, Double> rates;

    static {
        rates = new HashMap<>();
        rates.put(Currency.BGN, 1.0);    // BGN е базова валута
        rates.put(Currency.USD, 0.57);   // 1 BGN ~ 0.57 USD (пример)
        rates.put(Currency.EUR, 0.51);   // 1 BGN ~ 0.51 EUR (пример)
    }

    /**
     * Конвертира сума от BGN в дадена валута.
     */
    public static double convertFromBGN(double amountBGN, Currency targetCurrency) {
        Double rate = rates.get(targetCurrency);
        if (rate == null) {
            throw new IllegalArgumentException("Unsupported currency: " + targetCurrency);
        }
        return amountBGN * rate;
    }
// PRIMEREN!!!
}
