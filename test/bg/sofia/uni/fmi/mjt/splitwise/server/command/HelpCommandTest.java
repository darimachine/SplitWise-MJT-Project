package bg.sofia.uni.fmi.mjt.splitwise.server.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HelpCommandTest {

    private HelpCommand command;
    private SocketChannel clientChannelMock;
    private static final String[] ANY_ARGS = {"any", "args"};

    @BeforeEach
    void setUp() {
        command = new HelpCommand();
        clientChannelMock = null;
    }

    @Test
    void testExecute_ReturnsNonEmptyString() {
        String result = command.execute(ANY_ARGS, clientChannelMock);

        assertNotNull(result, "Help command output should not be null.");
        assertFalse(result.isBlank(), "Help command output should not be empty.");
    }

    @Test
    void testExecute_ContainsExpectedCommands() {
        String result = command.execute(ANY_ARGS, clientChannelMock);

        assertTrue(result.contains("-- login <username> <password>"), "Help output should contain login command.");
        assertTrue(result.contains("-- register <username> <password> <first_name> <last_name>"), "Help output should contain register command.");
        assertTrue(result.contains("-- logout - logs out the current user"), "Help output should contain logout command.");
        assertTrue(result.contains("-- add-friend <username>"), "Help output should contain add-friend command.");
        assertTrue(result.contains("-- split <amount> <username> <reason>"), "Help output should contain split command.");
        assertTrue(result.contains("-- payed <username> <amount>"), "Help output should contain payed command.");
        assertTrue(result.contains("-- switch-currency <currency>"), "Help output should contain switch-currency command.");
        assertTrue(result.contains("-- current-currency"), "Help output should contain current-currency command.");
    }

    @Test
    void testExecute_IgnoresArguments() {
        String helpOutputWithArgs = command.execute(new String[]{"random", "arguments"}, clientChannelMock);
        String helpOutputWithoutArgs = command.execute(new String[]{}, clientChannelMock);

        assertTrue(helpOutputWithArgs.equals(helpOutputWithoutArgs), "Help command should return the same output regardless of arguments.");
    }
}
