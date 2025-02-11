package bg.sofia.uni.fmi.mjt.splitwise.server.command.factory;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.SplitWiseExceptions;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.util.LoggerUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.nio.channels.SocketChannel;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CommandProcessorTest {

    private CommandProcessor commandProcessor;
    private CommandFactory commandFactoryMock;
    private AuthenticationManager authManagerMock;
    private SocketChannel clientChannelMock;

    @BeforeEach
    void setUp() {
        commandFactoryMock = mock(CommandFactory.class);
        authManagerMock = mock(AuthenticationManager.class);
        clientChannelMock = mock(SocketChannel.class);
        commandProcessor = new CommandProcessor(commandFactoryMock, authManagerMock);
    }

    @Test
    void testProcessClientInput_NullOrBlankInput_ThrowsInvalidCommandException() {
        String result = commandProcessor.processClientInput("   ", clientChannelMock);
        assertEquals(EMPTY_COMMAND_MESSAGE, result, "Should return EMPTY_COMMAND_MESSAGE for blank input.");
    }

    @Test
    void testProcessClientInput_ValidCommand_ExecutesSuccessfully() {
        String input = "logout";
        String[] parsedArgs = {"logout"};
        Command commandMock = mock(Command.class);

        when(commandFactoryMock.createCommand("logout")).thenReturn(commandMock);
        when(commandMock.execute(parsedArgs, clientChannelMock)).thenReturn("Logged out successfully.");

        String result = commandProcessor.processClientInput(input, clientChannelMock);

        verify(commandFactoryMock).createCommand("logout");
        verify(commandMock).execute(parsedArgs, clientChannelMock);
        assertEquals("Logged out successfully.", result, "Should return the correct command execution output.");
    }

    @Test
    void testProcessClientInput_UnknownCommand_ThrowsInvalidCommandException() {
        String input = "unknownCommand";
        when(commandFactoryMock.createCommand("unknownCommand")).thenThrow(new InvalidCommandException(INVALID_COMMAND_MESSAGE));

        String result = commandProcessor.processClientInput(input, clientChannelMock);

        assertEquals(INVALID_COMMAND_MESSAGE, result, "Should return INVALID_COMMAND_MESSAGE for unknown commands.");
    }

    @Test
    void testProcessClientInput_CommandThrowsSplitWiseException_ReturnsErrorMessage() {
        String input = "someCommand";
        String[] parsedArgs = {"someCommand"};
        Command commandMock = mock(Command.class);
        when(commandFactoryMock.createCommand("someCommand")).thenReturn(commandMock);
        when(commandMock.execute(parsedArgs, clientChannelMock)).thenThrow(new SplitWiseExceptions("Custom error occurred"));

        String result = commandProcessor.processClientInput(input, clientChannelMock);

        assertEquals("Custom error occurred", result, "Should return the exception message when a SplitWiseExceptions occurs.");
    }

    @Test
    void testProcessClientInput_CommandThrowsUnexpectedException_ReturnsUnexpectedErrorMessage() {
        String input = "someCommand";
        String[] parsedArgs = {"someCommand"};
        Command commandMock = mock(Command.class);
        when(commandFactoryMock.createCommand("someCommand")).thenReturn(commandMock);
        when(commandMock.execute(parsedArgs, clientChannelMock)).thenThrow(new RuntimeException("Unexpected failure"));

        String result = commandProcessor.processClientInput(input, clientChannelMock);

        assertEquals(UNEXPECTED_ERROR_MESSAGE + System.lineSeparator() + "Unexpected failure", result,
            "Should return UNEXPECTED_ERROR_MESSAGE followed by the error message.");
    }

    @Test
    void testProcessClientInput_EnsuresProperLoggingOnException() {
        String input = "errorCommand";
        String errorMessage = "Something went wrong";

        when(commandFactoryMock.createCommand("errorCommand"))
            .thenThrow(new SplitWiseExceptions(errorMessage));

        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        User user1 = mock(User.class);
        when(user1.getUsername()).thenReturn("user1");
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(user1);

        try (MockedStatic<LoggerUtil> loggerMock = mockStatic(LoggerUtil.class)) {
            commandProcessor.processClientInput(input, clientChannelMock);

            loggerMock.verify(() -> LoggerUtil.logError(
                eq(errorMessage + " " + input),
                argThat(ex -> ex instanceof SplitWiseExceptions && ex.getMessage().equals(errorMessage)),
                eq("user1")
            ));
        }
    }
}
