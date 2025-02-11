package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.friend;

import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.ObligationValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.UserValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.nio.channels.SocketChannel;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_FIND_OBLIGATION_COMMAND_MESSAGE;

public class FindObligationWithFriendCommandValidator extends AbstractCommandValidator {

    private static final int EXPECTED_ARGUMENTS = 2; // find-obligation <username>

    private final UserValidator userValidator;
    private final ObligationValidator obligationValidator;

    public FindObligationWithFriendCommandValidator(AuthenticationManager authManager,
                                                    UserValidator userValidator,
                                                    ObligationValidator obligationValidator) {
        super(authManager);
        this.userValidator = userValidator;
        this.obligationValidator = obligationValidator;
    }

    @Override
    public void validate(String[] args, SocketChannel clientChannel) {
        validateArguments(args, EXPECTED_ARGUMENTS, INVALID_FIND_OBLIGATION_COMMAND_MESSAGE);
        validateAuthentication(clientChannel);

        String currentUser = authManager.getAuthenticatedUser(clientChannel).getUsername();
        String friendUsername = args[1];

        userValidator.validateUserExist(friendUsername);

        obligationValidator.validateObligationExists(currentUser, friendUsername);

    }
}