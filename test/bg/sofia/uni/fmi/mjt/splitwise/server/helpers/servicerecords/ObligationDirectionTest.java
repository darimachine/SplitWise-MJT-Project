package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.servicerecords;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObligationDirectionTest {

    @Test
    void testObligationDirection_CorrectValues() {
        ObligationDirection obligation = new ObligationDirection("Alice", "Bob", 50.75, "USD");

        assertEquals("Alice", obligation.debtor(), "Debtor should be Alice.");
        assertEquals("Bob", obligation.creditor(), "Creditor should be Bob.");
        assertEquals(50.75, obligation.amount(), 0.001, "Amount should be 50.75.");
        assertEquals("USD", obligation.currency(), "Currency should be USD.");
    }

    @Test
    void testToString_ReturnsCorrectFormat() {
        ObligationDirection obligation = new ObligationDirection("Charlie", "David", 100.5, "EUR");
        String expected = "Charlie owes David 100.50 EUR";
        assertEquals(expected, obligation.toString(), "toString() should return the correct formatted string.");
    }

    @Test
    void testToString_HandlesZeroAmount() {
        ObligationDirection obligation = new ObligationDirection("Eve", "Frank", 0.0, "GBP");
        String expected = "Eve owes Frank 0.00 GBP";
        assertEquals(expected, obligation.toString(), "toString() should format zero amount correctly.");
    }
}
