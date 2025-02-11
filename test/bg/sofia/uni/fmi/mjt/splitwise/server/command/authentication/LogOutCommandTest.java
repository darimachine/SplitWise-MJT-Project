package bg.sofia.uni.fmi.mjt.splitwise.server.command.authentication;

import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


class LogOutCommandTest {

    private LogOutCommand logOutCommand;
    private AuthenticationManager authManagerMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;

    private static final String[] VALID_ARGS = {"logout"};

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        logOutCommand = new LogOutCommand(authManagerMock, validatorMock);
    }

    @Test
    void testExecute_SuccessfulLogout() {
        String result = logOutCommand.execute(VALID_ARGS, clientChannelMock);

        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
        verify(authManagerMock).logout(clientChannelMock);
        assertEquals("You have been successfully logged out.", result,
            "Logout message should match expected output.");
    }

    @Test
    void testExecute_ValidatorIsCalled() {
        logOutCommand.execute(VALID_ARGS, clientChannelMock);
        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
    }

    @Test
    void testExecute_LogoutMethodIsCalled() {
        logOutCommand.execute(VALID_ARGS, clientChannelMock);
        verify(authManagerMock).logout(clientChannelMock);
    }
}
