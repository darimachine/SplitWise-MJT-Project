package bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.notification.NotificationNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.model.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;

import java.util.List;

public class NotificationValidator {

    private final NotificationServiceAPI notificationService;

    public NotificationValidator(NotificationServiceAPI notificationService) {
        this.notificationService = notificationService;
    }

    public void validateAllNotificationExists(String username) {

        List<Notification> notifications = notificationService.getAllNotificationsForUser(username);
        if (notifications == null || notifications.isEmpty()) {
            throw new NotificationNotFoundException("No notifications found for user: " + username);
        }
    }

    public void validateUnseenNotificationExists(String username) {

        List<Notification> notifications = notificationService.getUnseenNotificationsForUser(username);
        if (notifications == null || notifications.isEmpty()) {
            throw new NotificationNotFoundException("No unseen notifications found for user: " + username);
        }
    }
}
