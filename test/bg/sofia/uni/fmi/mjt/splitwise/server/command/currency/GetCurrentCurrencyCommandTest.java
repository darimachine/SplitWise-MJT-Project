package bg.sofia.uni.fmi.mjt.splitwise.server.command.currency;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.model.enums.Currency;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetCurrentCurrencyCommandTest {

    private GetCurrentCurrencyCommand command;
    private AuthenticationManager authManagerMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;
    private User userMock;

    private static final String[] VALID_ARGS = {"current-currency"};

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        userMock = mock(User.class);

        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(userMock);
        when(userMock.getPreferredCurrency()).thenReturn(Currency.EUR);

        command = new GetCurrentCurrencyCommand(authManagerMock, validatorMock);
    }

    @Test
    void testExecute_ReturnsPreferredCurrency() {
        String result = command.execute(VALID_ARGS, clientChannelMock);

        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
        verify(authManagerMock).getAuthenticatedUser(clientChannelMock);
        verify(userMock).getPreferredCurrency();

        assertEquals("Your prefered currency is : EUR", result,
            "Command should return the correct preferred currency.");
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
}
