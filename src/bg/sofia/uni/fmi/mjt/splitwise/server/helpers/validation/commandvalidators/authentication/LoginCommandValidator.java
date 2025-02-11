package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.authentication;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators.UserValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_LOGIN_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.NON_EMPTY_LOGIN_FIELDS_MESSAGE;

public class LoginCommandValidator extends AbstractCommandValidator {

    private final UserValidator userValidator;
    private static final int REQUIRED_ARGUMENTS = 3;

    public LoginCommandValidator(AuthenticationManager authManager, UserValidator userValidator) {
        super(authManager);
        this.userValidator = userValidator;
    }

    @Override
    public void validate(String[] args) {
        validateArguments(args, REQUIRED_ARGUMENTS, INVALID_LOGIN_COMMAND_MESSAGE);
        validateArgumentsNull(args, NON_EMPTY_LOGIN_FIELDS_MESSAGE);

        String username = args[1];
        String password = args[2];
        userValidator.validateUserExist(username); // Check if user exists
        if (!authManager.authenticate(username, password)) {
            throw new InvalidPasswordException("Invalid password try again!");
        }

    }
}
