package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.friend;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators.ObligationValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators.UserValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_ADD_OBLIGATION_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_AMOUNT_MESSAGE;

public class AddObligationWithFriendCommandValidator extends AbstractCommandValidator {

    private static final int EXPECTED_ARGUMENTS = 3; // add-obligation <username> <amount>

    private final UserValidator userValidator;
    private final ObligationValidator obligationValidator;

    public AddObligationWithFriendCommandValidator(AuthenticationManager authManager,
                                                   UserValidator userValidator,
                                                   ObligationValidator obligationValidator) {
        super(authManager);
        this.userValidator = userValidator;
        this.obligationValidator = obligationValidator;
    }

    @Override
    public void validate(String[] args) {
        validateArguments(args, EXPECTED_ARGUMENTS, INVALID_ADD_OBLIGATION_COMMAND_MESSAGE);
        validateAuthentication();

        String currentUser = authManager.getAuthenticatedUser().getUsername();
        String friendUsername = args[1];
        userValidator.validateUserExist(friendUsername);
        String amountStr = args[2];
        try {
            double amount = Double.parseDouble(amountStr);
            obligationValidator.validateAddObligation(currentUser, friendUsername, amount);
        } catch (NumberFormatException e) {
            throw new InvalidCommandException(INVALID_AMOUNT_MESSAGE);
        }
    }

}
