package bg.sofia.uni.fmi.mjt.splitwise.server.command.group;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;

import java.nio.channels.SocketChannel;
import java.util.Set;

public class RemoveFriendFromGroupCommand implements Command {
    private final AuthenticationManager authManager;
    private final UserServiceAPI userService;
    private final GroupServiceAPI groupService;
    private final NotificationServiceAPI notificationService;
    private final CommandValidator validator;
    private static final int GROUP_NAME_INDEX = 1;
    private static final int FRIEND_USERNAME_INDEX = 2;
    private static final int MINIMUM_GROUP_MEMBER = 3;
    public RemoveFriendFromGroupCommand(AuthenticationManager authManager, UserServiceAPI userService,
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

        Set<String> groupMembers = groupService.getGroupMembers(groupName);
        groupMembers.remove(friendUsername);
        userService.getUser(friendUsername).removeGroup(groupName);
        if (groupMembers.size() < MINIMUM_GROUP_MEMBER) {
            for (String member : groupMembers) {
                userService.getUser(member).getGroups().remove(groupName);
            }
            groupService.removeGroup(groupName);
        }
        userService.saveAll();
        notificationService.addNotification("User " + loggedUser + " removed you from group: " + groupName,
            friendUsername);
        return "You successfully removed user " + friendUsername + " from group " + groupName;
    }
}
