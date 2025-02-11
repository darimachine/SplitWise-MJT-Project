package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.friend;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.ObligationValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.UserValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.nio.channels.SocketChannel;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_ADD_OBLIGATION_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_AMOUNT_MESSAGE;

public class AddObligationWithFriendCommandValidator extends AbstractCommandValidator {

    private static final int EXPECTED_ARGUMENTS = 3; // add-obligation <username> <amount>

    private final UserValidator userValidator;
    private final ObligationValidator obligationValidator;
    private static final int FRIEND_USERNAME_INDEX = 1;
    private static final int AMOUNT_INDEX = 2;
    public AddObligationWithFriendCommandValidator(AuthenticationManager authManager,
                                                   UserValidator userValidator,
                                                   ObligationValidator obligationValidator) {
        super(authManager);
        this.userValidator = userValidator;
        this.obligationValidator = obligationValidator;
    }

    @Override
    public void validate(String[] args, SocketChannel clientChannel) {
        validateArguments(args, EXPECTED_ARGUMENTS, INVALID_ADD_OBLIGATION_COMMAND_MESSAGE);
        validateAuthentication(clientChannel);

        String currentUser = authManager.getAuthenticatedUser(clientChannel).getUsername();
        String friendUsername = args[FRIEND_USERNAME_INDEX];
        userValidator.validateUserExist(friendUsername);
        String amountStr = args[AMOUNT_INDEX];
        try {
            double amount = Double.parseDouble(amountStr);
            obligationValidator.validateAddObligation(currentUser, friendUsername, amount);
        } catch (NumberFormatException e) {
            throw new InvalidCommandException(INVALID_AMOUNT_MESSAGE);
        }
    }

}
