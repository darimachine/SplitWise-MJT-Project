package bg.sofia.uni.fmi.mjt.splitwise.server.command.expenses;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseServiceAPI;

import java.nio.channels.SocketChannel;

public class ShowExpensesCommand implements Command {

    private final AuthenticationManager authManager;
    private final ExpenseServiceAPI expenseService;
    private final CommandValidator validator;

    public ShowExpensesCommand(AuthenticationManager authManager, ExpenseServiceAPI expenseService,
                               CommandValidator validator) {
        this.authManager = authManager;
        this.expenseService = expenseService;
        this.validator = validator;
    }

    @Override
    public String execute(String[] arguments, SocketChannel clientChannel) {
        validator.validate(arguments, clientChannel);
        String loggedUserUsername = authManager.getAuthenticatedUser(clientChannel).getUsername();
        return expenseService.getExpensesAsString(loggedUserUsername);
    }
}
