package bg.sofia.uni.fmi.mjt.splitwise.server.command.currency;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.model.enums.Currency;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;

import java.nio.channels.SocketChannel;

public class SwitchCurrencyCommand implements Command {
    private final AuthenticationManager authManager;
    private final UserServiceAPI userService;
    private final CommandValidator validator;

    public SwitchCurrencyCommand(AuthenticationManager authManager, UserServiceAPI userService,
                                 CommandValidator validator) {
        this.authManager = authManager;
        this.userService = userService;
        this.validator = validator;
    }

    @Override
    public String execute(String[] arguments, SocketChannel clientChannel) {
        validator.validate(arguments, clientChannel);
        User loggedUser = authManager.getAuthenticatedUser(clientChannel);
        String newCurrency = arguments[1].toUpperCase();
        loggedUser.setCurrency(Currency.fromString(newCurrency));
        userService.saveAll();
        return String.format("Successfully switched currency to %s!", newCurrency);
    }
}
