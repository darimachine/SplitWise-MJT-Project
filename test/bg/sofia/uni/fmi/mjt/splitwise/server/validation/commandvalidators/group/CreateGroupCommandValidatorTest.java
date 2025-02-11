package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.group;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.groups.CannotAddYourselfToGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.GroupValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateGroupCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private GroupValidator groupValidatorMock;
    private UserValidator userValidatorMock;
    private SocketChannel clientChannelMock;
    private CreateGroupCommandValidator validator;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        groupValidatorMock = mock(GroupValidator.class);
        userValidatorMock = mock(UserValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        validator = new CreateGroupCommandValidator(authManagerMock, groupValidatorMock, userValidatorMock);
    }

    @Test
    void testValidate_SuccessfulValidation() {
        String[] args = {"create-group", "groupName", "user1", "user2"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        assertDoesNotThrow(() -> validator.validate(args, clientChannelMock),
            "Validation should pass with correct arguments.");

        verify(groupValidatorMock).validateGroupDoesNotExist("groupName");
        verify(groupValidatorMock).validateGroupSize(Set.of("user1", "user2"));
        verify(userValidatorMock).validateUsersExist(Set.of("user1", "user2"));
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionForIncorrectArguments() {
        String[] args = {"create-group", "groupName"};
        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException for incorrect argument count.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionForNullArguments() {
        String[] args = {"create-group", "groupName", null, "user2"};
        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException when arguments contain null values.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfUserNotAuthenticated() {
        String[] args = {"create-group", "groupName", "user1", "user2"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(false);

        assertThrows(UserNotAuthenticatedException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException when user is not authenticated.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfGroupAlreadyExists() {
        String[] args = {"create-group", "groupName", "user1", "user2"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        doThrow(new InvalidCommandException("Group already exists")).when(groupValidatorMock)
            .validateGroupDoesNotExist("groupName");

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException if the group already exists.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfGroupSizeInvalid() {
        String[] args = {"create-group", "groupName", "user1"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        doThrow(new InvalidCommandException("Group size is too small")).when(groupValidatorMock)
            .validateGroupSize(Set.of("user1"));

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException if the group size is invalid.");
    }

    @Test
    void testValidate_ThrowsCannotAddYourselfToGroupException() {
        String[] args = {"create-group", "groupName", "user1", "alex"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        assertThrows(CannotAddYourselfToGroupException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw CannotAddYourselfToGroupException when user tries to add themselves.");

        verify(groupValidatorMock).validateGroupDoesNotExist("groupName");
        verify(groupValidatorMock).validateGroupSize(Set.of("user1", "alex"));
        verify(userValidatorMock).validateUsersExist(Set.of("user1", "alex"));
    }
}
