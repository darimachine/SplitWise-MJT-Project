package bg.sofia.uni.fmi.mjt.splitwise.server.currencyconverter.api.request;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.api.RequiredBaseCurrenycyMissingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

class ExchangeRateURITest {

    private static final String BASE_CURRENCY = "USD";
    private static final String MOCK_API_KEY = "null"; // Mock API key for testing
    private static final String EXPECTED_URI = "https://v6.exchangerate-api.com/v6/null/latest/USD";

    @BeforeEach
    void setUp() {
        System.setProperty("Exchange_Rate_API_KEY", MOCK_API_KEY);
    }

    @Test
    void testNewBuilder_ValidBaseCurrency_DoesNotThrow() {
        assertDoesNotThrow(() -> ExchangeRateURI.newBuilder(BASE_CURRENCY),
            "Valid base currency should not throw an exception.");
    }

    @Test
    void testNewBuilder_NullBaseCurrency_ThrowsException() {
        assertThrows(RequiredBaseCurrenycyMissingException.class,
            () -> ExchangeRateURI.newBuilder(null),
            "Null base currency should throw RequiredBaseCurrenycyMissingException.");
    }

    @Test
    void testNewBuilder_EmptyBaseCurrency_ThrowsException() {
        assertThrows(RequiredBaseCurrenycyMissingException.class,
            () -> ExchangeRateURI.newBuilder(""),
            "Empty base currency should throw RequiredBaseCurrenycyMissingException.");
    }

    @Test
    void testBuild_CreatesValidExchangeRateURI() {
        ExchangeRateURI exchangeRateURI = ExchangeRateURI.newBuilder(BASE_CURRENCY).build();
        assertNotNull(exchangeRateURI, "The ExchangeRateURI object should not be null.");
    }

    @Test
    void testGetUri_CorrectlyConstructsUri() {
        ExchangeRateURI exchangeRateURI = ExchangeRateURI.newBuilder(BASE_CURRENCY).build();
        URI uri = exchangeRateURI.getUri();

        assertEquals(EXPECTED_URI, uri.toString(), "URI should be correctly formatted.");
    }

    @Test
    void testGetUri_RuntimeExceptionOnFailure() {
        ExchangeRateURI exchangeRateURI = spy(ExchangeRateURI.newBuilder(BASE_CURRENCY).build());

        doThrow(new RuntimeException("Failed to create URI")).when(exchangeRateURI).getUri();

        assertThrows(RuntimeException.class, exchangeRateURI::getUri,
            "Should throw RuntimeException when URI creation fails.");
    }
}
