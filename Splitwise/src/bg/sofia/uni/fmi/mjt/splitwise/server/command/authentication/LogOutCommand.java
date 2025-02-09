package bg.sofia.uni.fmi.mjt.splitwise.server.command.authentication;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

public class LogOutCommand implements Command {
    private final AuthenticationManager authManager;
    private final CommandValidator validator;

    public LogOutCommand(AuthenticationManager authManager, CommandValidator validator) {
        this.authManager = authManager;
        this.validator = validator;
    }

    @Override
    public String execute(String[] arguments) {
        validator.validate(arguments);
        authManager.logout();
        return "You have been successfully logged out.";
    }
}
