package bg.sofia.uni.fmi.mjt.splitwise.server.command.group;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AddFriendToGroupCommandTest {

    private AddFriendToGroupCommand command;
    private AuthenticationManager authManagerMock;
    private UserServiceAPI userServiceMock;
    private GroupServiceAPI groupServiceMock;
    private NotificationServiceAPI notificationServiceMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;
    private User mockUser;
    private User friendUser;

    private static final String LOGGED_USER = "john_doe";
    private static final String FRIEND_USERNAME = "alice";
    private static final String GROUP_NAME = "Dinner Group";
    private static final String[] VALID_ARGS = {"add-friend-to-group", GROUP_NAME, FRIEND_USERNAME};

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        userServiceMock = mock(UserServiceAPI.class);
        groupServiceMock = mock(GroupServiceAPI.class);
        notificationServiceMock = mock(NotificationServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        mockUser = mock(User.class);
        friendUser = mock(User.class);

        command = new AddFriendToGroupCommand(authManagerMock, userServiceMock, groupServiceMock, notificationServiceMock, validatorMock);
    }

    @Test
    void testExecute_ValidatorIsCalled() {
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);
        when(mockUser.getUsername()).thenReturn(LOGGED_USER);
        when(userServiceMock.getUser(FRIEND_USERNAME)).thenReturn(friendUser);

        command.execute(VALID_ARGS, clientChannelMock);

        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
    }

    @Test
    void testExecute_SuccessfullyAddsFriendToGroup() {
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);
        when(mockUser.getUsername()).thenReturn(LOGGED_USER);
        when(userServiceMock.getUser(FRIEND_USERNAME)).thenReturn(friendUser);

        String expectedMessage = "User alice was successfully added to group Dinner Group";

        String result = command.execute(VALID_ARGS, clientChannelMock);

        verify(groupServiceMock).addUserToGroup(GROUP_NAME, FRIEND_USERNAME);
        verify(friendUser).addGroup(GROUP_NAME);
        verify(userServiceMock).saveAll();
        verify(notificationServiceMock).addNotification(
            "User john_doe added you to group: Dinner Group",
            FRIEND_USERNAME);

        assertEquals(expectedMessage, result, "Returned message should confirm the user was added.");
    }

    @Test
    void testExecute_CorrectlyFormatsNotificationMessage() {
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);
        when(mockUser.getUsername()).thenReturn(LOGGED_USER);
        when(userServiceMock.getUser(FRIEND_USERNAME)).thenReturn(friendUser);

        command.execute(VALID_ARGS, clientChannelMock);

        verify(notificationServiceMock).addNotification(
            "User john_doe added you to group: Dinner Group",
            FRIEND_USERNAME);
    }

    @Test
    void testExecute_ThrowsExceptionForInvalidArguments() {
        String[] invalidArgs = {"add-friend-to-group", GROUP_NAME}; // Missing friend's username

        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);
        when(mockUser.getUsername()).thenReturn(LOGGED_USER);

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> command.execute(invalidArgs, clientChannelMock),
            "Should throw ArrayIndexOutOfBoundsException for missing arguments.");
    }
}
