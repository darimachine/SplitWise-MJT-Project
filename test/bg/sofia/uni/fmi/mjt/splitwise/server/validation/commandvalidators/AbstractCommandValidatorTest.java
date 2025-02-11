package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserAlreadyAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class AbstractCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private AbstractCommandValidator commandValidator;
    private SocketChannel mockChannel;

    // Creating a concrete subclass for testing
    private static class TestCommandValidator extends AbstractCommandValidator {
        protected TestCommandValidator(AuthenticationManager authManager) {
            super(authManager);
        }

        @Override
        public void validate(String[] args, SocketChannel clientChannel) {
            // Not needed for testing
        }
    }

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        commandValidator = new TestCommandValidator(authManagerMock);
        mockChannel = mock(SocketChannel.class);
    }

    @Test
    void testValidateAuthentication_ThrowsExceptionIfNotAuthenticated() {
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(false);

        assertThrows(UserNotAuthenticatedException.class,
            () -> commandValidator.validateAuthentication(mockChannel));
    }

    @Test
    void testValidateAuthentication_DoesNotThrowIfAuthenticated() {
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(true);

        assertDoesNotThrow(() -> commandValidator.validateAuthentication(mockChannel),
            "Should not throw when user is authenticated.");
    }

    @Test
    void testValidateUserNotLogged_ThrowsExceptionIfUserAlreadyAuthenticated() {
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(true);
        assertThrows(UserAlreadyAuthenticatedException.class,
            () -> commandValidator.validateUserNotLogged(mockChannel));
    }

    @Test
    void testValidateUserNotLogged_DoesNotThrowIfUserNotAuthenticated() {
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(false);

        assertDoesNotThrow(() -> commandValidator.validateUserNotLogged(mockChannel),
            "Should not throw when user is not authenticated.");
    }

    @Test
    void testValidateArguments_ThrowsExceptionIfWrongLength() {
        String[] args = {"arg1", "arg2"};
        String errorMessage = "Invalid number of arguments";

        InvalidCommandException exception = assertThrows(
            InvalidCommandException.class,
            () -> commandValidator.validateArguments(args, 3, errorMessage)
        );

        assertEquals(errorMessage, exception.getMessage(),
            "Should throw InvalidCommandException when arguments count is incorrect.");
    }

    @Test
    void testValidateArguments_DoesNotThrowIfCorrectLength() {
        String[] args = {"arg1", "arg2"};

        assertDoesNotThrow(() -> commandValidator.validateArguments(args, 2, "Invalid args"),
            "Should not throw when the arguments count is correct.");
    }

    @Test
    void testValidateMinArguments_ThrowsExceptionIfLessThanMin() {
        String[] args = {"arg1"};
        String errorMessage = "Not enough arguments";

        InvalidCommandException exception = assertThrows(
            InvalidCommandException.class,
            () -> commandValidator.validateMinArguments(args, 2, errorMessage)
        );

        assertEquals(errorMessage, exception.getMessage(),
            "Should throw InvalidCommandException when arguments are below the required minimum.");
    }

    @Test
    void testValidateMinArguments_DoesNotThrowIfEnoughArguments() {
        String[] args = {"arg1", "arg2"};

        assertDoesNotThrow(() -> commandValidator.validateMinArguments(args, 2, "Invalid args"),
            "Should not throw when arguments meet the minimum requirement.");
    }

    @Test
    void testValidateArgumentsNull_ThrowsExceptionIfAnyNullOrBlank() {
        String[] args = {"valid", " ", null};
        String errorMessage = "Arguments cannot be null or blank";

        InvalidCommandException exception = assertThrows(
            InvalidCommandException.class,
            () -> commandValidator.validateArgumentsNull(args, errorMessage)
        );

        assertEquals(errorMessage, exception.getMessage(),
            "Should throw InvalidCommandException when an argument is null or blank.");
    }

    @Test
    void testValidateArgumentsNull_DoesNotThrowIfAllValid() {
        String[] args = {"valid", "anotherValid"};

        assertDoesNotThrow(() -> commandValidator.validateArgumentsNull(args, "Invalid args"),
            "Should not throw when all arguments are valid.");
    }
}
