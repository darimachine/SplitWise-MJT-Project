package bg.sofia.uni.fmi.mjt.splitwise.server.command.group;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RemoveFriendFromGroupCommandTest {

    private static final String LOGGED_USER = "loggedUser";
    private static final String FRIEND_USER = "friendUser";
    private static final String GROUP_NAME = "group1";
    private static final int MINIMUM_GROUP_MEMBER = 3;

    private AuthenticationManager authManagerMock;
    private UserServiceAPI userServiceMock;
    private GroupServiceAPI groupServiceMock;
    private NotificationServiceAPI notificationServiceMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;
    private RemoveFriendFromGroupCommand command;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        userServiceMock = mock(UserServiceAPI.class);
        groupServiceMock = mock(GroupServiceAPI.class);
        notificationServiceMock = mock(NotificationServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        command = new RemoveFriendFromGroupCommand(authManagerMock, userServiceMock, groupServiceMock, notificationServiceMock, validatorMock);
    }

    @Test
    void testExecute_SuccessfullyRemovesFriendFromGroup() {
        String[] args = {"remove-friend-from-group", GROUP_NAME, FRIEND_USER};

        User loggedUserMock = mock(User.class);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(loggedUserMock);
        when(loggedUserMock.getUsername()).thenReturn(LOGGED_USER);
        when(userServiceMock.getUser(LOGGED_USER)).thenReturn(loggedUserMock);

        User friendUserMock = mock(User.class);
        when(userServiceMock.getUser(FRIEND_USER)).thenReturn(friendUserMock);
        User member3Mock = mock(User.class);
        when(userServiceMock.getUser("member3")).thenReturn(member3Mock);

        Set<String> groups = new HashSet<>();
        groups.add(GROUP_NAME);
        when(friendUserMock.getGroups()).thenReturn(groups);

        Set<String> groupMembers = new HashSet<>(Set.of(LOGGED_USER, FRIEND_USER, "member3"));
        when(groupServiceMock.getGroupMembers(GROUP_NAME)).thenReturn(groupMembers);

        String result = command.execute(args, clientChannelMock);

        verify(validatorMock).validate(args, clientChannelMock);
        verify(userServiceMock).getUser(FRIEND_USER);
        verify(userServiceMock).saveAll();
        verify(notificationServiceMock).addNotification(
            "User " + LOGGED_USER + " removed you from group: " + GROUP_NAME, FRIEND_USER);

        assertEquals("You successfully removed user " + FRIEND_USER + " from group " + GROUP_NAME, result);
    }

    @Test
    void testExecute_RemovesGroupWhenMembersAreBelowThreshold() {
        String[] args = {"remove-friend-from-group", GROUP_NAME, FRIEND_USER};

        User loggedUserMock = mock(User.class);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(loggedUserMock);
        when(loggedUserMock.getUsername()).thenReturn(LOGGED_USER);

        User friendUserMock = mock(User.class);
        when(userServiceMock.getUser(FRIEND_USER)).thenReturn(friendUserMock);

        Set<String> groups = new HashSet<>();
        groups.add(GROUP_NAME);
        when(friendUserMock.getGroups()).thenReturn(groups);

        Set<String> groupMembers = new HashSet<>(Set.of(LOGGED_USER, FRIEND_USER)); // Only 2 members before removal
        when(groupServiceMock.getGroupMembers(GROUP_NAME)).thenReturn(groupMembers);
        when(userServiceMock.getUser(LOGGED_USER)).thenReturn(loggedUserMock);
        when(userServiceMock.getUser(FRIEND_USER)).thenReturn(friendUserMock);
        String result = command.execute(args, clientChannelMock);

        verify(groupServiceMock).removeGroup(GROUP_NAME);
        verify(userServiceMock).saveAll();
        assertEquals("You successfully removed user " + FRIEND_USER + " from group " + GROUP_NAME, result);
    }

    @Test
    void testExecute_ThrowsExceptionWhenUserNotAuthenticated() {
        String[] args = {"remove-friend-from-group", GROUP_NAME, FRIEND_USER};

        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> command.execute(args, clientChannelMock),
            "Should throw NullPointerException when user is not authenticated.");
    }

    @Test
    void testExecute_ThrowsExceptionWhenFriendNotFound() {
        String[] args = {"remove-friend-from-group", GROUP_NAME, FRIEND_USER};

        User loggedUserMock = mock(User.class);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(loggedUserMock);
        when(loggedUserMock.getUsername()).thenReturn(LOGGED_USER);

        when(userServiceMock.getUser(FRIEND_USER)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> command.execute(args, clientChannelMock),
            "Should throw NullPointerException when the friend user does not exist.");
    }

    @Test
    void testExecute_ThrowsExceptionWhenValidatorFails() {
        String[] args = {"remove-friend-from-group", GROUP_NAME, FRIEND_USER};

        doThrow(new IllegalArgumentException("Invalid arguments provided."))
            .when(validatorMock)
            .validate(args, clientChannelMock);

        assertThrows(IllegalArgumentException.class, () -> command.execute(args, clientChannelMock),
            "Should throw IllegalArgumentException when validation fails.");
    }
}
