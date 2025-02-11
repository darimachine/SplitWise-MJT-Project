package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.group;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators.ExpenseValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators.GroupValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_AMOUNT_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_SPLIT_GROUP_COMMAND_MESSAGE;

public class SplitWithGroupCommandValidator extends AbstractCommandValidator {

    private static final int EXPECTED_ARGUMENTS = 4; // split-group <amount> <group_name> <reason>
    private static final int AMOUNT_INDEX = 1;
    private static final int GROUP_NAME_INDEX = 2;
    private static final int REASON_INDEX = 3;
    private final ExpenseValidator expenseValidator;
    private final GroupValidator groupValidator;

    public SplitWithGroupCommandValidator(AuthenticationManager authManager,
                                          ExpenseValidator expenseValidator,
                                          GroupValidator groupValidator) {
        super(authManager);
        this.expenseValidator = expenseValidator;
        this.groupValidator = groupValidator;
    }

    @Override
    public void validate(String[] args) {
        validateArguments(args, EXPECTED_ARGUMENTS, INVALID_SPLIT_GROUP_COMMAND_MESSAGE);
        validateAuthentication();

        String amountStr = args[AMOUNT_INDEX];
        String groupName = args[GROUP_NAME_INDEX];
        String reason = args[REASON_INDEX];
        String currentUser = authManager.getAuthenticatedUser().getUsername();
        groupValidator.validateGroupExists(groupName);
        groupValidator.validateUserInsideGroup(currentUser, groupName);
        try {
            double amount = Double.parseDouble(amountStr);
            expenseValidator.validateExpenseInputOnGroups(currentUser, amount, reason);
        } catch (NumberFormatException e) {
            throw new InvalidCommandException(INVALID_AMOUNT_MESSAGE);
        }
    }
}