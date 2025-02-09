package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import bg.sofia.uni.fmi.mjt.splitwise.model.Notification;

import java.util.List;

public interface NotificationServiceAPI {

    List<Notification> getAllNotificationsForUser(String username);

    List<Notification> getUnseenNotificationsForUser(String username);

    String getNotificationsToString(List<Notification> notifications);

    void markNotificationsAsSeen(String username, List<Notification> notifications);

    void addNotification(String message, String recipientUsername);

    void addNotification(String message, List<String> recipientsUsernames);
}
