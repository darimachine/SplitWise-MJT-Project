package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.group;

import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.GroupValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.UserValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.nio.channels.SocketChannel;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.CANNOT_REMOVE_SELF_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_REMOVE_FRIEND_FROM_GROUP_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.NON_EMPTY_COMMANDS_MESSAGE;

public class RemoveFriendFromGroupCommandValidator extends AbstractCommandValidator {

    private static final int EXPECTED_ARGUMENTS = 3; // remove-friend-from-group <group_name> <friend_username>
    private final GroupValidator groupValidator;
    private final UserValidator userValidator;

    public RemoveFriendFromGroupCommandValidator(AuthenticationManager authManager,
                                                 GroupValidator groupValidator,
                                                 UserValidator userValidator) {
        super(authManager);
        this.groupValidator = groupValidator;
        this.userValidator = userValidator;
    }

    @Override
    public void validate(String[] args, SocketChannel clientChannel) {
        validateArguments(args, EXPECTED_ARGUMENTS, INVALID_REMOVE_FRIEND_FROM_GROUP_COMMAND_MESSAGE);
        validateArgumentsNull(args, NON_EMPTY_COMMANDS_MESSAGE);
        validateAuthentication(clientChannel);

        String groupName = args[1];
        String friendUsername = args[2];
        String currentUser = authManager.getAuthenticatedUser(clientChannel).getUsername();

        userValidator.validateUserExist(currentUser);
        userValidator.validateUserExist(friendUsername);
        groupValidator.validateGroupExists(groupName);
        groupValidator.validateUserInsideGroup(groupName, currentUser);
        groupValidator.validateUserInsideGroup(groupName, friendUsername);
        if (friendUsername.equals(currentUser)) {
            throw new IllegalArgumentException(CANNOT_REMOVE_SELF_MESSAGE);
        }
    }
}
