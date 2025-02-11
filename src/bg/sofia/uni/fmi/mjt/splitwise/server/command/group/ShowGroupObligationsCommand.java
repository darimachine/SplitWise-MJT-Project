package bg.sofia.uni.fmi.mjt.splitwise.server.command.group;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;

import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.stream.Collectors;

public class ShowGroupObligationsCommand implements Command {
    private final AuthenticationManager authManager;
    private final GroupServiceAPI groupService;
    private final ObligationServiceAPI obligationService;
    private final CommandValidator validator;

    public ShowGroupObligationsCommand(AuthenticationManager authManager, GroupServiceAPI groupService,
                                       ObligationServiceAPI obligationService,
                                       CommandValidator validator) {
        this.authManager = authManager;
        this.groupService = groupService;
        this.obligationService = obligationService;
        this.validator = validator;
    }

    @Override
    public String execute(String[] args, SocketChannel clientChannel) {
        validator.validate(args, clientChannel);
        User user = authManager.getAuthenticatedUser(clientChannel);
        Set<String> loggedUserGroups = user.getGroups();
        String username = user.getUsername();

        return obligationService.getMyGroupObligations(username, loggedUserGroups.stream()
            .collect(Collectors.toMap(group -> group,
                groupService::getGroupMembers))); // GroupName: [member1, member2, member3]
    }
}
