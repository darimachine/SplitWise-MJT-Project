//package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation;
//
//import bg.sofia.uni.fmi.mjt.splitwise.exceptions.obligation.InvalidPaymentAmountException;
//import bg.sofia.uni.fmi.mjt.splitwise.exceptions.obligation.ObligationNotFoundException;
//import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotFoundException;
//import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators.ObligationValidator;
//import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators.UserValidator;
//import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//public class ObligationValidatorTest {
//    private ObligationValidator validator;
//    private ObligationServiceAPI obligationService;
//    private UserValidator userValidator;
//
//    @BeforeEach
//    void setUp() {
//        obligationService = mock(ObligationServiceAPI.class);
//        userValidator = mock(UserValidator.class);
//        ObligationValidator.resetForTest();
//        validator = ObligationValidator.getInstance(obligationService, userValidator);
//
//        Map<String, Map<String, Double>> mockObligations = new HashMap<>();
//        mockObligations.put("alex", new HashMap<>(Map.of("maria", 10.0)));
//        mockObligations.put("maria", new HashMap<>(Map.of("john", 5.0)));
//
//        when(obligationService.getAllObligations()).thenReturn(mockObligations);
//    }
//
//    @Test
//    void testValidateUsersExist_WhenUserDoesNotExist_ShouldThrowException() {
//        doThrow(new UserNotFoundException("User not found"))
//            .when(userValidator).validateUser("unknown");
//
//        assertThrows(UserNotFoundException.class, () -> validator.validateUsersExist("unknown", "maria"),
//            "Should throw exception when user does not exist");
//    }
//    @Test
//    void testValidateUsersExist_WhenUsersExist_ShouldPass() {
//        doNothing().when(userValidator).validateUser("alex");
//        doNothing().when(userValidator).validateUser("maria");
//
//        assertDoesNotThrow(() -> validator.validateUsersExist("alex", "maria"),
//            "Should not throw exception if both users exist");
//    }
//
//
//
//    @Test
//    void testValidatePositiveAmount_WhenAmountPositive_ShouldPass() {
//        assertDoesNotThrow(() -> validator.validatePositiveAmount(10.0),
//            "Should not throw exception for positive amount");
//    }
//    @Test
//    void testValidatePositiveAmount_WhenAmountZero_ShouldThrowException() {
//        assertThrows(IllegalArgumentException.class, () -> validator.validatePositiveAmount(0.0),
//            "Should throw exception for zero amount");
//    }
//
//    @Test
//    void testValidateObligationExists_WhenObligationExists_ShouldPass() {
//        assertDoesNotThrow(() -> validator.validateObligationExists("alex", "maria"),
//            "Should not throw exception for existing obligation");
//    }
//
//    @Test
//    void testValidateObligationExists_WhenObligationDoesNotExist_ShouldThrowException() {
//        assertThrows(ObligationNotFoundException.class, () -> validator.validateObligationExists("alex", "john"),
//            "Should throw exception for non-existing obligation");
//    }
//
//    @Test
//    void testValidatePaymentDoesNotExceedObligation_WhenWithinLimit_ShouldPass() {
//        assertDoesNotThrow(() -> validator.validatePaymentDoesNotExceedObligation("alex", "maria", 5.0),
//            "Should not throw exception if payment is within limit");
//    }
//
//    @Test
//    void testValidatePaymentExceedObligation() {
//        assertThrows(InvalidPaymentAmountException.class, () ->
//                validator.validatePaymentDoesNotExceedObligation("alex", "maria", 15.0),
//            "Should throw exception if payment exceeds obligation");
//    }
//
//    @Test
//    void testValidateAddObligation_WhenSameUser_ShouldThrowException() {
//        assertThrows(IllegalArgumentException.class, () -> validator.validateAddObligation("alex", "alex", 10.0),
//            "Should throw exception if a user tries to add an obligation to themselves");
//    }
//
//    @Test
//    void testValidateAddObligation_WhenValid_ShouldPass() {
//        doNothing().when(userValidator).validateUser("alex");
//        doNothing().when(userValidator).validateUser("maria");
//
//        assertDoesNotThrow(() -> validator.validateAddObligation("alex", "maria", 10.0),
//            "Should not throw exception for a valid obligation");
//    }
//    @Test
//    void testValidatePaymentDoesNotExceedObligation_WhenNoObligationExists_ShouldThrowException() {
//        assertThrows(NullPointerException.class, () ->
//                validator.validatePaymentDoesNotExceedObligation("alex", "john", 5.0),
//            "Should throw exception if no obligation exists between users");
//    }
//
//    @Test
//    void testValidateAddObligation_WhenAmountIsNegative_ShouldThrowException() {
//        doNothing().when(userValidator).validateUser("alex");
//        doNothing().when(userValidator).validateUser("maria");
//
//        assertThrows(IllegalArgumentException.class, () -> validator.validateAddObligation("alex", "maria", -5.0),
//            "Should throw exception when trying to add a negative obligation");
//    }
//}
