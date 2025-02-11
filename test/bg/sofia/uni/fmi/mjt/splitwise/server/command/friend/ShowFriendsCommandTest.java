package bg.sofia.uni.fmi.mjt.splitwise.server.command.friend;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShowFriendsCommandTest {

    private ShowFriendsCommand command;
    private AuthenticationManager authManagerMock;
    private ObligationServiceAPI obligationServiceMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;
    private User mockUser;

    private static final String USERNAME = "john_doe";
    private static final Set<String> FRIENDS = Set.of("alice", "bob");
    private static final String FRIEND_OBLIGATIONS = "Alice owes you 10.00 EUR\nYou owe Bob 5.00 EUR";
    private static final String[] VALID_ARGS = {"my-friends"};

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        obligationServiceMock = mock(ObligationServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        mockUser = mock(User.class);

        command = new ShowFriendsCommand(authManagerMock, obligationServiceMock, validatorMock);
    }

    @Test
    void testExecute_ValidatorIsCalled() {
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);
        when(mockUser.getUsername()).thenReturn(USERNAME);
        when(mockUser.getFriends()).thenReturn(FRIENDS);
        when(obligationServiceMock.getMyFriendsObligations(USERNAME, FRIENDS)).thenReturn(FRIEND_OBLIGATIONS);

        command.execute(VALID_ARGS, clientChannelMock);

        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
    }

    @Test
    void testExecute_SuccessfullyRetrievesFriendsObligations() {
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);
        when(mockUser.getUsername()).thenReturn(USERNAME);
        when(mockUser.getFriends()).thenReturn(FRIENDS);
        when(obligationServiceMock.getMyFriendsObligations(USERNAME, FRIENDS)).thenReturn(FRIEND_OBLIGATIONS);

        String result = command.execute(VALID_ARGS, clientChannelMock);

        assertEquals(FRIEND_OBLIGATIONS, result, "Returned message should match the friend obligations.");
    }

    @Test
    void testExecute_UserHasNoFriends_ReturnsEmptyObligationMessage() {
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);
        when(mockUser.getUsername()).thenReturn(USERNAME);
        when(mockUser.getFriends()).thenReturn(Set.of());
        when(obligationServiceMock.getMyFriendsObligations(USERNAME, Set.of())).thenReturn("No friend obligations.");

        String result = command.execute(VALID_ARGS, clientChannelMock);

        assertEquals("No friend obligations.", result, "Should return a message indicating no friend obligations.");
    }
}
