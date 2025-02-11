package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.group;

import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.AbstractCommandValidator;

import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators.GroupValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_GROUP_INFO_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.NON_EMPTY_COMMANDS_MESSAGE;

public class ShowGroupMembersCommandValidator extends AbstractCommandValidator {

    private static final int EXPECTED_ARGUMENTS = 2; // "group-info <group_name>"
    private final GroupValidator groupValidator;

    public ShowGroupMembersCommandValidator(AuthenticationManager authManager,
                                            GroupValidator groupValidator) {
        super(authManager);
        this.groupValidator = groupValidator;
    }

    @Override
    public void validate(String[] args) {
        validateArguments(args, EXPECTED_ARGUMENTS, INVALID_GROUP_INFO_COMMAND_MESSAGE);
        validateArgumentsNull(args, NON_EMPTY_COMMANDS_MESSAGE);
        validateAuthentication();

        String groupName = args[1];
        String username = authManager.getAuthenticatedUser().getUsername();
        groupValidator.validateGroupExists(groupName);
        groupValidator.validateUserInsideGroup(groupName, username);
    }

}