package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserAlreadyAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.UserNotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.util.Arrays;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.NOT_AUTHENTICATED_MESSAGE;

public abstract class AbstractCommandValidator implements CommandValidator {

    protected final AuthenticationManager authManager;

    protected AbstractCommandValidator(AuthenticationManager authManager) {
        this.authManager = authManager;
    }

    protected void validateAuthentication() {
        if (!authManager.isAuthenticated()) {
            throw new UserNotAuthenticatedException(NOT_AUTHENTICATED_MESSAGE);
        }
    }

    protected void validateUserNotLogged() {
        if (authManager.isAuthenticated()) {
            throw new UserAlreadyAuthenticatedException("User is already logged in.");
        }
    }

    protected void validateArguments(String[] args, int expectedLength, String errorMessage) {
        if (args.length != expectedLength) {
            throw new InvalidCommandException(errorMessage);
        }
    }

    protected void validateMinArguments(String[] args, int minExpectedLength, String errorMessage) {
        if (args.length < minExpectedLength) {
            throw new InvalidCommandException(errorMessage);
        }
    }

    protected void validateArgumentsNull(String[] args, String errorMessage) {
        if (Arrays.stream(args).anyMatch(arg -> arg == null || arg.isBlank())) {
            throw new InvalidCommandException(errorMessage);
        }
    }
}