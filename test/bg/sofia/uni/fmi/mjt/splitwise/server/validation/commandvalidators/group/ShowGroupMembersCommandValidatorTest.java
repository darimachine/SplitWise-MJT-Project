package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.group;

import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.GroupValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShowGroupMembersCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private GroupValidator groupValidatorMock;
    private ShowGroupMembersCommandValidator validator;
    private SocketChannel clientChannelMock;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        groupValidatorMock = mock(GroupValidator.class);
        validator = new ShowGroupMembersCommandValidator(authManagerMock, groupValidatorMock);
        clientChannelMock = mock(SocketChannel.class);
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfInvalidArguments() {
        String[] invalidArgs = {"group-info"}; // Missing group name
        assertThrows(InvalidCommandException.class, () -> validator.validate(invalidArgs, clientChannelMock),
            "Should throw InvalidCommandException when arguments are missing.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfArgumentsContainNullOrEmpty() {
        String[] invalidArgs = {"group-info", ""}; // Empty group name
        assertThrows(InvalidCommandException.class, () -> validator.validate(invalidArgs, clientChannelMock),
            "Should throw InvalidCommandException when arguments contain null or empty values.");
    }

    @Test
    void testValidate_ThrowsUserNotAuthenticatedExceptionIfNotLoggedIn() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(false);
        String[] args = {"group-info", "group1"};

        assertThrows(UserNotAuthenticatedException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw UserNotAuthenticatedException if user is not logged in.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfGroupDoesNotExist() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock))
            .thenReturn(new User("alex", "pass", "Alex", "Johnson"));

        doThrow(new InvalidCommandException("Group does not exist"))
            .when(groupValidatorMock).validateGroupExists("group1");

        String[] args = {"group-info", "group1"};

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException when group does not exist.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfUserNotInGroup() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock))
            .thenReturn(new User("alex", "pass", "Alex", "Johnson"));

        doNothing().when(groupValidatorMock).validateGroupExists("group1");

        doThrow(new InvalidCommandException("User not in group"))
            .when(groupValidatorMock).validateUserInsideGroup("group1", "alex");

        String[] args = {"group-info", "group1"};

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException when user is not in the group.");
    }

    @Test
    void testValidate_DoesNotThrowIfValidationPasses() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock))
            .thenReturn(new User("alex", "pass", "Alex", "Johnson"));

        doNothing().when(groupValidatorMock).validateGroupExists("group1");
        doNothing().when(groupValidatorMock).validateUserInsideGroup("group1", "alex");

        String[] args = {"group-info", "group1"};

        assertDoesNotThrow(() -> validator.validate(args, clientChannelMock),
            "Should not throw any exception if the command is valid.");
    }
}
