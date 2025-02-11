package bg.sofia.uni.fmi.mjt.splitwise.server.command.currency;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCurrencyException;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.model.enums.Currency;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SwitchCurrencyCommandTest {

    private SwitchCurrencyCommand command;
    private AuthenticationManager authManagerMock;
    private UserServiceAPI userServiceMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;
    private User userMock;

    private static final String[] VALID_ARGS = {"switch-currency", "USD"};
    private static final String[] INVALID_ARGS = {"switch-currency", "INVALID"};

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        userServiceMock = mock(UserServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        userMock = mock(User.class);

        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(userMock);
        command = new SwitchCurrencyCommand(authManagerMock, userServiceMock, validatorMock);
    }

    @Test
    void testExecute_SuccessfulCurrencySwitch() {
        String result = command.execute(VALID_ARGS, clientChannelMock);

        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
        verify(authManagerMock).getAuthenticatedUser(clientChannelMock);
        verify(userMock).setCurrency(Currency.USD);
        verify(userServiceMock).saveAll();

        assertEquals("Successfully switched currency to USD!", result,
            "Should confirm successful currency switch.");
    }

    @Test
    void testExecute_InvalidCurrency_ThrowsException() {
        doThrow(IllegalArgumentException.class).when(userMock).setCurrency(any());

        assertThrows(InvalidCurrencyException.class, () -> command.execute(INVALID_ARGS, clientChannelMock),
            "Should throw IllegalArgumentException for an invalid currency.");
    }

    @Test
    void testExecute_ValidatorIsCalled() {
        command.execute(VALID_ARGS, clientChannelMock);
        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
    }

    @Test
    void testExecute_AuthenticationManagerIsCalled() {
        command.execute(VALID_ARGS, clientChannelMock);
        verify(authManagerMock).getAuthenticatedUser(clientChannelMock);
    }

    @Test
    void testExecute_UserServiceSaveAllIsCalled() {
        command.execute(VALID_ARGS, clientChannelMock);
        verify(userServiceMock).saveAll();
    }
}
