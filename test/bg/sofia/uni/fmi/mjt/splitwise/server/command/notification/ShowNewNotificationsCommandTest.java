package bg.sofia.uni.fmi.mjt.splitwise.server.command.notification;

import bg.sofia.uni.fmi.mjt.splitwise.model.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShowNewNotificationsCommandTest {

    private static final String USERNAME = "user1";
    private NotificationServiceAPI notificationServiceMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;
    private ShowNewNotificationsCommand command;

    @BeforeEach
    void setUp() {
        AuthenticationManager authManagerMock = mock(AuthenticationManager.class);
        notificationServiceMock = mock(NotificationServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);

        command = new ShowNewNotificationsCommand(authManagerMock, notificationServiceMock, validatorMock);

        User userMock = mock(User.class);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(userMock);
        when(userMock.getUsername()).thenReturn(USERNAME);
    }

    @Test
    void testExecute_ValidArguments_ReturnsFormattedUnseenNotifications() {
        List<Notification> notifications = List.of(
            new Notification("New Expense Added"),
            new Notification("Friend Request Accepted")
        );

        when(notificationServiceMock.getUnseenNotificationsForUser(USERNAME)).thenReturn(notifications);
        when(notificationServiceMock.getNotificationsToString(notifications))
            .thenReturn("New Expense Added\nFriend Request Accepted");

        String[] validArgs = {"notifications"};
        String result = command.execute(validArgs, clientChannelMock);

        verify(validatorMock).validate(validArgs, clientChannelMock);
        verify(notificationServiceMock).getUnseenNotificationsForUser(USERNAME);
        verify(notificationServiceMock).getNotificationsToString(notifications);

        assertEquals("New Expense Added\nFriend Request Accepted", result,
            "Should return formatted unseen notifications.");
    }

    @Test
    void testExecute_NoNewNotifications_ReturnsEmptyMessage() {
        List<Notification> emptyList = List.of();

        when(notificationServiceMock.getUnseenNotificationsForUser(USERNAME)).thenReturn(emptyList);
        when(notificationServiceMock.getNotificationsToString(emptyList)).thenReturn("No new notifications.");

        String[] validArgs = {"notifications"};
        String result = command.execute(validArgs, clientChannelMock);

        verify(validatorMock).validate(validArgs, clientChannelMock);
        verify(notificationServiceMock).getUnseenNotificationsForUser(USERNAME);
        verify(notificationServiceMock).getNotificationsToString(emptyList);

        assertEquals("No new notifications.", result, "Should return 'No new notifications.' if none exist.");
    }
}
