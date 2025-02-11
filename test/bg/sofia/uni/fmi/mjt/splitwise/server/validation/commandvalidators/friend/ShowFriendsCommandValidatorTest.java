package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.friend;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.FriendshipValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShowFriendsCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private FriendshipValidator friendshipValidatorMock;
    private SocketChannel clientChannelMock;
    private ShowFriendsCommandValidator validator;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        friendshipValidatorMock = mock(FriendshipValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        validator = new ShowFriendsCommandValidator(authManagerMock, friendshipValidatorMock);
    }

    @Test
    void testValidate_SuccessfulValidation() {
        String[] args = {"my-friends"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        assertDoesNotThrow(() -> validator.validate(args, clientChannelMock),
            "Validation should pass with correct arguments.");

        verify(friendshipValidatorMock).validateUserExists("alex");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionWhenIncorrectArguments() {
        String[] args = {};
        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException for incorrect argument count.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionWhenArgumentsAreNullOrBlank() {
        String[] args = {" "};
        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException when arguments are null or blank.");
    }

    @Test
    void testValidate_ThrowsExceptionIfUserNotAuthenticated() {
        String[] args = {"my-friends"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(false);

        assertThrows(UserNotAuthenticatedException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException when user is not authenticated.");
    }

    @Test
    void testValidate_ThrowsExceptionIfUserDoesNotExist() {
        String[] args = {"my-friends"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        doThrow(new InvalidCommandException("User does not exist")).when(friendshipValidatorMock)
            .validateUserExists("alex");

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException if logged-in user does not exist.");
    }
}
