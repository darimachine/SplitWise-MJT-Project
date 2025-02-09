package bg.sofia.uni.fmi.mjt.splitwise.server.command.authentication;

import bg.sofia.uni.fmi.mjt.splitwise.model.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;

import java.util.List;
import java.util.stream.Collectors;

public class LoginCommand implements Command {
    private final CommandValidator validator;
    private final AuthenticationManager authManager;
    private final NotificationServiceAPI notificationService;
    private static final int USERNAME_INDEX = 1;
    private static final int PASS_INDEX = 2;

    public LoginCommand(AuthenticationManager authManager, NotificationServiceAPI notificationService,
                        CommandValidator validator) {
        this.validator = validator;
        this.authManager = authManager;
        this.notificationService = notificationService;
    }

    @Override
    public String execute(String[] arguments) {
        validator.validate(arguments);
        String username = arguments[USERNAME_INDEX];

        List<Notification> unSeenNotifications = notificationService.getUnseenNotificationsForUser(username);
        String notifications = unSeenNotifications.stream()
            .map(Notification::toString)
            .collect(Collectors.joining(System.lineSeparator()));

        return ("Welcome, " + username + "!"
            + System.lineSeparator() + notifications);
    }
}
