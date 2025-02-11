package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.group;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.FriendshipValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.GroupValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class AddFriendToGroupCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private GroupValidator groupValidatorMock;
    private UserValidator userValidatorMock;
    private FriendshipValidator friendshipValidatorMock;
    private SocketChannel clientChannelMock;
    private AddFriendToGroupCommandValidator validator;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        groupValidatorMock = mock(GroupValidator.class);
        userValidatorMock = mock(UserValidator.class);
        friendshipValidatorMock = mock(FriendshipValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        validator = new AddFriendToGroupCommandValidator(authManagerMock, groupValidatorMock, userValidatorMock,
            friendshipValidatorMock);
    }

    @Test
    void testValidate_SuccessfulValidation() {
        String[] args = {"add-friend-to-group", "groupName", "friendUser"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        assertDoesNotThrow(() -> validator.validate(args, clientChannelMock),
            "Validation should pass with correct arguments.");

        verify(friendshipValidatorMock).validateFriendshipExists("alex", "friendUser");
        verify(userValidatorMock).validateUserExist("friendUser");
        verify(groupValidatorMock).validateGroupExists("groupName");
        verify(groupValidatorMock).validateUserInsideGroup("groupName", "alex");
        verify(groupValidatorMock).validateUserNotInGroup("groupName", "friendUser");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionForIncorrectArguments() {
        String[] args = {"add-friend-to-group", "groupName"};
        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException for incorrect argument count.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionForNullArguments() {
        String[] args = {"add-friend-to-group", null, "friendUser"};
        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException when arguments contain null values.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfUserNotAuthenticated() {
        String[] args = {"add-friend-to-group", "groupName", "friendUser"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(false);

        assertThrows(UserNotAuthenticatedException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException when user is not authenticated.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfFriendshipDoesNotExist() {
        String[] args = {"add-friend-to-group", "groupName", "friendUser"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        doThrow(new InvalidCommandException("Users are not friends")).when(friendshipValidatorMock)
            .validateFriendshipExists("alex", "friendUser");

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException if the users are not friends.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfGroupDoesNotExist() {
        String[] args = {"add-friend-to-group", "groupName", "friendUser"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        doThrow(new InvalidCommandException("Group does not exist")).when(groupValidatorMock)
            .validateGroupExists("groupName");

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException if the group does not exist.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfUserNotInGroup() {
        String[] args = {"add-friend-to-group", "groupName", "friendUser"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        doThrow(new InvalidCommandException("User is not in the group")).when(groupValidatorMock)
            .validateUserInsideGroup("groupName", "alex");

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException if the user is not in the group.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfFriendAlreadyInGroup() {
        String[] args = {"add-friend-to-group", "groupName", "friendUser"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        doThrow(new InvalidCommandException("Friend is already in the group")).when(groupValidatorMock)
            .validateUserNotInGroup("groupName", "friendUser");

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException if the friend is already in the group.");
    }
}
