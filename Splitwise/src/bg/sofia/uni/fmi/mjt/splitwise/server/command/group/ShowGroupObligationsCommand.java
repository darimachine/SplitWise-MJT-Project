package bg.sofia.uni.fmi.mjt.splitwise.server.command.group;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    public String execute(String[] args) {
        validator.validate(args);
        User user = authManager.getAuthenticatedUser();
        Set<String> loggedUserGroups = user.getGroups();
        String username = user.getUsername();
        Map<String, Set<String>> groupsWhichUserParticipate = new HashMap<>();
        for (String group : loggedUserGroups) {
            Set<String> members = groupService.getGroupMembers(group);
            groupsWhichUserParticipate.put(group, members);
        }

        return obligationService.getMyGroupObligations(username,
            groupsWhichUserParticipate); // GroupName: [member1, member2, member3]
    }
}
