package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.friend;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.ExpenseValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.FriendshipValidator;
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

class SplitWithFriendCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private ExpenseValidator expenseValidatorMock;
    private FriendshipValidator friendshipValidatorMock;
    private SocketChannel clientChannelMock;
    private SplitWithFriendCommandValidator validator;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        expenseValidatorMock = mock(ExpenseValidator.class);
        friendshipValidatorMock = mock(FriendshipValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        validator = new SplitWithFriendCommandValidator(authManagerMock, expenseValidatorMock, friendshipValidatorMock);
    }

    @Test
    void testValidate_SuccessfulValidation() {
        String[] args = {"split", "20", "friendUser", "Lunch"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        assertDoesNotThrow(() -> validator.validate(args, clientChannelMock),
            "Validation should pass with correct arguments.");

        verify(friendshipValidatorMock).validateUserExists("friendUser");
        verify(friendshipValidatorMock).validateFriendshipExists("alex", "friendUser");
        verify(expenseValidatorMock).validateExpenseInputs("alex", 20, "Lunch", Set.of("friendUser"));
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionForIncorrectArguments() {
        String[] args = {"split", "20", "friendUser"};
        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException for incorrect argument count.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionForInvalidAmount() {
        String[] args = {"split", "invalidAmount", "friendUser", "Lunch"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException for invalid amount.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfUserNotAuthenticated() {
        String[] args = {"split", "20", "friendUser", "Lunch"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(false);

        assertThrows(UserNotAuthenticatedException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException when user is not authenticated.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfFriendDoesNotExist() {
        String[] args = {"split", "20", "friendUser", "Lunch"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        doThrow(new InvalidCommandException("User does not exist")).when(friendshipValidatorMock)
            .validateUserExists("friendUser");

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException if the friend does not exist.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfUsersAreNotFriends() {
        String[] args = {"split", "20", "friendUser", "Lunch"};
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        doThrow(new InvalidCommandException("Users are not friends")).when(friendshipValidatorMock)
            .validateFriendshipExists("alex", "friendUser");

        assertThrows(InvalidCommandException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw InvalidCommandException if the users are not friends.");
    }
}
