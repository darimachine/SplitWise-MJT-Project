package bg.sofia.uni.fmi.mjt.splitwise.server.command.group;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateGroupCommand implements Command {

    private final AuthenticationManager authManager;
    private final GroupServiceAPI groupService;
    private final NotificationServiceAPI notificationService;
    private final UserServiceAPI userService;
    private final CommandValidator validator;

    public CreateGroupCommand(AuthenticationManager authManager, GroupServiceAPI groupService,
                              NotificationServiceAPI notificationService,
                              UserServiceAPI userService,
                              CommandValidator validator) {
        this.authManager = authManager;
        this.groupService = groupService;
        this.notificationService = notificationService;
        this.userService = userService;
        this.validator = validator;
    }

    @Override
    public String execute(String[] arguments) {
        validator.validate(arguments);
        String groupName = arguments[1];
        Set<String> members = Arrays.stream(arguments).skip(2)
            .collect(Collectors.toSet());
        User loggedUser = authManager.getAuthenticatedUser();

        for (String member : members) {
            notificationService.addNotification(
                loggedUser.getFullName() + " added you to group " + groupName,
                member);
        }
        String creator = loggedUser.getUsername();
        members.add(creator);

        for (String member : members) {
            User user = userService.getUser(member);
            user.addGroup(groupName);
        }
        groupService.createGroup(groupName, members);
        userService.saveAll();
        return "Successfully created group " + groupName + "!";
    }
}
