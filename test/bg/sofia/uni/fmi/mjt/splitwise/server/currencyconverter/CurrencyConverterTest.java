package bg.sofia.uni.fmi.mjt.splitwise.server.currencyconverter;

import bg.sofia.uni.fmi.mjt.splitwise.server.currencyconverter.api.fetcher.ConversationRates;
import bg.sofia.uni.fmi.mjt.splitwise.server.currencyconverter.api.fetcher.ExchangeRateFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyConverterTest {

    private static final String BASE_CURRENCY = "BGN";
    private static final double AMOUNT_BGN = 100.0;
    private static final double USD_RATE = 0.56;
    private static final double EUR_RATE = 0.51;
    private static final double GBP_RATE = 0.45;
    private CurrencyConverter currencyConverter;

    @BeforeEach
    void setUp() {
        ExchangeRateFetcher fetcherMock = mock(ExchangeRateFetcher.class);
        ConversationRates ratesMock = new ConversationRates(
            Map.of("USD", USD_RATE, "EUR", EUR_RATE, "GBP", GBP_RATE)
        );

        when(fetcherMock.fetchRates(BASE_CURRENCY)).thenReturn(ratesMock);
        currencyConverter = new CurrencyConverter(fetcherMock, BASE_CURRENCY);
    }

    @Test
    void testConvertFromBGN_ValidCurrency() {
        double expectedUsd = AMOUNT_BGN * USD_RATE;
        double expectedEur = AMOUNT_BGN * EUR_RATE;
        double expectedGbp = AMOUNT_BGN * GBP_RATE;

        assertEquals(expectedUsd, currencyConverter.convertFromBGN("USD", AMOUNT_BGN), 0.01,
            "Conversion from BGN to USD should be correct.");
        assertEquals(expectedEur, currencyConverter.convertFromBGN("EUR", AMOUNT_BGN), 0.01,
            "Conversion from BGN to EUR should be correct.");
        assertEquals(expectedGbp, currencyConverter.convertFromBGN("GBP", AMOUNT_BGN), 0.01,
            "Conversion from BGN to GBP should be correct.");
    }

    @Test
    void testConvertFromBGN_ThrowsExceptionForInvalidCurrency() {
        assertThrows(IllegalArgumentException.class,
            () -> currencyConverter.convertFromBGN("INVALID", AMOUNT_BGN),
            "Should throw IllegalArgumentException for unsupported currency.");
    }

    @Test
    void testConvertToBGN_ValidCurrency() {
        double expectedUsd = AMOUNT_BGN / USD_RATE;
        double expectedEur = AMOUNT_BGN / EUR_RATE;
        double expectedGbp = AMOUNT_BGN / GBP_RATE;

        assertEquals(expectedUsd, currencyConverter.convertToBGN("USD", AMOUNT_BGN), 0.01,
            "Conversion to BGN from USD should be correct.");
        assertEquals(expectedEur, currencyConverter.convertToBGN("EUR", AMOUNT_BGN), 0.01,
            "Conversion to BGN from EUR should be correct.");
        assertEquals(expectedGbp, currencyConverter.convertToBGN("GBP", AMOUNT_BGN), 0.01,
            "Conversion to BGN from GBP should be correct.");
    }

    @Test
    void testConvertToBGN_ThrowsExceptionForInvalidCurrency() {
        assertThrows(IllegalArgumentException.class,
            () -> currencyConverter.convertToBGN("INVALID", AMOUNT_BGN),
            "Should throw IllegalArgumentException for unsupported currency.");
    }
}
