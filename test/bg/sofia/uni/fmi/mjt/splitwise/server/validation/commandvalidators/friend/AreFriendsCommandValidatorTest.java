package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.friend;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotAuthenticatedException;
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

class AreFriendsCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private FriendshipValidator friendshipValidatorMock;
    private SocketChannel clientChannelMock;
    private AreFriendsCommandValidator validator;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        friendshipValidatorMock = mock(FriendshipValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        validator = new AreFriendsCommandValidator(authManagerMock, friendshipValidatorMock);
    }

    @Test
    void testValidate_SuccessfulValidation() {
        String[] args = {"are-friends", "user1", "user2"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);

        assertDoesNotThrow(() -> validator.validate(args, clientChannelMock),
            "Validation should pass with correct arguments.");

        verify(friendshipValidatorMock).validateUserExists("user1");
        verify(friendshipValidatorMock).validateUserExists("user2");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionWhenIncorrectArguments() {
        String[] args = {"are-friends", "user1"};
        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException for incorrect argument count.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionWhenArgumentsAreNull() {
        String[] args = {"are-friends", null, "user2"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException if arguments contain null or blank values.");
    }

    @Test
    void testValidate_ThrowsExceptionIfNotAuthenticated() {
        String[] args = {"are-friends", "user1", "user2"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(false);

        assertThrows(UserNotAuthenticatedException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException when user is not authenticated.");
    }

    @Test
    void testValidate_ThrowsExceptionIfUserDoesNotExist() {
        String[] args = {"are-friends", "user1", "user2"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);

        doThrow(new InvalidCommandException("User does not exist")).when(friendshipValidatorMock)
            .validateUserExists("user1");

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException if one of the users does not exist.");
    }
}
