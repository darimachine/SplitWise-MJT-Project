package bg.sofia.uni.fmi.mjt.splitwise.server.command.friend;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;

import java.nio.channels.SocketChannel;

public class AddObligationWithFriendCommand implements Command {

    private final AuthenticationManager authManager;
    private final ObligationServiceAPI obligationService;
    private final NotificationServiceAPI notificationService;
    private final CommandValidator validator;
    private static final int FRIEND_USERNAME_INDEX = 1;
    private static final int AMOUNT_INDEX = 2;

    public AddObligationWithFriendCommand(AuthenticationManager authManager, ObligationServiceAPI obligationService,
                                          NotificationServiceAPI notificationService, CommandValidator validator) {
        this.authManager = authManager;
        this.obligationService = obligationService;
        this.notificationService = notificationService;
        this.validator = validator;
    }

    @Override
    public String execute(String[] arguments, SocketChannel clientChannel) {
        validator.validate(arguments, clientChannel);
        String loggedUsername = authManager.getAuthenticatedUser(clientChannel).getUsername();
        String friendUsername = arguments[FRIEND_USERNAME_INDEX];
        double amount = Double.parseDouble(arguments[AMOUNT_INDEX]);
        obligationService.addObligation(loggedUsername, friendUsername, amount);
        notificationService.addNotification(
                String.format("%s added an obligation of %s to you.", loggedUsername, amount), friendUsername);

        return "You added an obligation of " + amount + " to " + friendUsername + ".";
    }
}
