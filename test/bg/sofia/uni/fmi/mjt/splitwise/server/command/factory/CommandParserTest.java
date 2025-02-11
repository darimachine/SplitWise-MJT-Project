package bg.sofia.uni.fmi.mjt.splitwise.server.command.factory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class CommandParserTest {

    @Test
    void testParseCommand_SimpleCommand_ReturnsCorrectTokens() {
        String input = "logout";
        String[] expected = {"logout"};

        String[] result = CommandParser.parseCommand(input);

        assertArrayEquals(expected, result, "Should correctly parse a single-word command.");
    }

    @Test
    void testParseCommand_CommandWithArguments_ReturnsCorrectTokens() {
        String input = "add-friend johnDoe";
        String[] expected = {"add-friend", "johnDoe"};

        String[] result = CommandParser.parseCommand(input);

        assertArrayEquals(expected, result, "Should correctly split a command with arguments.");
    }

    @Test
    void testParseCommand_CommandWithExtraWhitespace_ReturnsTrimmedTokens() {
        String input = "   register   user1   password1   John   Doe   ";
        String[] expected = {"register", "user1", "password1", "John", "Doe"};

        String[] result = CommandParser.parseCommand(input);

        assertArrayEquals(expected, result, "Should correctly parse and remove extra spaces.");
    }

    @Test
    void testParseCommand_SplitCommandWithReasonField_ReturnsCorrectTokens() {
        String input = "split 50 johnDoe Dinner at restaurant";
        String[] expected = {"split", "50", "johnDoe", "Dinner at restaurant"};

        String[] result = CommandParser.parseCommand(input);

        assertArrayEquals(expected, result, "Should correctly parse a split command with a reason field.");
    }

    @Test
    void testParseCommand_SplitGroupCommandWithReasonField_ReturnsCorrectTokens() {
        String input = "split-group 100 teamOuting Fun day at the beach";
        String[] expected = {"split-group", "100", "teamOuting", "Fun day at the beach"};

        String[] result = CommandParser.parseCommand(input);

        assertArrayEquals(expected, result, "Should correctly parse a split-group command with a reason field.");
    }

    @Test
    void testParseCommand_CommandWithInsufficientArguments_ReturnsTokensAsIs() {
        String input = "split 50";
        String[] expected = {"split", "50"};

        String[] result = CommandParser.parseCommand(input);

        assertArrayEquals(expected, result, "Should return the original tokens if not enough arguments are present.");
    }

    @Test
    void testParseCommand_EmptyInput_ReturnsEmptyArray() {
        String input = "";
        String[] expected = {""};

        String[] result = CommandParser.parseCommand(input);

        assertArrayEquals(expected, result, "Should return an empty array for an empty input.");
    }

    @Test
    void testParseCommand_WhitespaceOnlyInput_ReturnsEmptyArray() {
        String input = "     ";
        String[] expected = {""};

        String[] result = CommandParser.parseCommand(input);

        assertArrayEquals(expected, result, "Should return an empty array for an input with only spaces.");
    }

    @Test
    void testParseCommand_UnexpectedCommandWithSpaces_ReturnsCorrectTokens() {
        String input = "unknown-command param1 param2";
        String[] expected = {"unknown-command", "param1", "param2"};

        String[] result = CommandParser.parseCommand(input);

        assertArrayEquals(expected, result, "Should correctly parse an unknown command with parameters.");
    }
}
