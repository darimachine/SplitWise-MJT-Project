package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.expenses;

import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.nio.channels.SocketChannel;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_MY_EXPENSES_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.NON_EMPTY_COMMANDS_MESSAGE;

public class ShowExpensesCommandValidator extends AbstractCommandValidator {

    private static final int EXPECTED_ARGUMENTS = 1; // Only "my-expenses"

    public ShowExpensesCommandValidator(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    public void validate(String[] args, SocketChannel clientChannel) {
        validateArguments(args, EXPECTED_ARGUMENTS, INVALID_MY_EXPENSES_COMMAND_MESSAGE);
        validateArgumentsNull(args, NON_EMPTY_COMMANDS_MESSAGE);
        validateAuthentication(clientChannel);
    }
}