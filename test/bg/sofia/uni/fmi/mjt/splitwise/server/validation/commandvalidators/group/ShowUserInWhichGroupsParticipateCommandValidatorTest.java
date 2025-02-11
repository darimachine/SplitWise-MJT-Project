package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.group;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.UserValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShowUserInWhichGroupsParticipateCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private UserValidator userValidatorMock;
    private ShowUserInWhichGroupsParticipateCommandValidator validator;
    private SocketChannel clientChannelMock;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        userValidatorMock = mock(UserValidator.class);
        validator = new ShowUserInWhichGroupsParticipateCommandValidator(authManagerMock, userValidatorMock);
        clientChannelMock = mock(SocketChannel.class);
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfInvalidArguments() {
        String[] invalidArgs = {}; // Empty arguments
        assertThrows(InvalidCommandException.class, () -> validator.validate(invalidArgs, clientChannelMock),
            "Should throw InvalidCommandException when arguments are missing.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfArgumentsContainNullOrEmpty() {
        String[] invalidArgs = {"getUserGroups", ""}; // Empty username
        assertThrows(UserNotAuthenticatedException.class, () -> validator.validate(invalidArgs, clientChannelMock),
            "Should throw InvalidCommandException when arguments contain null or empty values.");
    }

    @Test
    void testValidate_ThrowsUserNotAuthenticatedExceptionIfNotLoggedIn() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(false);
        String[] args = {"getUserGroups", "alex"};

        assertThrows(UserNotAuthenticatedException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw UserNotAuthenticatedException if user is not logged in.");
    }

    @Test
    void testValidate_ThrowsUserNotFoundExceptionIfUserDoesNotExist() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        doThrow(new UserNotFoundException("User not found")).when(userValidatorMock).validateUserExist("alex");

        String[] args = {"getUserGroups", "alex"};

        assertThrows(UserNotFoundException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw UserNotFoundException when the user does not exist.");
    }

    @Test
    void testValidate_DoesNotThrowIfUserExistsAndAuthenticated() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        doNothing().when(userValidatorMock).validateUserExist("alex");

        String[] args = {"getUserGroups", "alex"};

        assertDoesNotThrow(() -> validator.validate(args, clientChannelMock),
            "Should not throw an exception if the user exists and is authenticated.");
    }
}
