package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.friend;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.ObligationValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AddObligationWithFriendCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private UserValidator userValidatorMock;
    private ObligationValidator obligationValidatorMock;
    private SocketChannel clientChannelMock;
    private AddObligationWithFriendCommandValidator validator;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        userValidatorMock = mock(UserValidator.class);
        obligationValidatorMock = mock(ObligationValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        validator =
            new AddObligationWithFriendCommandValidator(authManagerMock, userValidatorMock, obligationValidatorMock);
    }

    @Test
    void testValidate_SuccessfulValidation() {
        String[] args = {"add-obligation", "friendUser", "50"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Smith"));

        assertDoesNotThrow(() -> validator.validate(args, clientChannelMock),
            "Validation should pass with correct arguments.");

        verify(userValidatorMock).validateUserExist("friendUser");
        verify(obligationValidatorMock).validateAddObligation("alex", "friendUser", 50.0);
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionWhenIncorrectArguments() {
        String[] args = {"add-obligation", "friendUser"};
        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException for incorrect argument count.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionForInvalidAmount() {
        String[] args = {"add-obligation", "friendUser", "notANumber"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Smith"));

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException for non-numeric amount.");
    }

    @Test
    void testValidate_ThrowsExceptionIfNotAuthenticated() {
        String[] args = {"add-obligation", "friendUser", "50"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(false);

        assertThrows(UserNotAuthenticatedException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException when user is not authenticated.");
    }

    @Test
    void testValidate_ThrowsExceptionIfUserDoesNotExist() {
        String[] args = {"add-obligation", "friendUser", "50"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Smith"));

        doThrow(new InvalidCommandException("User does not exist")).when(userValidatorMock)
            .validateUserExist("friendUser");

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException if user does not exist.");
    }
}
