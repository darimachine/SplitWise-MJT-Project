package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.notification;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.notification.NotificationNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.NotificationValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShowAllNotificationsCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private NotificationValidator notificationValidatorMock;
    private ShowAllNotificationsCommandValidator validator;
    private SocketChannel clientChannelMock;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        notificationValidatorMock = mock(NotificationValidator.class);
        validator = new ShowAllNotificationsCommandValidator(authManagerMock, notificationValidatorMock);
        clientChannelMock = mock(SocketChannel.class);
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfInvalidArguments() {
        String[] invalidArgs = {}; // Empty arguments
        assertThrows(InvalidCommandException.class, () -> validator.validate(invalidArgs, clientChannelMock),
            "Should throw IllegalArgumentException when arguments are missing.");
    }

    @Test
    void testValidate_ThrowsUserNotAuthenticatedExceptionIfNotLoggedIn() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(false);
        String[] args = {"all-notifications"};

        assertThrows(UserNotAuthenticatedException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw UserNotAuthenticatedException if user is not logged in.");
    }

    @Test
    void testValidate_ThrowsNotificationNotFoundExceptionIfNoNotificationsExist() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));
        doThrow(new NotificationNotFoundException("No notifications found"))
            .when(notificationValidatorMock).validateAllNotificationExists("alex");

        String[] args = {"all-notifications"};

        assertThrows(NotificationNotFoundException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw NotificationNotFoundException when there are no notifications.");
    }

    @Test
    void testValidate_DoesNotThrowIfValidArguments() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        String[] args = {"all-notifications"};

        assertDoesNotThrow(() -> validator.validate(args, clientChannelMock),
            "Should not throw an exception if all arguments are valid.");
    }
}
