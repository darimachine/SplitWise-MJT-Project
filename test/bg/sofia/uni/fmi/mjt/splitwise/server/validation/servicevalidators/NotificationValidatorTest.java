package bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.notification.NotificationNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.model.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificationValidatorTest {

    private NotificationValidator notificationValidator;
    private NotificationServiceAPI notificationServiceMock;

    @BeforeEach
    void setUp() {
        notificationServiceMock = mock(NotificationServiceAPI.class);
        notificationValidator = new NotificationValidator(notificationServiceMock);
    }

    @Test
    void testValidateAllNotificationExists_Success() {
        List<Notification> mockNotifications = List.of(new Notification("You owe Alex 10 BGN"));

        when(notificationServiceMock.getAllNotificationsForUser("john")).thenReturn(mockNotifications);

        assertDoesNotThrow(() -> notificationValidator.validateAllNotificationExists("john"),
            "Should not throw if notifications exist for user");
    }

    @Test
    void testValidateAllNotificationExists_ThrowsNotificationNotFoundException_EmptyList() {
        when(notificationServiceMock.getAllNotificationsForUser("john")).thenReturn(List.of());

        assertThrows(NotificationNotFoundException.class,
            () -> notificationValidator.validateAllNotificationExists("john"),
            "Should throw NotificationNotFoundException if user has no notifications");
    }

    @Test
    void testValidateAllNotificationExists_ThrowsNotificationNotFoundException_NullList() {
        when(notificationServiceMock.getAllNotificationsForUser("john")).thenReturn(null);

        assertThrows(NotificationNotFoundException.class,
            () -> notificationValidator.validateAllNotificationExists("john"),
            "Should throw NotificationNotFoundException if user has no notifications (null case)");
    }

    @Test
    void testValidateUnseenNotificationExists_Success() {
        List<Notification> unseenNotifications = List.of(new Notification("New transaction alert"));

        when(notificationServiceMock.getUnseenNotificationsForUser("john")).thenReturn(unseenNotifications);

        assertDoesNotThrow(() -> notificationValidator.validateUnseenNotificationExists("john"),
            "Should not throw if unseen notifications exist for user");
    }

    @Test
    void testValidateUnseenNotificationExists_ThrowsNotificationNotFoundException_EmptyList() {
        when(notificationServiceMock.getUnseenNotificationsForUser("john")).thenReturn(List.of());

        assertThrows(NotificationNotFoundException.class,
            () -> notificationValidator.validateUnseenNotificationExists("john"),
            "Should throw NotificationNotFoundException if user has no unseen notifications");
    }

    @Test
    void testValidateUnseenNotificationExists_ThrowsNotificationNotFoundException_NullList() {
        when(notificationServiceMock.getUnseenNotificationsForUser("john")).thenReturn(null);

        assertThrows(NotificationNotFoundException.class,
            () -> notificationValidator.validateUnseenNotificationExists("john"),
            "Should throw NotificationNotFoundException if unseen notifications list is null");
    }
}
