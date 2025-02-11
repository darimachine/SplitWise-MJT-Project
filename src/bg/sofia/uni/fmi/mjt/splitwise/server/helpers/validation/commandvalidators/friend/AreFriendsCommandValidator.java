package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.friend;

import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators.FriendshipValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_ARE_FRIENDS_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.NON_EMPTY_COMMANDS_MESSAGE;

public class AreFriendsCommandValidator extends AbstractCommandValidator {

    private static final int REQUIRED_ARGUMENTS = 3; // "are-friends <user1> <user2>"
    private final FriendshipValidator friendshipValidator;

    public AreFriendsCommandValidator(AuthenticationManager authManager,
                                      FriendshipValidator friendshipValidator) {
        super(authManager);
        this.friendshipValidator = friendshipValidator;
    }

    @Override
    public void validate(String[] args) {
        validateArguments(args, REQUIRED_ARGUMENTS, INVALID_ARE_FRIENDS_COMMAND_MESSAGE);
        validateArgumentsNull(args, NON_EMPTY_COMMANDS_MESSAGE);
        validateAuthentication();

        String user1 = args[1];
        String user2 = args[2];

        friendshipValidator.validateUserExists(user1);
        friendshipValidator.validateUserExists(user2);
    }
}
