package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.group;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.groups.GroupDoesntExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.expense.NegativeAmountException;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.ExpenseValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.GroupValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SplitWithGroupCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private ExpenseValidator expenseValidatorMock;
    private GroupValidator groupValidatorMock;
    private SplitWithGroupCommandValidator validator;
    private SocketChannel clientChannelMock;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        expenseValidatorMock = mock(ExpenseValidator.class);
        groupValidatorMock = mock(GroupValidator.class);
        validator = new SplitWithGroupCommandValidator(authManagerMock, expenseValidatorMock, groupValidatorMock);
        clientChannelMock = mock(SocketChannel.class);
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfInvalidArguments() {
        String[] invalidArgs = {}; // Empty arguments
        assertThrows(InvalidCommandException.class, () -> validator.validate(invalidArgs, clientChannelMock),
            "Should throw InvalidCommandException when arguments are missing.");
    }

    @Test
    void testValidate_ThrowsInvalidCommandExceptionIfAmountIsNotANumber() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        String[] invalidArgs = {"split-group", "abc", "group1", "Dinner"};

        assertThrows(InvalidCommandException.class, () -> validator.validate(invalidArgs, clientChannelMock),
            "Should throw InvalidCommandException when amount is not a valid number.");
    }

    @Test
    void testValidate_ThrowsUserNotAuthenticatedExceptionIfNotLoggedIn() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(false);
        String[] args = {"split-group", "50.0", "group1", "Dinner"};

        assertThrows(UserNotAuthenticatedException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw UserNotAuthenticatedException if user is not logged in.");
    }

    @Test
    void testValidate_ThrowsGroupDoesNotExistExceptionIfGroupIsInvalid() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));
        doThrow(new GroupDoesntExistsException("Group not found")).when(groupValidatorMock)
            .validateGroupExists("group1");

        String[] args = {"split-group", "50.0", "group1", "Dinner"};

        assertThrows(GroupDoesntExistsException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw GroupDoesntExistsException when the group does not exist.");
    }

    @Test
    void testValidate_ThrowsNegativeAmountExceptionIfAmountIsNegative() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));
        doThrow(new NegativeAmountException("Amount cannot be negative")).when(expenseValidatorMock)
            .validateExpenseInputOnGroups("alex", -50.0, "Dinner");

        String[] args = {"split-group", "-50.0", "group1", "Dinner"};

        assertThrows(NegativeAmountException.class, () -> validator.validate(args, clientChannelMock),
            "Should throw NegativeAmountException when amount is negative.");
    }

    @Test
    void testValidate_DoesNotThrowIfValidArguments() {
        when(authManagerMock.isAuthenticated(clientChannelMock)).thenReturn(true);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(
            new User("alex", "pass", "Alex", "Johnson"));

        String[] args = {"split-group", "50.0", "group1", "Dinner"};

        assertDoesNotThrow(() -> validator.validate(args, clientChannelMock),
            "Should not throw an exception if all arguments are valid.");
    }
}
