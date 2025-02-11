package bg.sofia.uni.fmi.mjt.splitwise.server.command.friend;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;

import java.nio.channels.SocketChannel;
import java.util.Set;

public class ShowFriendsCommand implements Command {
    private final AuthenticationManager authManager;
    private final ObligationServiceAPI obligationService;
    private final CommandValidator validator;

    public ShowFriendsCommand(AuthenticationManager authManager, ObligationServiceAPI obligationService,
                              CommandValidator validator) {
        this.authManager = authManager;
        this.obligationService = obligationService;
        this.validator = validator;
    }

    @Override
    public String execute(String[] arguments, SocketChannel clientChannel) {
        validator.validate(arguments, clientChannel);
        User user = authManager.getAuthenticatedUser(clientChannel);
        String username = user.getUsername();
        Set<String> friends = user.getFriends();
        return obligationService.getMyFriendsObligations(username, friends);
    }
}
