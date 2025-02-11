package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.friend;

import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators.FriendshipValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_SHOW_FRIENDS_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.NON_EMPTY_COMMANDS_MESSAGE;

public class ShowFriendsCommandValidator extends AbstractCommandValidator {

    private static final int REQUIRED_ARGUMENTS = 1; // Only "my-friends"
    private final FriendshipValidator friendshipValidator;

    public ShowFriendsCommandValidator(AuthenticationManager authManager,
                                       FriendshipValidator friendshipValidator) {
        super(authManager);
        this.friendshipValidator = friendshipValidator;
    }

    @Override
    public void validate(String[] args) {
        validateArguments(args, REQUIRED_ARGUMENTS, INVALID_SHOW_FRIENDS_COMMAND_MESSAGE);
        validateArgumentsNull(args, NON_EMPTY_COMMANDS_MESSAGE);
        validateAuthentication();

        String loggedInUser = authManager.getAuthenticatedUser().getUsername();
        friendshipValidator.validateUserExists(loggedInUser);
    }
}