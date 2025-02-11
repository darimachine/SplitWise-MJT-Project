package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.currency;

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

class GetCurrentCurrencyCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private GetCurrentCurrencyCommandValidator validator;
    private SocketChannel mockChannel;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        validator = new GetCurrentCurrencyCommandValidator(authManagerMock);
        mockChannel = mock(SocketChannel.class);
    }

    @Test
    void testValidate_ValidRequest_DoesNotThrow() {
        String[] validArgs = {"current-currency"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(true);

        assertDoesNotThrow(() -> validator.validate(validArgs, mockChannel),
            "Should not throw when a valid command is provided and user is authenticated.");
    }

    @Test
    void testValidate_ThrowsIfNotAuthenticated() {
        String[] validArgs = {"current-currency"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(false);

        assertThrows(UserNotAuthenticatedException.class,
            () -> validator.validate(validArgs, mockChannel),
            "Should throw UserNotAuthenticatedException when user is not authenticated.");
    }

    @Test
    void testValidate_ThrowsIfIncorrectArguments() {
        String[] tooManyArgs = {"current-currency", "extraArg"};

        assertThrows(InvalidCommandException.class,
            () -> validator.validate(tooManyArgs, mockChannel),
            "Should throw InvalidCommandException when extra arguments are provided.");
    }

    @Test
    void testValidate_ThrowsIfEmptyArguments() {
        String[] emptyArgs = {""};

        assertThrows(InvalidCommandException.class,
            () -> validator.validate(emptyArgs, mockChannel),
            "Should throw InvalidCommandException when arguments contain an empty string.");
    }

    @Test
    void testValidate_ThrowsIfNullArguments() {
        String[] nullArgs = {null};

        assertThrows(InvalidCommandException.class,
            () -> validator.validate(nullArgs, mockChannel),
            "Should throw InvalidCommandException when arguments contain null.");
    }
}
