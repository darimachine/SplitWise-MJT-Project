package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.friend;

import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.FriendshipValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.nio.channels.SocketChannel;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_ARE_FRIENDS_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.NON_EMPTY_COMMANDS_MESSAGE;

public class AreFriendsCommandValidator extends AbstractCommandValidator {

    private static final int REQUIRED_ARGUMENTS = 3; // "are-friends <user1> <user2>"
    private final FriendshipValidator friendshipValidator;
    private static final int USER1_INDEX = 1;
    private static final int USER2_INDEX = 2;
    public AreFriendsCommandValidator(AuthenticationManager authManager, FriendshipValidator friendshipValidator) {
        super(authManager);
        this.friendshipValidator = friendshipValidator;
    }

    @Override
    public void validate(String[] args, SocketChannel clientChannel) {
        validateArguments(args, REQUIRED_ARGUMENTS, INVALID_ARE_FRIENDS_COMMAND_MESSAGE);
        validateArgumentsNull(args, NON_EMPTY_COMMANDS_MESSAGE);
        validateAuthentication(clientChannel);
        String user1 = args[USER1_INDEX];
        String user2 = args[USER2_INDEX];
        friendshipValidator.validateUserExists(user1);
        friendshipValidator.validateUserExists(user2);
    }
}
