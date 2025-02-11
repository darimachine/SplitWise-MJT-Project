package bg.sofia.uni.fmi.mjt.splitwise.server.command.friend;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.servicerecords.ObligationDirection;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;

import java.nio.channels.SocketChannel;

public class FindObligationWithFriendCommand implements Command {
    private final AuthenticationManager authManager;
    private final ObligationServiceAPI obligationService;
    private final CommandValidator validator;
    private static final int FRIEND_USERNAME_INDEX = 1;

    public FindObligationWithFriendCommand(AuthenticationManager authManager, ObligationServiceAPI obligationService,
                                           CommandValidator validator) {
        this.authManager = authManager;
        this.obligationService = obligationService;
        this.validator = validator;
    }

    @Override
    public String execute(String[] arguments, SocketChannel clientChannel) {
        validator.validate(arguments, clientChannel);
        String friendUsername = arguments[FRIEND_USERNAME_INDEX];
        String loggedUsername = authManager.getAuthenticatedUser(clientChannel).getUsername();
        ObligationDirection obligation = obligationService.findObligationBetweenUsers(loggedUsername, friendUsername);

        return obligation.toString();
    }
}
