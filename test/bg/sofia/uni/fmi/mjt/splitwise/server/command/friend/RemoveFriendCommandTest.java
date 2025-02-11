package bg.sofia.uni.fmi.mjt.splitwise.server.command.friend;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipServiceAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RemoveFriendCommandTest {

    private RemoveFriendCommand command;
    private AuthenticationManager authManagerMock;
    private FriendshipServiceAPI friendshipServiceMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;

    private static final String USERNAME = "john_doe";
    private static final String FRIEND = "jane_smith";
    private static final String[] VALID_ARGS = {"remove-friend", FRIEND};

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        friendshipServiceMock = mock(FriendshipServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);

        command = new RemoveFriendCommand(authManagerMock, friendshipServiceMock, validatorMock);
    }

    @Test
    void testExecute_ValidatorIsCalled() {
        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn(USERNAME);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);

        command.execute(VALID_ARGS, clientChannelMock);
        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
    }

    @Test
    void testExecute_RemovesFriendSuccessfully() {
        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn(USERNAME);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);

        String result = command.execute(VALID_ARGS, clientChannelMock);

        verify(friendshipServiceMock).removeFriend(USERNAME, FRIEND);
        assertEquals("Successfully removed " + FRIEND + " from your friend list!", result);
    }

    @Test
    void testExecute_FriendDoesNotExist() {
        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn(USERNAME);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);

        doThrow(new IllegalArgumentException("Friend not found")).when(friendshipServiceMock)
            .removeFriend(USERNAME, FRIEND);

        assertThrows(IllegalArgumentException.class,
            () -> command.execute(VALID_ARGS, clientChannelMock));
        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
    }
}
