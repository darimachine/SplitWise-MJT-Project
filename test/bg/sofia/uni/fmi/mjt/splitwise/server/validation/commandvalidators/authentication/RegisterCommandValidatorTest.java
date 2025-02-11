package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.authentication;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserAlreadyAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserAlreadyExistException;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_PASSWORD_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.channels.SocketChannel;

class RegisterCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private UserValidator userValidatorMock;
    private RegisterCommandValidator registerValidator;
    private SocketChannel mockChannel;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        userValidatorMock = mock(UserValidator.class);
        registerValidator = new RegisterCommandValidator(authManagerMock, userValidatorMock);
        mockChannel = mock(SocketChannel.class);
    }

    @Test
    void testValidate_ValidArguments_DoesNotThrow() {
        String[] validArgs = {"register", "newUser", "ValidPass123", "John", "Doe"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(false);

        assertDoesNotThrow(() -> registerValidator.validate(validArgs, mockChannel));
    }

    @Test
    void testValidate_ThrowsIfUserAlreadyLoggedIn() {
        String[] validArgs = {"register", "newUser", "ValidPass123", "John", "Doe"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(true);

        assertThrows(UserAlreadyAuthenticatedException.class,
            () -> registerValidator.validate(validArgs, mockChannel),
            "Should throw UserAlreadyAuthenticatedException when the user is already logged in.");
    }

    @Test
    void testValidate_ThrowsIfArgumentsAreNullOrEmpty() {
        String[] invalidArgs = {"register", "newUser", "", "John", "Doe"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(false);

        assertThrows(InvalidCommandException.class,
            () -> registerValidator.validate(invalidArgs, mockChannel),
            "Should throw InvalidCommandException when any argument is empty or null.");
    }

    @Test
    void testValidate_ThrowsIfUsernameAlreadyExists() {
        String[] validArgs = {"register", "existingUser", "ValidPass123", "John", "Doe"};
        doThrow(new UserAlreadyExistException("User already exists"))
            .when(userValidatorMock).validateUserDoesNotExists("existingUser");

        assertThrows(UserAlreadyExistException.class,
            () -> registerValidator.validate(validArgs, mockChannel),
            "Should throw UserAlreadyExistException when username already exists.");
    }

    @Test
    void testValidate_ThrowsIfNotEnoughArguments() {
        String[] shortArgs = {"register", "newUser"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(false);

        assertThrows(InvalidCommandException.class,
            () -> registerValidator.validate(shortArgs, mockChannel),
            "Should throw InvalidCommandException when the argument length is incorrect.");
    }
    @Test
    void testValidate_UserAlreadyAuthenticatedExceptionThrown() {
        String[] validArgs = {"register", "newUser", "ValidPass123", "John", "Doe"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(true);

        assertThrows(UserAlreadyAuthenticatedException.class,
            () -> registerValidator.validate(validArgs, mockChannel),
            "Should throw UserAlreadyAuthenticatedException when the user is already logged in.");
    }
    @Test
    void testValidate_ValidPassword_DoesNotThrowException() {
        String[] args = {"register", "validUser", "Valid123", "First", "Last"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(false);

        assertDoesNotThrow(() -> registerValidator.validate(args, mockChannel),
            "Valid password should not throw an exception.");
    }

    @Test
    void testValidate_PasswordWithoutUppercase_ThrowsInvalidPasswordException() {
        String[] args = {"register", "user1", "password1", "John", "Doe"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(false);

       assertThrows(InvalidPasswordException.class,
            () -> registerValidator.validate(args, mockChannel));
    }

    @Test
    void testValidate_PasswordWithoutLowercase_ThrowsInvalidPasswordException() {
        String[] args = {"register", "user2", "PASSWORD1", "Alice", "Smith"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(false);

        assertThrows(InvalidPasswordException.class,
            () -> registerValidator.validate(args, mockChannel));
    }

    @Test
    void testValidate_PasswordWithoutNumbers_ThrowsInvalidPasswordException() {
        String[] args = {"register", "user3", "Password", "Bob", "Miller"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(false);
        assertThrows(InvalidPasswordException.class,
            () -> registerValidator.validate(args, mockChannel));
    }

    @Test
    void testValidate_PasswordTooShort_ThrowsInvalidPasswordException() {
        String[] args = {"register", "user4", "A1b!", "Charlie", "Brown"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(false);

       assertThrows(InvalidPasswordException.class,
            () -> registerValidator.validate(args, mockChannel));
    }

    @Test
    void testValidate_PasswordOnlyNumbersSpecialChars_ThrowsInvalidPasswordException() {
        String[] args = {"register", "user5", "12345678!", "Eve", "Davis"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(false);
        assertThrows(InvalidPasswordException.class,
            () -> registerValidator.validate(args, mockChannel));

    }
}
