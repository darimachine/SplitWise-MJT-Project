package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.friend;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.FriendshipValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.nio.channels.SocketChannel;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.CANNOT_ADD_SELF_AS_FRIEND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_ADD_FRIEND_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.NON_EMPTY_COMMANDS_MESSAGE;

public class AddFriendCommandValidator extends AbstractCommandValidator {

    private static final int REQUIRED_ARGUMENTS = 2; // "add-friend <username>"
    private final FriendshipValidator friendshipValidator;
    public AddFriendCommandValidator(FriendshipValidator friendshipValidator,
                                     AuthenticationManager authManager) {
        super(authManager);
        this.friendshipValidator = friendshipValidator;
    }

    @Override
    public void validate(String[] args, SocketChannel clientChannel) {
        validateArguments(args, REQUIRED_ARGUMENTS, INVALID_ADD_FRIEND_COMMAND_MESSAGE);
        validateArgumentsNull(args, NON_EMPTY_COMMANDS_MESSAGE);
        validateAuthentication(clientChannel);

        String loggedInUser = authManager.getAuthenticatedUser(clientChannel).getUsername();
        String friendUsername = args[1];
        validateNotAddingSelf(loggedInUser, friendUsername);
        friendshipValidator.validateUserExists(friendUsername);
        friendshipValidator.validateFriendshipDoesNotExist(loggedInUser, friendUsername);
    }

    private void validateNotAddingSelf(String user, String friend) {
        if (user.equals(friend)) {
            throw new InvalidCommandException(CANNOT_ADD_SELF_AS_FRIEND_MESSAGE);
        }
    }
}