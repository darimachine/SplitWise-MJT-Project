package bg.sofia.uni.fmi.mjt.splitwise.server.command.group;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;

import java.nio.channels.SocketChannel;

public class AddFriendToGroupCommand implements Command {

    private final AuthenticationManager authManager;
    private final UserServiceAPI userService;
    private final GroupServiceAPI groupService;
    private final NotificationServiceAPI notificationService;
    private final CommandValidator validator;
    private static final int GROUP_NAME_INDEX = 1;
    private static final int FRIEND_USERNAME_INDEX = 2;

    public AddFriendToGroupCommand(AuthenticationManager authManager, UserServiceAPI userService,
                                   GroupServiceAPI groupService, NotificationServiceAPI notificationService,
                                   CommandValidator validator) {
        this.authManager = authManager;
        this.userService = userService;
        this.groupService = groupService;
        this.notificationService = notificationService;
        this.validator = validator;
    }

    @Override
    public String execute(String[] arguments, SocketChannel clientChannel) {
        validator.validate(arguments, clientChannel);
        String groupName = arguments[GROUP_NAME_INDEX];
        String friendUsername = arguments[FRIEND_USERNAME_INDEX];
        String loggedUser = authManager.getAuthenticatedUser(clientChannel).getUsername();
        groupService.addUserToGroup(groupName, friendUsername);
        userService.getUser(friendUsername).addGroup(groupName);
        userService.saveAll();
        notificationService.addNotification("User " + loggedUser + " added you to group: " + groupName,
            friendUsername);
        return "User " + friendUsername + " was successfully added to group " + groupName;
    }
}
