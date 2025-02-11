package bg.sofia.uni.fmi.mjt.splitwise.server.command.authentication;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class RegisterCommandTest {

    private UserServiceAPI userServiceMock;
    private CommandValidator validatorMock;
    private RegisterCommand registerCommand;
    private SocketChannel clientChannelMock;

    private static final String[] VALID_ARGS = {"register", "testUser", "SecurePass123", "John", "Doe"};

    @BeforeEach
    void setUp() {
        userServiceMock = mock(UserServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        registerCommand = new RegisterCommand(userServiceMock, validatorMock);
    }

    @Test
    void testExecute_SuccessfulRegistration() {
        String result = registerCommand.execute(VALID_ARGS, clientChannelMock);

        assertTrue(result.contains("Successfully created user testUser!"),
            "Response should confirm user creation.");
        assertTrue(result.contains("Please register or login to get started."),
            "Response should prompt login.");

        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        //with verify and captor We are capturing the argument passed in addUser method which is the User!
        verify(userServiceMock).addUser(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        // Ensure correct values
        assertEquals("testUser", capturedUser.getUsername(), "Username should match.");
        assertEquals("John", capturedUser.getFirstName(), "First name should match.");
        assertEquals("Doe", capturedUser.getLastName(), "Last name should match.");
        assertNotNull(capturedUser.getPassword(), "Password hash should not be null.");
        assertNotEquals("SecurePass123", capturedUser.getPassword(), "Password should be hashed.");
    }

    @Test
    void testExecute_ValidatorThrowsException() {
        doThrow(new IllegalArgumentException("Validation failed"))
            .when(validatorMock).validate(VALID_ARGS, clientChannelMock);

        assertThrows(IllegalArgumentException.class, () -> registerCommand.execute(VALID_ARGS, clientChannelMock),
            "Should throw an exception when validation fails.");

        verify(userServiceMock, never()).addUser(any());
    }
}
