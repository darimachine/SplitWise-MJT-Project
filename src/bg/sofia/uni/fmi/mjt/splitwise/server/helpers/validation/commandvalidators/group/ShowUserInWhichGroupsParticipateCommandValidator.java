package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.group;

import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators.UserValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_GET_USER_GROUPS_COMMAND_MESSAGE;

public class ShowUserInWhichGroupsParticipateCommandValidator extends AbstractCommandValidator {

    private final UserValidator userValidator;
    private static final int EXPECTED_ARGUMENTS = 2; // getUserGroups <username>

    public ShowUserInWhichGroupsParticipateCommandValidator(AuthenticationManager authManager,
                                                            UserValidator userValidator) {
        super(authManager);
        this.userValidator = userValidator;
    }

    @Override
    public void validate(String[] args) {
        validateArguments(args, EXPECTED_ARGUMENTS, INVALID_GET_USER_GROUPS_COMMAND_MESSAGE);
        validateAuthentication();
        String username = args[1];
        userValidator.validateUserExist(username);
    }
}
