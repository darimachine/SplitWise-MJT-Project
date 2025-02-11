package bg.sofia.uni.fmi.mjt.splitwise.server.command.group;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;

import java.nio.channels.SocketChannel;

public class ShowGroupMembersCommand implements Command {

    private final GroupServiceAPI groupService;
    private final CommandValidator validator;

    public ShowGroupMembersCommand(GroupServiceAPI groupService, CommandValidator validator) {
        this.groupService = groupService;
        this.validator = validator;
    }

    @Override
    public String execute(String[] arguments, SocketChannel clientChannel) {
        validator.validate(arguments, clientChannel);
        String groupName = arguments[1];

        return "👥 Members of group '" + groupName + "':\n- " +
            String.join("\n- ", groupService.getGroupMembers(groupName));
    }
}
