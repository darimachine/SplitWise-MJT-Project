package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.notification.NotificationNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.model.Notification;

import java.util.List;
import java.util.Map;

public class NotificationValidator {

    public void validateNotificationExists(Map<String, List<Notification>> notifications, String username) {

        if (!notifications.containsKey(username) || notifications.get(username).isEmpty()) {
            throw new NotificationNotFoundException("No notifications found for user: " + username);
        }
    }
}
