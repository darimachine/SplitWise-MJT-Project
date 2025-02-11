package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.friend;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.ExpenseValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.FriendshipValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.nio.channels.SocketChannel;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_AMOUNT_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_SPLIT_COMMAND_MESSAGE;

public class SplitWithFriendCommandValidator extends AbstractCommandValidator {

    private static final int EXPECTED_ARGUMENTS = 4; // split <amount> <username> <reason>
    private static final int AMOUNT_INDEX = 1;
    private static final int FRIEND_USERNAME_INDEX = 2;
    private static final int REASON_INDEX = 3;
    private final ExpenseValidator expenseValidator;
    private final FriendshipValidator friendshipValidator;

    public SplitWithFriendCommandValidator(AuthenticationManager authManager,
                                           ExpenseValidator expenseValidator,
                                           FriendshipValidator friendshipValidator) {
        super(authManager);
        this.expenseValidator = expenseValidator;
        this.friendshipValidator = friendshipValidator;
    }

    @Override
    public void validate(String[] args, SocketChannel clientChannel) {
        validateArguments(args, EXPECTED_ARGUMENTS, INVALID_SPLIT_COMMAND_MESSAGE);
        validateAuthentication(clientChannel);

        String amountStr = args[AMOUNT_INDEX];
        String friendUsername = args[FRIEND_USERNAME_INDEX];
        String reason = args[REASON_INDEX];
        String currentUser = authManager.getAuthenticatedUser(clientChannel).getUsername();

        friendshipValidator.validateUserExists(friendUsername);
        friendshipValidator.validateFriendshipExists(currentUser, friendUsername);
        try {
            double amount = Double.parseDouble(amountStr);
            expenseValidator.validateExpenseInputs(currentUser, amount, reason, Set.of(friendUsername));
        } catch (NumberFormatException e) {
            throw new InvalidCommandException(INVALID_AMOUNT_MESSAGE);
        }
    }
}