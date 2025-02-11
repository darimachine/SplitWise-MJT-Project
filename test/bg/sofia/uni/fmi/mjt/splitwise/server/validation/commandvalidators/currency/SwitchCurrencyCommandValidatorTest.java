package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.currency;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCurrencyException;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class SwitchCurrencyCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private SwitchCurrencyCommandValidator validator;
    private SocketChannel mockChannel;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        validator = new SwitchCurrencyCommandValidator(authManagerMock);
        mockChannel = mock(SocketChannel.class);
    }

    @Test
    void testValidate_ValidCurrency_DoesNotThrow() {
        String[] validArgs = {"switch-currency", "EUR"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(true);

        assertDoesNotThrow(() -> validator.validate(validArgs, mockChannel),
            "Should not throw when a valid currency command is provided and user is authenticated.");
    }

    @Test
    void testValidate_ThrowsIfNotAuthenticated() {
        String[] validArgs = {"switch-currency", "USD"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(false);

        assertThrows(UserNotAuthenticatedException.class,
            () -> validator.validate(validArgs, mockChannel),
            "Should throw UserNotAuthenticatedException when user is not authenticated.");
    }

    @Test
    void testValidate_ThrowsIfInvalidCurrency() {
        String[] invalidCurrencyArgs = {"switch-currency", "XYZ"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(true);

        assertThrows(InvalidCurrencyException.class,
            () -> validator.validate(invalidCurrencyArgs, mockChannel),
            "Should throw InvalidCurrencyException when an invalid currency is provided.");
    }

    @Test
    void testValidate_ThrowsIfIncorrectArguments() {
        String[] tooManyArgs = {"switch-currency", "EUR", "extraArg"};

        assertThrows(InvalidCommandException.class,
            () -> validator.validate(tooManyArgs, mockChannel),
            "Should throw InvalidCommandException when extra arguments are provided.");
    }

    @Test
    void testValidate_ThrowsIfEmptyArguments() {
        String[] emptyArgs = {"switch-currency", ""};

        assertThrows(InvalidCommandException.class,
            () -> validator.validate(emptyArgs, mockChannel),
            "Should throw InvalidCommandException when arguments contain an empty string.");
    }

    @Test
    void testValidate_ThrowsIfNullArguments() {
        String[] nullArgs = {"switch-currency", null};

        assertThrows(InvalidCommandException.class,
            () -> validator.validate(nullArgs, mockChannel),
            "Should throw InvalidCommandException when arguments contain null.");
    }
}
