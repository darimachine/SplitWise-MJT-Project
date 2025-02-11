package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.authentication;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators.UserValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.util.regex.Pattern;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_PASSWORD_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_REGISTER_COMMAND_MESSAGE;

public class RegisterCommandValidator extends AbstractCommandValidator {

    private final UserValidator userValidator;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$");
    private static final int REQUIRED_ARGUMENTS = 5;

    public RegisterCommandValidator(AuthenticationManager authManager, UserValidator userValidator) {
        super(authManager);
        this.userValidator = userValidator;
    }

    public void validate(String[] args) {
        validateArguments(args, REQUIRED_ARGUMENTS, INVALID_REGISTER_COMMAND_MESSAGE);
        validateArgumentsNull(args, INVALID_REGISTER_COMMAND_MESSAGE);
        validateUserNotLogged();
        String username = args[1];
        String password = args[2];
        userValidator.validateUserDoesNotExists(username);

        // for passwords
//        if (!PASSWORD_PATTERN.matcher(password).matches()) {
//            throw new InvalidPasswordException(INVALID_PASSWORD_MESSAGE);
//        }
    }
}
