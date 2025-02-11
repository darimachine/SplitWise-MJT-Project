package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.group;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotInAnyGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
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

class ShowGroupObligationsCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private UserValidator userValidatorMock;
    private ShowGroupObligationsCommandValidator validator;
    private SocketChannel clientChannelMock;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        userValidatorMock = mock(UserValidator.class);
        validator = new ShowGroupObligationsCommandValidator(authManagerMock, userValidatorMock);
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
        String[] invalidArgs = {""}; // Empty command
        assertThrows(InvalidCommandException.class, () -> validator.validate(invalidArgs, clientChannelMock),
            "Should throw InvalidCommandException when arguments contain null or empty values.");
    }

    @Test
    void testValidate_ThrowsUserNotAuthenticatedExceptionIfNotLoggedIn() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(false);
        String[] args = {"my-groups"};

        assertThrows(UserNotAuthenticatedException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw UserNotAuthenticatedException if user is not logged in.");
    }

    @Test
    void testValidate_ThrowsUserNotInAnyGroupExceptionIfUserHasNoGroups() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        User user = new User("alex", "pass", "Alex", "Johnson");
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(user);

        doThrow(new UserNotInAnyGroupException("User not in any group"))
            .when(userValidatorMock).validateIfUserIsInsideAGroup(user.getGroups());

        String[] args = {"my-groups"};

        assertThrows(UserNotInAnyGroupException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw UserNotInAnyGroupException when user has no groups.");
    }

    @Test
    void testValidate_DoesNotThrowIfUserHasGroups() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        User user = new User("alex", "pass", "Alex", "Johnson");
        user.getGroups().add("group1"); // User is part of a group
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(user);

        doNothing().when(userValidatorMock).validateIfUserIsInsideAGroup(user.getGroups());

        String[] args = {"my-groups"};

        assertDoesNotThrow(() -> validator.validate(args, clientChannelMock),
            "Should not throw an exception if the user has group obligations.");
    }
}
