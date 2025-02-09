package bg.sofia.uni.fmi.mjt.splitwise.server.command.friend;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipServiceAPI;

public class AreFriendsCommand implements Command {

    private static final int FIRST_USER_INDEX = 1;
    private static final int SECOND_USER_INDEX = 2;
    private final FriendshipServiceAPI friendshipService;
    private final CommandValidator validator;

    public AreFriendsCommand(FriendshipServiceAPI friendshipService,
                             CommandValidator validator) {
        this.friendshipService = friendshipService;
        this.validator = validator;
    }

    @Override
    public String execute(String[] arguments) {
        validator.validate(arguments);
        String username = arguments[FIRST_USER_INDEX];
        String friendUsername = arguments[SECOND_USER_INDEX];
        boolean areFriends = friendshipService.areFriends(username, friendUsername);
        if (areFriends) {
            return "User: " + username + " and user: " + friendUsername + " are friends.";
        } else {
            return "User: " + username + " and user: " + friendUsername + " are not friends.";
        }
    }
}
