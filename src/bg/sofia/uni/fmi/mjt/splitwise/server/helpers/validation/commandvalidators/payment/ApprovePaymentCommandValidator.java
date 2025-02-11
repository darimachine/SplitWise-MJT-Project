package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.payment;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators.ObligationValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators.UserValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_APPROVE_PAYMENT_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_PAYMENT_AMOUNT_MESSAGE;

public class ApprovePaymentCommandValidator extends AbstractCommandValidator {

    private static final int EXPECTED_ARGUMENTS = 3; // payed <username> <amount>

    private final ObligationValidator obligationValidator;
    private final UserValidator userValidator;

    public ApprovePaymentCommandValidator(AuthenticationManager authManager,
                                          ObligationValidator obligationValidator,
                                          UserValidator userValidator) {
        super(authManager);
        this.obligationValidator = obligationValidator;
        this.userValidator = userValidator;
    }

    @Override
    public void validate(String[] args) {
        validateArguments(args, EXPECTED_ARGUMENTS, INVALID_APPROVE_PAYMENT_COMMAND_MESSAGE);
        validateAuthentication();

        String loggedInUser = authManager.getAuthenticatedUser().getUsername();
        String targetUser = args[1];
        String amountStr = args[2];

        userValidator.validateUserExist(targetUser);

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            obligationValidator.validateAddObligation(loggedInUser, targetUser, amount);
        } catch (NumberFormatException e) {
            throw new InvalidCommandException(INVALID_PAYMENT_AMOUNT_MESSAGE);
        }

        obligationValidator.validateObligationExists(targetUser, loggedInUser);
        obligationValidator.validatePaymentDoesNotExceedObligation(targetUser, loggedInUser, amount);
    }
}