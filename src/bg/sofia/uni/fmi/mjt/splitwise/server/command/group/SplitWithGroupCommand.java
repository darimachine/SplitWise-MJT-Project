package bg.sofia.uni.fmi.mjt.splitwise.server.command.group;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;

import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.stream.Collectors;

public class SplitWithGroupCommand implements Command {

    private final AuthenticationManager authManager;
    private final GroupServiceAPI groupService;
    private final ExpenseServiceAPI expenseService;
    private final NotificationServiceAPI notificationService;
    private final CommandValidator validator;
    private static final int AMOUNT_INDEX = 1;
    private static final int GROUP_INDEX = 2;
    private static final int DESCRIPTION_INDEX = 3;

    public SplitWithGroupCommand(AuthenticationManager authManager, GroupServiceAPI groupService,
                                 ExpenseServiceAPI expenseService,
                                 NotificationServiceAPI notificationService, CommandValidator validator) {
        this.authManager = authManager;
        this.expenseService = expenseService;
        this.groupService = groupService;
        this.notificationService = notificationService;
        this.validator = validator;
    }

    @Override
    public String execute(String[] arguments, SocketChannel clientChannel) {
        validator.validate(arguments, clientChannel);
        double amount = Double.parseDouble(arguments[AMOUNT_INDEX]);
        String groupName = arguments[GROUP_INDEX];
        String description = arguments[DESCRIPTION_INDEX];
        User loggedUser = authManager.getAuthenticatedUser(clientChannel);
        String loggedUserUsername = loggedUser.getUsername();
        Set<String> members = groupService.getGroupMembers(groupName).stream()
            .filter(member -> !member.equals(loggedUserUsername))
            .collect(Collectors.toSet());

        double splitAmount = amount / (members.size() + 1.00);
        expenseService.addFriendExpense(loggedUserUsername, amount, description, members);
        String message = String.format("%s (%s) paid %s (%s each) for group %s Reason: %s.", loggedUser.getFullName(),
            loggedUserUsername, amount, splitAmount, groupName, description);
        notificationService.addNotification(message, members);

        return String.format("You successfully split %.2f BGN with group %s. They owe you %.2f BGN.",
            amount, groupName, amount - splitAmount);
    }
}
