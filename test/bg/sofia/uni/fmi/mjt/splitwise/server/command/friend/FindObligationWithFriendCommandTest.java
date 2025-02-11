package bg.sofia.uni.fmi.mjt.splitwise.server.command.friend;

import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.servicerecords.ObligationDirection;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindObligationWithFriendCommandTest {

    private FindObligationWithFriendCommand command;
    private AuthenticationManager authManagerMock;
    private ObligationServiceAPI obligationServiceMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;

    private static final String USERNAME = "Alice";
    private static final String FRIEND = "Bob";
    private static final String CURRENCY = "USD";
    private static final double AMOUNT = 50.0;

    private static final String[] VALID_ARGS = {"find-obligation", FRIEND};

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        obligationServiceMock = mock(ObligationServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);

        command = new FindObligationWithFriendCommand(authManagerMock, obligationServiceMock, validatorMock);
    }

    @Test
    void testExecute_ObligationExists_ReturnsObligationDetails() {
        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn(USERNAME);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);

        ObligationDirection mockObligation = new ObligationDirection(USERNAME, FRIEND, AMOUNT, CURRENCY);
        when(obligationServiceMock.findObligationBetweenUsers(USERNAME, FRIEND)).thenReturn(mockObligation);

        String result = command.execute(VALID_ARGS, clientChannelMock);
        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
        verify(authManagerMock).getAuthenticatedUser(clientChannelMock);
        verify(obligationServiceMock).findObligationBetweenUsers(USERNAME, FRIEND);
        assertEquals("Alice owes Bob 50.00 USD", result, "Should return correct obligation details.");
    }

    @Test
    void testExecute_ValidatorIsCalled() {
        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn(USERNAME);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);

        ObligationDirection mockObligation = new ObligationDirection(USERNAME, FRIEND, AMOUNT, CURRENCY);
        when(obligationServiceMock.findObligationBetweenUsers(USERNAME, FRIEND)).thenReturn(mockObligation);

        command.execute(VALID_ARGS, clientChannelMock);
        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
    }

    @Test
    void testExecute_ObligationServiceIsCalledCorrectly() {
        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn(USERNAME);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);

        ObligationDirection mockObligation = new ObligationDirection(USERNAME, FRIEND, AMOUNT, CURRENCY);
        when(obligationServiceMock.findObligationBetweenUsers(USERNAME, FRIEND)).thenReturn(mockObligation);
        command.execute(VALID_ARGS, clientChannelMock);

        ArgumentCaptor<String> userCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> friendCaptor = ArgumentCaptor.forClass(String.class);
        verify(obligationServiceMock).findObligationBetweenUsers(userCaptor.capture(), friendCaptor.capture());

        assertEquals(USERNAME, userCaptor.getValue(), "First argument should be the logged-in user.");
        assertEquals(FRIEND, friendCaptor.getValue(), "Second argument should be the friend.");
    }
}
