package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.notification;

import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_SHOW_NEW_NOTIFICATIONS_COMMAND_MESSAGE;

public class ShowNewNotificationsCommandValidator extends AbstractCommandValidator {

    private static final int EXPECTED_ARGUMENTS = 1; // "notifications"

    public ShowNewNotificationsCommandValidator(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    public void validate(String[] args) {
        validateArguments(args, EXPECTED_ARGUMENTS, INVALID_SHOW_NEW_NOTIFICATIONS_COMMAND_MESSAGE);
        validateAuthentication();
    }
}
