package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.authentication;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LogOutCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private LogOutCommandValidator logOutValidator;
    private SocketChannel mockChannel;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        logOutValidator = new LogOutCommandValidator(authManagerMock);
        mockChannel = mock(SocketChannel.class);
    }

    @Test
    void testValidate_ValidLogout_DoesNotThrow() {
        String[] validArgs = {"logout"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(true);

        assertDoesNotThrow(() -> logOutValidator.validate(validArgs, mockChannel),
            "Should not throw when a valid logout request is made.");
    }

    @Test
    void testValidate_ThrowsIfNotAuthenticated() {
        String[] validArgs = {"logout"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(false);

        assertThrows(UserNotAuthenticatedException.class,
            () -> logOutValidator.validate(validArgs, mockChannel),
            "Should throw UserNotAuthenticatedException when user is not authenticated.");
    }

    @Test
    void testValidate_ThrowsIfIncorrectArguments() {
        String[] tooManyArgs = {"logout", "extraArg"};

        assertThrows(InvalidCommandException.class,
            () -> logOutValidator.validate(tooManyArgs, mockChannel),
            "Should throw InvalidCommandException when extra arguments are provided.");
    }

    @Test
    void testValidate_ThrowsIfArgumentsAreNull() {
        String[] nullArgs = {null};

        assertThrows(InvalidCommandException.class,
            () -> logOutValidator.validate(nullArgs, mockChannel),
            "Should throw InvalidCommandException when arguments contain null.");
    }

    @Test
    void testValidate_ThrowsIfEmptyCommand() {
        String[] emptyArgs = {""};

        assertThrows(InvalidCommandException.class,
            () -> logOutValidator.validate(emptyArgs, mockChannel),
            "Should throw InvalidCommandException when arguments contain an empty string.");
    }
}
