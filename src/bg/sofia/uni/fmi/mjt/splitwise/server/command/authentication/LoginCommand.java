package bg.sofia.uni.fmi.mjt.splitwise.server.command.authentication;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.splitwise.model.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;

import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.stream.Collectors;

public class LoginCommand implements Command {
    private final AuthenticationManager authManager;
    private final CommandValidator validator;
    private final NotificationServiceAPI notificationService;
    private static final int USERNAME_INDEX = 1;
    private static final int PASSWORD_INDEX = 2;

    public LoginCommand(AuthenticationManager authManager, NotificationServiceAPI notificationService,
                        CommandValidator validator) {
        this.authManager = authManager;
        this.validator = validator;
        this.notificationService = notificationService;
    }

    @Override
    public String execute(String[] arguments, SocketChannel clientChannel) {
        validator.validate(arguments, clientChannel);
        String username = arguments[USERNAME_INDEX];
        String password = arguments[PASSWORD_INDEX];
        authenticateUser(clientChannel, username, password);
        String notifications = getUnseenNotifications(username);
        return buildWelcomeMessage(username, notifications, clientChannel);
    }

    private void authenticateUser(SocketChannel clientChannel, String username, String password) {
        if (!authManager.login(clientChannel, username, password)) {
            throw new InvalidPasswordException("Invalid password, try again!");
        }
    }

    private String getUnseenNotifications(String username) {
        List<Notification> unSeenNotifications = notificationService.getUnseenNotificationsForUser(username);
        notificationService.markNotificationsAsSeen(username, unSeenNotifications);
        return unSeenNotifications.stream()
            .map(Notification::toString)
            .collect(Collectors.joining(System.lineSeparator()));
    }

    private String buildWelcomeMessage(String username, String notifications, SocketChannel clientChannel) {
        return "Welcome, " + username + "!" +
            System.lineSeparator() +
            (notifications.isEmpty() ? "No new notifications" : notifications) +
            System.lineSeparator() +
            "Your preferred currency is: " +
            authManager.getAuthenticatedUser(clientChannel).getPreferredCurrency().getCurrency();
    }
}