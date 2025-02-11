package bg.sofia.uni.fmi.mjt.splitwise.server.command.group;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;

import java.nio.channels.SocketChannel;
import java.util.Set;

public class ShowUserInWhichGroupsParticipateCommand implements Command {

    private final UserServiceAPI userService;
    private final CommandValidator validator;
    private static final int USERNAME_INDEX = 1;

    public ShowUserInWhichGroupsParticipateCommand(UserServiceAPI userService, CommandValidator validator) {
        this.userService = userService;
        this.validator = validator;
    }

    @Override
    public String execute(String[] arguments, SocketChannel clientChannel) {
        validator.validate(arguments, clientChannel);
        String username = arguments[USERNAME_INDEX];
        Set<String> groups = userService.getUser(username).getGroups();

        return groups.isEmpty()
            ? String.format("User '%s' does not participate in any groups.", username)
            : String.format("User '%s' participates in the following groups: [%s]",
            username, String.join(", ", groups));
    }
}
