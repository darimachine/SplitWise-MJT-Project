package bg.sofia.uni.fmi.mjt.splitwise.server.currencyconverter.api.fetcher;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConversationRatesTest {

    @Test
    void testConstructor_ValidConversionRates() {
        Map<String, Double> rates = Map.of("USD", 1.0, "EUR", 0.85);
        ConversationRates conversationRates = new ConversationRates(rates);

        assertNotNull(conversationRates, "ConversationRates object should not be null.");
        assertEquals(rates, conversationRates.conversionRates(), "Conversion rates should match the provided map.");
    }

    @Test
    void testConstructor_NullConversionRates() {
        ConversationRates conversationRates = new ConversationRates(null);

        assertNotNull(conversationRates, "ConversationRates object should be created even if null is provided.");
        assertNull(conversationRates.conversionRates(), "Conversion rates should be null if null was provided.");
    }

    @Test
    void testGetConversionRates_NonEmptyMap() {
        Map<String, Double> rates = Map.of("USD", 1.0, "GBP", 0.75);
        ConversationRates conversationRates = new ConversationRates(rates);

        assertEquals(2, conversationRates.conversionRates().size(), "Should contain two currency conversion rates.");
        assertTrue(conversationRates.conversionRates().containsKey("USD"), "USD should be present in conversion rates.");
        assertEquals(1.0, conversationRates.conversionRates().get("USD"), "USD rate should be 1.0.");
        assertEquals(0.75, conversationRates.conversionRates().get("GBP"), "GBP rate should be 0.75.");
    }

    @Test
    void testGetConversionRates_EmptyMap() {
        ConversationRates conversationRates = new ConversationRates(Map.of());

        assertNotNull(conversationRates.conversionRates(), "Conversion rates should not be null.");
        assertTrue(conversationRates.conversionRates().isEmpty(), "Conversion rates should be an empty map.");
    }

    @Test
    void testSerializedNameAnnotation() {
        assertDoesNotThrow(() -> {
            Map<String, Double> rates = Map.of("USD", 1.0);
            ConversationRates conversationRates = new ConversationRates(rates);
            assertEquals(rates, conversationRates.conversionRates(), "Gson should correctly map 'conversion_rates'.");
        }, "SerializedName should properly map conversion_rates field.");
    }
}
