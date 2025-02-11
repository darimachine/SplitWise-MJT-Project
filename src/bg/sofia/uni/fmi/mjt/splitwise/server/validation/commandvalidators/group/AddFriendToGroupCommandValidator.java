package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.group;

import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.FriendshipValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.GroupValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.UserValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.nio.channels.SocketChannel;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_ADD_FRIEND_TO_GROUP_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.NON_EMPTY_COMMANDS_MESSAGE;

public class AddFriendToGroupCommandValidator extends AbstractCommandValidator {

    private static final int EXPECTED_ARGUMENTS = 3; // add-friend-to-group <group_name> <friend_username>
    private final GroupValidator groupValidator;
    private final UserValidator userValidator;
    private final FriendshipValidator friendshipValidator;

    private static final int GROUP_NAME_INDEX = 1;
    private static final int FRIEND_USERNAME_INDEX = 2;
    public AddFriendToGroupCommandValidator(AuthenticationManager authManager,
                                            GroupValidator groupValidator,
                                            UserValidator userValidator, FriendshipValidator friendshipValidator) {

        super(authManager);
        this.groupValidator = groupValidator;
        this.userValidator = userValidator;
        this.friendshipValidator = friendshipValidator;
    }

    @Override
    public void validate(String[] args, SocketChannel clientChannel) {
        validateArguments(args, EXPECTED_ARGUMENTS, INVALID_ADD_FRIEND_TO_GROUP_COMMAND_MESSAGE);
        validateArgumentsNull(args, NON_EMPTY_COMMANDS_MESSAGE);
        validateAuthentication(clientChannel);

        String groupName = args[GROUP_NAME_INDEX];
        String friendUsername = args[FRIEND_USERNAME_INDEX];
        String currentUser = authManager.getAuthenticatedUser(clientChannel).getUsername();
        friendshipValidator.validateFriendshipExists(currentUser, friendUsername);
        userValidator.validateUserExist(friendUsername);
        groupValidator.validateGroupExists(groupName);
        groupValidator.validateUserInsideGroup(groupName, currentUser);
        groupValidator.validateUserNotInGroup(groupName, friendUsername);
    }
}