package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.model.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.server.database.json.NotificationJsonProcessor;

import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//VALIDACII V OTDELEN KLAS I GI VIKAME OT COMMAND_PATTERNA
public class NotificationService implements NotificationServiceAPI {
    private final NotificationJsonProcessor processor;
    private final Map<String, List<Notification>> notifications;

    public NotificationService(NotificationJsonProcessor processor) {
        this.processor = processor;
        this.notifications = processor.loadData();
    }

    @Override
    public List<Notification> getAllNotificationsForUser(String username) {
        //validateUser(userService, username);
        return notifications.getOrDefault(username, List.of());
    }

    @Override
    public List<Notification> getUnseenNotificationsForUser(String username) {
        //validateUser(userService, username);
        List<Notification> currUserNotifications = notifications.getOrDefault(username, List.of());
        return currUserNotifications.stream()
            .filter(notification -> !notification.isSeen())
            .toList();
    }

    @Override
    public String getNotificationsToString(List<Notification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Notification notification : notifications) {
            sb.append(notification.toString()).append(System.lineSeparator());
        }

        return  "*** Notifications ***" + System.lineSeparator() + sb.toString();
    }

    @Override
    public void markNotificationsAsSeen(String username, List<Notification> notificationsToMark) {
        //validateUser(userService, username);
        if (notificationsToMark == null || notificationsToMark.isEmpty()) {
            return;
        }
        List<Notification> userNotifications = notifications.get(username);
        if (userNotifications == null || userNotifications.isEmpty()) {
            return;
        }
        for (Notification n : userNotifications) {
            if (notificationsToMark.contains(n)) {
                n.markAsSeen();
            }
        }
        processor.saveData(notifications);
    }

    @Override
    public void addNotification(String message, String recipientUsername) {
        //validateUser(userService, recipientUsername);
        Notification notification = new Notification(message);
        notifications.putIfAbsent(recipientUsername, new ArrayList<>());
        notifications.get(recipientUsername).add(notification);
        processor.saveData(notifications);
    }

    @Override
    public void addNotification(String message, List<String> recipientsUsernames) {
        if (recipientsUsernames == null || recipientsUsernames.isEmpty()) {
            throw new IllegalArgumentException("Recipients cannot be null or empty!");
        }
        for (String recipient : recipientsUsernames) {
            addNotification(message, recipient);
        }
    }

}
