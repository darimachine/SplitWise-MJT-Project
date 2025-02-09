package bg.sofia.uni.fmi.mjt.splitwise.server.command.factory;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.SplitWiseExceptions;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.util.LoggerUtil;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.EMPTY_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.UNEXPECTED_ERROR_MESSAGE;

public class CommandProcessor {

    private final CommandFactory commandFactory;
    private final AuthenticationManager authManager;

    public CommandProcessor(CommandFactory commandFactory, AuthenticationManager authManager) {
        this.commandFactory = commandFactory;
        this.authManager = authManager;
    }

    public String processClientInput(String clientInput) {
        try {
            if (clientInput == null || clientInput.isBlank()) {
                throw new InvalidCommandException(EMPTY_COMMAND_MESSAGE);
            }
            String[] args = CommandParser.parseCommand(clientInput);
            if (args.length == 0) {
                throw new InvalidCommandException(INVALID_COMMAND_MESSAGE);
            }
            String commandName = args[0];
            Command command = commandFactory.createCommand(commandName);

            return command.execute(args);
        } catch (SplitWiseExceptions e) {

            String currentUser =
                authManager.isAuthenticated() ? authManager.getAuthenticatedUser().getUsername() : "Unknown";
            LoggerUtil.logError(e.getMessage() + " " + clientInput, e, currentUser);

            return e.getMessage();
        } catch (Exception e) {

            String currentUser =
                authManager.isAuthenticated() ? authManager.getAuthenticatedUser().getUsername() : "Unknown";
            LoggerUtil.logError(e.getMessage() + " " + clientInput, e, currentUser);
            return UNEXPECTED_ERROR_MESSAGE;
        }

    }
}
