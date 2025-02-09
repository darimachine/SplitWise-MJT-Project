package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.authentication;

import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_LOGOUT_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.NON_EMPTY_COMMANDS_MESSAGE;

public class LogOutCommandValidator extends AbstractCommandValidator {

    private static final int REQUIRED_ARGUMENTS = 1; // logout command has no arguments

    public LogOutCommandValidator(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    public void validate(String[] args) {
        validateArguments(args, REQUIRED_ARGUMENTS, INVALID_LOGOUT_COMMAND_MESSAGE);
        validateArgumentsNull(args, NON_EMPTY_COMMANDS_MESSAGE);
        validateAuthentication();
    }
}