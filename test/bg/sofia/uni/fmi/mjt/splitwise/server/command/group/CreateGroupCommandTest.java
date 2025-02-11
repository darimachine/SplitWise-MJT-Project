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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateGroupCommandTest {

    private CreateGroupCommand command;
    private GroupServiceAPI groupServiceMock;
    private NotificationServiceAPI notificationServiceMock;
    private UserServiceAPI userServiceMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;
    private User loggedUser;
    private User aliceUser;
    private User bobUser;

    private static final String LOGGED_USER = "john_doe";
    private static final String LOGGED_USER_FULL_NAME = "John Doe";
    private static final String ALICE_USERNAME = "alice";
    private static final String BOB_USERNAME = "bob";
    private static final String GROUP_NAME = "Trip Group";

    private static final String[] VALID_ARGS = {"create-group", GROUP_NAME, ALICE_USERNAME, BOB_USERNAME};
    private static final String[] INVALID_ARGS_FEW_USERS = {"create-group", GROUP_NAME, ALICE_USERNAME};

    @BeforeEach
    void setUp() {
        AuthenticationManager authManagerMock = mock(AuthenticationManager.class);
        groupServiceMock = mock(GroupServiceAPI.class);
        notificationServiceMock = mock(NotificationServiceAPI.class);
        userServiceMock = mock(UserServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        loggedUser = mock(User.class);
        aliceUser = mock(User.class);
        bobUser = mock(User.class);

        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(loggedUser);
        when(loggedUser.getUsername()).thenReturn(LOGGED_USER);
        when(loggedUser.getFullName()).thenReturn(LOGGED_USER_FULL_NAME);

        when(userServiceMock.getUser(LOGGED_USER)).thenReturn(loggedUser);
        when(userServiceMock.getUser(ALICE_USERNAME)).thenReturn(aliceUser);
        when(userServiceMock.getUser(BOB_USERNAME)).thenReturn(bobUser);

        command = new CreateGroupCommand(authManagerMock, groupServiceMock, notificationServiceMock, userServiceMock,
            validatorMock);
    }

    @Test
    void testExecute_ValidatorIsCalled() {
        command.execute(VALID_ARGS, clientChannelMock);
        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
    }

    @Test
    void testExecute_SuccessfullyCreatesGroup() {
        String expectedMessage = "Successfully created group Trip Group!";

        String result = command.execute(VALID_ARGS, clientChannelMock);

        verify(groupServiceMock).createGroup(GROUP_NAME, Set.of(LOGGED_USER, ALICE_USERNAME, BOB_USERNAME));
        verify(loggedUser).addGroup(GROUP_NAME);
        verify(aliceUser).addGroup(GROUP_NAME);
        verify(bobUser).addGroup(GROUP_NAME);
        verify(userServiceMock).saveAll();

        assertEquals(expectedMessage, result, "Returned message should confirm the group was created.");
    }

    @Test
    void testExecute_SendsNotificationsToAllMembers() {
        command.execute(VALID_ARGS, clientChannelMock);

        verify(notificationServiceMock).addNotification(
            "John Doe added you to group Trip Group", ALICE_USERNAME);
        verify(notificationServiceMock).addNotification(
            "John Doe added you to group Trip Group", BOB_USERNAME);
    }

    @Test
    void testExecute_ThrowsExceptionIfUsersLessThanThree() {
        doThrow(IllegalArgumentException.class)
            .when(validatorMock)
            .validate(INVALID_ARGS_FEW_USERS, clientChannelMock);

        assertThrows(IllegalArgumentException.class, () -> command.execute(INVALID_ARGS_FEW_USERS, clientChannelMock),
            "Should throw IllegalArgumentException when less than 3 users are provided.");
    }

    @Test
    void testExecute_ThrowsExceptionWhenUserDoesNotExist() {
        when(userServiceMock.getUser(BOB_USERNAME)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> command.execute(VALID_ARGS, clientChannelMock),
            "Should throw NullPointerException when a user does not exist.");
    }
}
