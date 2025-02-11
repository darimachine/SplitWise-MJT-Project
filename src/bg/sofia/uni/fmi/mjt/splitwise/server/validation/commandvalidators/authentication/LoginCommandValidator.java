package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.authentication;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserAlreadyAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.UserValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.nio.channels.SocketChannel;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_LOGIN_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.NON_EMPTY_LOGIN_FIELDS_MESSAGE;

public class LoginCommandValidator extends AbstractCommandValidator {

    private final UserValidator userValidator;
    private static final int REQUIRED_ARGUMENTS = 3;
    private static final int USERNAME_INDEX = 1;

    public LoginCommandValidator(AuthenticationManager authManager, UserValidator userValidator) {
        super(authManager);
        this.userValidator = userValidator;
    }

    @Override
    public void validate(String[] args, SocketChannel clientChannel) {
        validateArguments(args, REQUIRED_ARGUMENTS, INVALID_LOGIN_COMMAND_MESSAGE);
        validateArgumentsNull(args, NON_EMPTY_LOGIN_FIELDS_MESSAGE);

        String username = args[USERNAME_INDEX];
        userValidator.validateUserExist(username); // Check if user exists
        if (authManager.isAuthenticated(clientChannel)) {
            throw new UserAlreadyAuthenticatedException("You are already logged in!");
        }

    }
}
