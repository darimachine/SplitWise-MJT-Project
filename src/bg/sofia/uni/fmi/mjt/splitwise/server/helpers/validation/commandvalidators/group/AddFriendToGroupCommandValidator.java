package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.group;

import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators.GroupValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators.UserValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_ADD_FRIEND_TO_GROUP_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.NON_EMPTY_COMMANDS_MESSAGE;

public class AddFriendToGroupCommandValidator extends AbstractCommandValidator {

    private static final int EXPECTED_ARGUMENTS = 3; // add-friend-to-group <group_name> <friend_username>
    private final GroupValidator groupValidator;
    private final UserValidator userValidator;

    public AddFriendToGroupCommandValidator(AuthenticationManager authManager,
                                            GroupValidator groupValidator,
                                            UserValidator userValidator) {
        super(authManager);
        this.groupValidator = groupValidator;
        this.userValidator = userValidator;
    }

    @Override
    public void validate(String[] args) {
        validateArguments(args, EXPECTED_ARGUMENTS, INVALID_ADD_FRIEND_TO_GROUP_COMMAND_MESSAGE);
        validateArgumentsNull(args, NON_EMPTY_COMMANDS_MESSAGE);
        validateAuthentication();

        String groupName = args[1];
        String friendUsername = args[2];
        String currentUser = authManager.getAuthenticatedUser().getUsername();

        userValidator.validateUserExist(friendUsername);
        groupValidator.validateGroupExists(groupName);
        groupValidator.validateUserInsideGroup(currentUser, groupName);
        groupValidator.validateUserNotInGroup(groupName, friendUsername);
        groupValidator.validateUserNotInGroup(groupName, friendUsername);
    }
}