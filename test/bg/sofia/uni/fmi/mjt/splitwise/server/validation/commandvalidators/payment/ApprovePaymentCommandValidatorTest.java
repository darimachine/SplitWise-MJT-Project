package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.payment;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.obligation.InvalidPaymentAmountException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.obligation.ObligationNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.ObligationValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.UserValidator;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApprovePaymentCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private ObligationValidator obligationValidatorMock;
    private ApprovePaymentCommandValidator validator;
    private SocketChannel clientChannelMock;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        obligationValidatorMock = mock(ObligationValidator.class);
        UserValidator userValidatorMock = mock(UserValidator.class);
        validator = new ApprovePaymentCommandValidator(authManagerMock, obligationValidatorMock, userValidatorMock);
        clientChannelMock = mock(SocketChannel.class);
    }

    @Test
    void testValidate_ThrowsIllegalArgumentExceptionIfInvalidArguments() {
        String[] invalidArgs = {}; // Empty arguments
        assertThrows(InvalidCommandException.class, () -> validator.validate(invalidArgs, clientChannelMock),
            "Should throw IllegalArgumentException when arguments are missing.");
    }

    @Test
    void testValidate_ThrowsUserNotAuthenticatedExceptionIfNotLoggedIn() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(false);
        String[] args = {"payed", "friend", "10"};

        assertThrows(UserNotAuthenticatedException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw UserNotAuthenticatedException if user is not logged in.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfAmountIsNotNumber() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        String[] args = {"payed", "friend", "not-a-number"};

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException if amount is not a valid number.");
    }

    @Test
    void testValidate_ThrowsObligationNotFoundExceptionIfNoObligationExists() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        String[] args = {"payed", "friend", "10"};
        doThrow(new ObligationNotFoundException("No obligation found"))
            .when(obligationValidatorMock).validateObligationExists("friend", "alex");

        assertThrows(ObligationNotFoundException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw ObligationNotFoundException when no obligation exists between users.");
    }

    @Test
    void testValidate_ThrowsInvalidPaymentAmountExceptionIfAmountExceedsObligation() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        String[] args = {"payed", "friend", "1000"};
        doThrow(new InvalidPaymentAmountException("Payment exceeds obligation"))
            .when(obligationValidatorMock).validatePaymentDoesNotExceedObligation("friend", "alex", 1000);

        assertThrows(InvalidPaymentAmountException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidPaymentAmountException when payment exceeds obligation.");
    }

    @Test
    void testValidate_DoesNotThrowIfValidArguments() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        String[] args = {"payed", "friend", "50"};

        assertDoesNotThrow(() -> validator.validate(args, clientChannelMock),
            "Should not throw any exception if arguments are valid.");
    }
}
