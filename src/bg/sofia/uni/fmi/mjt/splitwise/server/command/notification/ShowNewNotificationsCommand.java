package bg.sofia.uni.fmi.mjt.splitwise.server.command.notification;

import bg.sofia.uni.fmi.mjt.splitwise.model.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;

import java.nio.channels.SocketChannel;
import java.util.List;

public class ShowNewNotificationsCommand implements Command {
    private final AuthenticationManager authManager;
    private final NotificationServiceAPI notificationService;
    private final CommandValidator validator;

    public ShowNewNotificationsCommand(AuthenticationManager authManager, NotificationServiceAPI notificationService,
                                       CommandValidator validator) {
        this.authManager = authManager;
        this.notificationService = notificationService;
        this.validator = validator;
    }

    @Override
    public String execute(String[] arguments, SocketChannel clientChannel) {
        validator.validate(arguments, clientChannel);
        String username = authManager.getAuthenticatedUser(clientChannel).getUsername();
        List<Notification> unseenNotifications = notificationService.getUnseenNotificationsForUser(username);
        return notificationService.getNotificationsToString(unseenNotifications);
    }
}
