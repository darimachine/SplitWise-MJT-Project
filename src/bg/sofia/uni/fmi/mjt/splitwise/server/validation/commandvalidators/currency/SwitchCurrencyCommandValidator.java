package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.currency;

import bg.sofia.uni.fmi.mjt.splitwise.model.enums.Currency;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.nio.channels.SocketChannel;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_SWITCH_CURRENCY_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.NON_EMPTY_COMMANDS_MESSAGE;

public class SwitchCurrencyCommandValidator extends AbstractCommandValidator {

    private static final int EXPECTED_ARGUMENTS = 2; // Only "switch-currency EUR"
    private static final int CURRENCY_INDEX = 1;
    public SwitchCurrencyCommandValidator(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    public void validate(String[] args, SocketChannel clientChannel) {
        validateArguments(args, EXPECTED_ARGUMENTS, INVALID_SWITCH_CURRENCY_COMMAND_MESSAGE);
        validateArgumentsNull(args, NON_EMPTY_COMMANDS_MESSAGE);
        validateAuthentication(clientChannel);
        String currency = args[CURRENCY_INDEX].toUpperCase();
        Currency.fromString(currency);
    }
}
