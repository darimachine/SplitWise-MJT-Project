package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.friend;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.FriendshipValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.nio.channels.SocketChannel;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.CANNOT_REMOVE_SELF_AS_FRIEND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_REMOVE_FRIEND_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.NON_EMPTY_COMMANDS_MESSAGE;

public class RemoveFriendCommandValidator extends AbstractCommandValidator {

    private static final int REQUIRED_ARGUMENTS = 2; // "remove-friend <username>"
    private final FriendshipValidator friendshipValidator;

    public RemoveFriendCommandValidator(AuthenticationManager authManager,
                                        FriendshipValidator friendshipValidator) {
        super(authManager);
        this.friendshipValidator = friendshipValidator;
    }

    @Override
    public void validate(String[] args, SocketChannel clientChannel) {
        validateArguments(args, REQUIRED_ARGUMENTS, INVALID_REMOVE_FRIEND_COMMAND_MESSAGE);
        validateArgumentsNull(args, NON_EMPTY_COMMANDS_MESSAGE);
        validateAuthentication(clientChannel);

        String loggedInUser = authManager.getAuthenticatedUser(clientChannel).getUsername();
        String friendUsername = args[1];

        if (loggedInUser.equals(friendUsername)) {
            throw new InvalidCommandException(CANNOT_REMOVE_SELF_AS_FRIEND_MESSAGE);
        }

        friendshipValidator.validateUserExists(friendUsername);
        friendshipValidator.validateFriendshipExists(loggedInUser, friendUsername);
        friendshipValidator.validateRemoveFriendShipIfThereIsObligationsBetweenThem(loggedInUser, friendUsername);
    }
}