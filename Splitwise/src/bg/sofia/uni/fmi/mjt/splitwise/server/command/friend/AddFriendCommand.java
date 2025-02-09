package bg.sofia.uni.fmi.mjt.splitwise.server.command.friend;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;

public class AddFriendCommand implements Command {

    private final AuthenticationManager authManager;
    private final FriendshipServiceAPI frienshipService;
    private final NotificationServiceAPI notificationService;
    private final CommandValidator validator;

    public AddFriendCommand(AuthenticationManager authManager, FriendshipServiceAPI frienshipService,
                            NotificationServiceAPI notificationService, CommandValidator validator) {
        this.authManager = authManager;
        this.frienshipService = frienshipService;
        this.notificationService = notificationService;
        this.validator = validator;
    }

    @Override
    public String execute(String[] arguments) {
        validator.validate(arguments);
        String friendUsername = arguments[1];
        String username = authManager.getAuthenticatedUser().getUsername();
        frienshipService.addFriend(username, friendUsername);
        notificationService.addNotification(username + " added you as a friend!", friendUsername);
        return "Successfully added " + friendUsername + " to your friend list!";
    }
}
