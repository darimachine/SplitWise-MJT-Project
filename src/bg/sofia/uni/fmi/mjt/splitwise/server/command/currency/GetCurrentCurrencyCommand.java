package bg.sofia.uni.fmi.mjt.splitwise.server.command.currency;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.nio.channels.SocketChannel;

public class GetCurrentCurrencyCommand implements Command {
    private final AuthenticationManager authManager;
    private final CommandValidator validator;

    public GetCurrentCurrencyCommand(AuthenticationManager authManager, CommandValidator validator) {
        this.authManager = authManager;
        this.validator = validator;
    }

    @Override
    public String execute(String[] arguments, SocketChannel clientChannel) {
        validator.validate(arguments, clientChannel);
        return "Your prefered currency is : " +
            authManager.getAuthenticatedUser(clientChannel).getPreferredCurrency().getCurrency();

    }
}
