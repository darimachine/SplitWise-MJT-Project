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

class ShowAllNotificationsCommandTest {

    private static final String USERNAME = "user1";
    private NotificationServiceAPI notificationServiceMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;
    private ShowAllNotificationsCommand command;

    @BeforeEach
    void setUp() {
        AuthenticationManager authManagerMock = mock(AuthenticationManager.class);
        notificationServiceMock = mock(NotificationServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);

        command = new ShowAllNotificationsCommand(authManagerMock, notificationServiceMock, validatorMock);

        User userMock = mock(User.class);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(userMock);
        when(userMock.getUsername()).thenReturn(USERNAME);
    }

    @Test
    void testExecute_ValidArguments_ReturnsFormattedNotifications() {
        List<Notification> notifications = List.of(
            new Notification("Notification 1"),
            new Notification("Notification 2")
        );

        when(notificationServiceMock.getAllNotificationsForUser(USERNAME)).thenReturn(notifications);
        when(notificationServiceMock.getNotificationsToString(notifications)).thenReturn("Notification 1\nNotification 2");

        String[] validArgs = {"all-notifications"};
        String result = command.execute(validArgs, clientChannelMock);

        verify(validatorMock).validate(validArgs, clientChannelMock);
        verify(notificationServiceMock).getAllNotificationsForUser(USERNAME);
        verify(notificationServiceMock).getNotificationsToString(notifications);

        assertEquals("Notification 1\nNotification 2", result, "Should return formatted notifications.");
    }

    @Test
    void testExecute_NoNotifications_ReturnsEmptyMessage() {
        List<Notification> emptyList = List.of();

        when(notificationServiceMock.getAllNotificationsForUser(USERNAME)).thenReturn(emptyList);
        when(notificationServiceMock.getNotificationsToString(emptyList)).thenReturn("No notifications.");

        String[] validArgs = {"all-notifications"};
        String result = command.execute(validArgs, clientChannelMock);

        verify(validatorMock).validate(validArgs, clientChannelMock);
        verify(notificationServiceMock).getAllNotificationsForUser(USERNAME);
        verify(notificationServiceMock).getNotificationsToString(emptyList);

        assertEquals("No notifications.", result, "Should return 'No notifications.' if no notifications exist.");
    }
}
