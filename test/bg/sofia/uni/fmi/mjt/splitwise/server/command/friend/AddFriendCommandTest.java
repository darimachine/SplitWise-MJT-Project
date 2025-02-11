package bg.sofia.uni.fmi.mjt.splitwise.server.command.friend;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AddFriendCommandTest {

    private AddFriendCommand command;
    private AuthenticationManager authManagerMock;
    private FriendshipServiceAPI friendshipServiceMock;
    private NotificationServiceAPI notificationServiceMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;
    private User userMock;

    private static final String[] VALID_ARGS = {"add-friend", "friendUser"};
    private static final String CURRENT_USER = "testUser";
    private static final String FRIEND_USER = "friendUser";

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        friendshipServiceMock = mock(FriendshipServiceAPI.class);
        notificationServiceMock = mock(NotificationServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        userMock = mock(User.class);

        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(userMock);
        when(userMock.getUsername()).thenReturn(CURRENT_USER);

        command = new AddFriendCommand(authManagerMock, friendshipServiceMock, notificationServiceMock, validatorMock);
    }

    @Test
    void testExecute_SuccessfullyAddsFriend() {
        String result = command.execute(VALID_ARGS, clientChannelMock);

        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
        verify(authManagerMock).getAuthenticatedUser(clientChannelMock);
        verify(friendshipServiceMock).addFriend(CURRENT_USER, FRIEND_USER);
        verify(notificationServiceMock).addNotification(CURRENT_USER + " added you as a friend!", FRIEND_USER);

        assertEquals("Successfully added " + FRIEND_USER + " to your friend list!", result,
            "The success message should be returned.");
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
    void testExecute_FriendshipServiceIsCalled() {
        command.execute(VALID_ARGS, clientChannelMock);
        verify(friendshipServiceMock).addFriend(CURRENT_USER, FRIEND_USER);
    }

    @Test
    void testExecute_NotificationServiceIsCalled() {
        command.execute(VALID_ARGS, clientChannelMock);
        verify(notificationServiceMock).addNotification(CURRENT_USER + " added you as a friend!", FRIEND_USER);
    }
}
