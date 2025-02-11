package bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.obligation.InvalidPaymentAmountException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.obligation.ObligationNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class ObligationValidatorTest {

    private ObligationValidator obligationValidator;
    private ObligationServiceAPI obligationServiceMock;

    @BeforeEach
    void setUp() {
        obligationServiceMock = mock(ObligationServiceAPI.class);
        obligationValidator = new ObligationValidator(obligationServiceMock);
    }

    @Test
    void testValidatePositiveAmount_Success() {
        assertDoesNotThrow(() -> obligationValidator.validatePositiveAmount(10.0),
            "Should not throw for positive amount");
    }

    @Test
    void testValidatePositiveAmount_Zero_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> obligationValidator.validatePositiveAmount(0.0),
            "Should throw if amount is zero");
    }

    @Test
    void testValidatePositiveAmount_Negative_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> obligationValidator.validatePositiveAmount(-5.0),
            "Should throw if amount is negative");
    }

    @Test
    void testValidateObligationExists_Success() {
        Map<String, Map<String, Double>> obligations = new HashMap<>();
        obligations.put("alice", Map.of("bob", 100.0));
        when(obligationServiceMock.getAllObligations()).thenReturn(obligations);

        assertDoesNotThrow(() -> obligationValidator.validateObligationExists("alice", "bob"),
            "Should not throw if obligation exists");
    }

    @Test
    void testValidateObligationExists_Success_ReverseDirection() {
        Map<String, Map<String, Double>> obligations = new HashMap<>();
        obligations.put("bob", Map.of("alice", 100.0));
        when(obligationServiceMock.getAllObligations()).thenReturn(obligations);

        assertDoesNotThrow(() -> obligationValidator.validateObligationExists("alice", "bob"),
            "Should not throw if obligation exists in reverse direction");
    }

    @Test
    void testValidateObligationExists_NoObligation_ThrowsObligationNotFoundException() {
        when(obligationServiceMock.getAllObligations()).thenReturn(Collections.emptyMap());

        assertThrows(ObligationNotFoundException.class,
            () -> obligationValidator.validateObligationExists("alice", "bob"),
            "Should throw if no obligation exists between users");
    }

    @Test
    void testValidatePaymentDoesNotExceedObligation_Success() {
        Map<String, Double> innerMap = new HashMap<>();
        innerMap.put("bob", 100.0);
        Map<String, Map<String, Double>> obligations = new HashMap<>();
        obligations.put("alice", innerMap);
        when(obligationServiceMock.getAllObligations()).thenReturn(obligations);

        assertDoesNotThrow(() -> obligationValidator.validatePaymentDoesNotExceedObligation("alice", "bob", 50.0),
            "Should not throw if payment does not exceed obligation");
    }

    @Test
    void testValidatePaymentDoesNotExceedObligation_ExactAmount_Success() {
        Map<String, Double> innerMap = new HashMap<>();
        innerMap.put("bob", 50.0);
        Map<String, Map<String, Double>> obligations = new HashMap<>();
        obligations.put("alice", innerMap);
        when(obligationServiceMock.getAllObligations()).thenReturn(obligations);

        assertDoesNotThrow(() -> obligationValidator.validatePaymentDoesNotExceedObligation("alice", "bob", 50.0),
            "Should not throw if payment equals the obligation");
    }

    @Test
    void testValidatePaymentDoesNotExceedObligation_ExceedsObligation_ThrowsInvalidPaymentAmountException() {
        Map<String, Double> innerMap = new HashMap<>();
        innerMap.put("bob", 50.0);
        Map<String, Map<String, Double>> obligations = new HashMap<>();
        obligations.put("alice", innerMap);
        when(obligationServiceMock.getAllObligations()).thenReturn(obligations);

        assertThrows(InvalidPaymentAmountException.class,
            () -> obligationValidator.validatePaymentDoesNotExceedObligation("alice", "bob", 60.0),
            "Should throw if payment exceeds obligation");
    }

    @Test
    void testValidateAddObligation_Success() {
        assertDoesNotThrow(() -> obligationValidator.validateAddObligation("alice", "bob", 50.0),
            "Should not throw if obligation is valid");
    }

    @Test
    void testValidateAddObligation_SameUser_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> obligationValidator.validateAddObligation("alice", "alice", 50.0),
            "Should throw if adding obligation to the same user");
    }

    @Test
    void testValidateAddObligation_NegativeAmount_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> obligationValidator.validateAddObligation("alice", "bob", -50.0),
            "Should throw if obligation amount is negative");
    }

    @Test
    void testValidateAddObligation_ZeroAmount_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> obligationValidator.validateAddObligation("alice", "bob", 0.0),
            "Should throw if obligation amount is zero");
    }
}
