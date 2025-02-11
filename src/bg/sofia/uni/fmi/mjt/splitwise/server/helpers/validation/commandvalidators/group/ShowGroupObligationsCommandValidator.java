package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.group;

import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators.UserValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.util.Set;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_MY_GROUPS_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.NON_EMPTY_COMMANDS_MESSAGE;

public class ShowGroupObligationsCommandValidator extends AbstractCommandValidator {

    private static final int EXPECTED_ARGUMENTS = 1; // Only "my-groups"
    private final UserValidator userValidator;

    public ShowGroupObligationsCommandValidator(AuthenticationManager authManager, UserValidator userValidator) {
        super(authManager);
        this.userValidator = userValidator;
    }

    @Override
    public void validate(String[] args) {
        validateArguments(args, EXPECTED_ARGUMENTS, INVALID_MY_GROUPS_COMMAND_MESSAGE);
        validateArgumentsNull(args, NON_EMPTY_COMMANDS_MESSAGE);
        validateAuthentication();

        Set<String> groups = authManager.getAuthenticatedUser().getGroups();
        userValidator.validateIfUserIsInsideAGroup(groups);
    }
}