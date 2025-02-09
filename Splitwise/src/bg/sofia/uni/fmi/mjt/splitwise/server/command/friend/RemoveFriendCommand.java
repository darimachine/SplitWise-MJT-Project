package bg.sofia.uni.fmi.mjt.splitwise.server.command.friend;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipServiceAPI;

public class RemoveFriendCommand implements Command {

    private final AuthenticationManager authManager;
    private final FriendshipServiceAPI friendshipService;
    private final CommandValidator validator;
    public RemoveFriendCommand(AuthenticationManager authManager, FriendshipServiceAPI friendshipService,
                               CommandValidator validator) {
        this.authManager = authManager;
        this.friendshipService = friendshipService;
        this.validator = validator;
    }

    @Override
    public String execute(String[] arguments) {
        validator.validate(arguments);
        String username = authManager.getAuthenticatedUser().getUsername();
        String friendUsername = arguments[1];
        friendshipService.removeFriend(username, friendUsername);
        return "Successfully removed " + friendUsername + " from your friend list!";
    }
}
