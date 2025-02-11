package bg.sofia.uni.fmi.mjt.splitwise.server.command.friend;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.model.enums.Currency;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;

import java.nio.channels.SocketChannel;
import java.util.Set;

public class SplitWithFriendCommand implements Command {

    private final AuthenticationManager authManager;
    private final ExpenseServiceAPI expenseService;
    private final NotificationServiceAPI notificationService;
    private final CommandValidator validator;
    private static final int AMOUNT_INDEX = 1;
    private static final int FRIEND_USERNAME_INDEX = 2;
    private static final int DESCRIPTION_INDEX = 3;

    public SplitWithFriendCommand(AuthenticationManager authManager, ExpenseServiceAPI expenseService,
                                  NotificationServiceAPI notificationService, CommandValidator validator) {
        this.authManager = authManager;
        this.expenseService = expenseService;
        this.notificationService = notificationService;
        this.validator = validator;
    }

    //rest api currency
    @Override
    public String execute(String[] arguments, SocketChannel clientChannel) {
        validator.validate(arguments, clientChannel);
        double amount = Double.parseDouble(arguments[AMOUNT_INDEX]);
        String friendUsername = arguments[FRIEND_USERNAME_INDEX];
        String description = arguments[DESCRIPTION_INDEX];
        User loggedUser = authManager.getAuthenticatedUser(clientChannel);
        String loggedUserUsername = loggedUser.getUsername();
        Currency loggedUserCurrency = loggedUser.getPreferredCurrency();
        expenseService.addFriendExpense(loggedUserUsername, amount, description, Set.of(friendUsername));
        String message = String.format("%s paid a total of %.2f %s for you (%.2f each). Reason: %s.",
            loggedUser.getFullName(),
            amount,
            loggedUserCurrency.getCurrency(),
            amount / 2,
            description);
        notificationService.addNotification(message, friendUsername);

        return String.format("You successfully split %.2f %s with %s. They owe you %.2f %s.",
            amount, loggedUserCurrency.getCurrency(), friendUsername, amount / 2, loggedUserCurrency.getCurrency());
    }
}
