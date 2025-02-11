package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.group;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.GroupValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.UserValidator;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RemoveFriendFromGroupCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private GroupValidator groupValidatorMock;
    private UserValidator userValidatorMock;
    private SocketChannel clientChannelMock;
    private RemoveFriendFromGroupCommandValidator validator;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        groupValidatorMock = mock(GroupValidator.class);
        userValidatorMock = mock(UserValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        validator = new RemoveFriendFromGroupCommandValidator(authManagerMock, groupValidatorMock, userValidatorMock);
    }

    @Test
    void testValidate_SuccessfulValidation() {
        String[] args = {"remove-friend-from-group", "group1", "user1"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        assertDoesNotThrow(() -> validator.validate(args, clientChannelMock),
            "Validation should pass with correct arguments.");

        verify(userValidatorMock).validateUserExist("alex");
        verify(userValidatorMock).validateUserExist("user1");
        verify(groupValidatorMock).validateGroupExists("group1");
        verify(groupValidatorMock).validateUserInsideGroup("group1", "alex");
        verify(groupValidatorMock).validateUserInsideGroup("group1", "user1");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionForIncorrectArguments() {
        String[] args = {"remove-friend-from-group", "group1"};
        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException for incorrect argument count.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionForNullArguments() {
        String[] args = {"remove-friend-from-group", "group1", null};
        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException when arguments contain null values.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfUserNotAuthenticated() {
        String[] args = {"remove-friend-from-group", "group1", "user1"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(false);

        assertThrows(UserNotAuthenticatedException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException when user is not authenticated.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfGroupDoesNotExist() {
        String[] args = {"remove-friend-from-group", "group1", "user1"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        doThrow(new InvalidCommandException("Group does not exist")).when(groupValidatorMock)
            .validateGroupExists("group1");

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException if the group does not exist.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfUserNotInGroup() {
        String[] args = {"remove-friend-from-group", "group1", "user1"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        doThrow(new InvalidCommandException("User not in group")).when(groupValidatorMock)
            .validateUserInsideGroup("group1", "alex");

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException if the logged-in user is not in the group.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfFriendNotInGroup() {
        String[] args = {"remove-friend-from-group", "group1", "user1"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        doThrow(new InvalidCommandException("Friend not in group")).when(groupValidatorMock)
            .validateUserInsideGroup("group1", "user1");

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException if the friend is not in the group.");
    }

    @Test
    void testValidate_ThrowsIllegalArgumentExceptionIfRemovingSelf() {
        String[] args = {"remove-friend-from-group", "group1", "alex"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        assertThrows(IllegalArgumentException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw IllegalArgumentException when a user tries to remove themselves.");

        verify(userValidatorMock,times(2)).validateUserExist("alex");
        verify(groupValidatorMock).validateGroupExists("group1");
        verify(groupValidatorMock,times(2)).validateUserInsideGroup("group1", "alex");
    }
}
