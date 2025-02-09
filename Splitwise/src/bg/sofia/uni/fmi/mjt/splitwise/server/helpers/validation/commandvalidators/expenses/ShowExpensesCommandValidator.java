package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.expenses;

import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_MY_EXPENSES_COMMAND_MESSAGE;

public class ShowExpensesCommandValidator extends AbstractCommandValidator {

    private static final int EXPECTED_ARGUMENTS = 1; // Only "my-expenses"

    public ShowExpensesCommandValidator(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    public void validate(String[] args) {
        validateArguments(args, EXPECTED_ARGUMENTS, INVALID_MY_EXPENSES_COMMAND_MESSAGE);
        validateAuthentication();
    }
}