package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.notification;

import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.NotificationValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.nio.channels.SocketChannel;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_SHOW_ALL_NOTIFICATIONS_COMMAND_MESSAGE;

public class ShowAllNotificationsCommandValidator extends AbstractCommandValidator {

    private final NotificationValidator notificationValidator;
    private static final int EXPECTED_ARGUMENTS = 1; // "all-notifications"

    public ShowAllNotificationsCommandValidator(AuthenticationManager authManager,
                                                NotificationValidator notificationValidator) {
        super(authManager);
        this.notificationValidator = notificationValidator;
    }

    @Override
    public void validate(String[] args, SocketChannel clientChannel) {
        validateArguments(args, EXPECTED_ARGUMENTS, INVALID_SHOW_ALL_NOTIFICATIONS_COMMAND_MESSAGE);
        validateAuthentication(clientChannel);
        notificationValidator.validateAllNotificationExists(
            authManager.getAuthenticatedUser(clientChannel).getUsername());
    }
}