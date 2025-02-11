package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.authentication;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserAlreadyAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoginCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private UserValidator userValidatorMock;
    private LoginCommandValidator loginValidator;
    private SocketChannel mockChannel;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        userValidatorMock = mock(UserValidator.class);
        loginValidator = new LoginCommandValidator(authManagerMock, userValidatorMock);
        mockChannel = mock(SocketChannel.class);
    }

    @Test
    void testValidate_ValidLogin_DoesNotThrow() {
        String[] validArgs = {"login", "existingUser", "ValidPass123"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(false);

        assertDoesNotThrow(() -> loginValidator.validate(validArgs, mockChannel));
    }

    @Test
    void testValidate_ThrowsIfUserAlreadyLoggedIn() {
        String[] validArgs = {"login", "existingUser", "ValidPass123"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(true);

        assertThrows(UserAlreadyAuthenticatedException.class,
            () -> loginValidator.validate(validArgs, mockChannel),
            "Should throw UserAlreadyAuthenticatedException when the user is already logged in.");
    }

    @Test
    void testValidate_ThrowsIfArgumentsAreNullOrEmpty() {
        String[] invalidArgs = {"login", "", "ValidPass123"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(false);

        assertThrows(InvalidCommandException.class,
            () -> loginValidator.validate(invalidArgs, mockChannel),
            "Should throw InvalidCommandException when any argument is empty or null.");
    }

    @Test
    void testValidate_ThrowsIfUsernameDoesNotExist() {
        String[] validArgs = {"login", "nonExistingUser", "ValidPass123"};
        doThrow(new InvalidCommandException("User does not exist"))
            .when(userValidatorMock).validateUserExist("nonExistingUser");

        assertThrows(InvalidCommandException.class,
            () -> loginValidator.validate(validArgs, mockChannel),
            "Should throw InvalidCommandException when username does not exist.");
    }

    @Test
    void testValidate_ThrowsIfNotEnoughArguments() {
        String[] shortArgs = {"login", "onlyUsername"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(false);

        assertThrows(InvalidCommandException.class,
            () -> loginValidator.validate(shortArgs, mockChannel),
            "Should throw InvalidCommandException when there are not enough arguments.");
    }
}
