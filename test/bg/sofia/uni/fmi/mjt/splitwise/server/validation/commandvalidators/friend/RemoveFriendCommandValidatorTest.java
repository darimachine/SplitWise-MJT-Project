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

class RemoveFriendCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private FriendshipValidator friendshipValidatorMock;
    private SocketChannel clientChannelMock;
    private RemoveFriendCommandValidator validator;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        friendshipValidatorMock = mock(FriendshipValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        validator = new RemoveFriendCommandValidator(authManagerMock, friendshipValidatorMock);
    }

    @Test
    void testValidate_SuccessfulValidation() {
        String[] args = {"remove-friend", "friendUser"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("currentUser", "pass", "First", "Last"));

        assertDoesNotThrow(() -> validator.validate(args, clientChannelMock),
            "Validation should pass with correct arguments.");

        verify(friendshipValidatorMock).validateUserExists("friendUser");
        verify(friendshipValidatorMock).validateFriendshipExists("currentUser", "friendUser");
        verify(friendshipValidatorMock).validateRemoveFriendShipIfThereIsObligationsBetweenThem("currentUser",
            "friendUser");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionWhenIncorrectArguments() {
        String[] args = {"remove-friend"};
        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException for incorrect argument count.");
    }

    @Test
    void testValidate_ThrowsExceptionIfNotAuthenticated() {
        String[] args = {"remove-friend", "friendUser"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(false);

        assertThrows(UserNotAuthenticatedException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException when user is not authenticated.");
    }

    @Test
    void testValidate_ThrowsExceptionIfRemovingSelf() {
        String[] args = {"remove-friend", "currentUser"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("currentUser", "pass", "First", "Last"));

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException when user tries to remove themselves.");
    }

    @Test
    void testValidate_ThrowsExceptionIfFriendUserDoesNotExist() {
        String[] args = {"remove-friend", "friendUser"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("currentUser", "pass", "First", "Last"));

        doThrow(new InvalidCommandException("User does not exist")).when(friendshipValidatorMock)
            .validateUserExists("friendUser");

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException if friend user does not exist.");
    }

    @Test
    void testValidate_ThrowsExceptionIfFriendshipDoesNotExist() {
        String[] args = {"remove-friend", "friendUser"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("currentUser", "pass", "First", "Last"));

        doThrow(new InvalidCommandException("Users are not friends")).when(friendshipValidatorMock)
            .validateFriendshipExists("currentUser", "friendUser");

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException if users are not friends.");
    }

    @Test
    void testValidate_ThrowsExceptionIfObligationsExist() {
        String[] args = {"remove-friend", "friendUser"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("currentUser", "pass", "First", "Last"));

        doThrow(new InvalidCommandException("Cannot remove friend due to obligations")).when(friendshipValidatorMock)
            .validateRemoveFriendShipIfThereIsObligationsBetweenThem("currentUser", "friendUser");

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException if obligations exist between users.");
    }
}
