package bg.sofia.uni.fmi.mjt.splitwise.server.command.group;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.nio.channels.SocketChannel;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SplitWithGroupCommandTest {

    private static final String LOGGED_USERNAME = "user1";
    private static final String GROUP_NAME = "TestGroup";
    private static final String DESCRIPTION = "Dinner";
    private static final String FRIEND_1 = "friend1";
    private static final String FRIEND_2 = "friend2";
    private static final double AMOUNT = 100.0;
    private static final double SPLIT_AMOUNT = AMOUNT / 3.0; // 2 friends + logged user

    private GroupServiceAPI groupServiceMock;
    private ExpenseServiceAPI expenseServiceMock;
    private NotificationServiceAPI notificationServiceMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;
    private SplitWithGroupCommand command;

    @BeforeEach
    void setUp() {
        AuthenticationManager authManagerMock = mock(AuthenticationManager.class);
        groupServiceMock = mock(GroupServiceAPI.class);
        expenseServiceMock = mock(ExpenseServiceAPI.class);
        notificationServiceMock = mock(NotificationServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        User loggedUserMock = mock(User.class);

        command = new SplitWithGroupCommand(authManagerMock, groupServiceMock, expenseServiceMock,
            notificationServiceMock, validatorMock);

        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(loggedUserMock);
        when(loggedUserMock.getUsername()).thenReturn(LOGGED_USERNAME);
    }

    @Test
    void testExecute_ValidArguments_SplitsCorrectly() {
        // Mock group members excluding logged user
        when(groupServiceMock.getGroupMembers(GROUP_NAME)).thenReturn(Set.of(LOGGED_USERNAME, FRIEND_1, FRIEND_2));

        String[] validArgs = {"split-group", String.valueOf(AMOUNT), GROUP_NAME, DESCRIPTION};
        String expectedMessage = String.format(
            "You successfully split %.2f BGN with group %s. They owe you %.2f BGN.",
            AMOUNT, GROUP_NAME, AMOUNT - SPLIT_AMOUNT);

        String result = command.execute(validArgs, clientChannelMock);

        verify(validatorMock).validate(validArgs, clientChannelMock);
        verify(expenseServiceMock).addFriendExpense(LOGGED_USERNAME, AMOUNT, DESCRIPTION, Set.of(FRIEND_1, FRIEND_2));

        ArgumentCaptor<String> notificationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Set<String>> recipientsCaptor = ArgumentCaptor.forClass(Set.class);
        verify(notificationServiceMock).addNotification(notificationCaptor.capture(), recipientsCaptor.capture());

        String notificationMessage = notificationCaptor.getValue();
        Set<String> recipients = recipientsCaptor.getValue();

        assertTrue(notificationMessage.contains(LOGGED_USERNAME));
        assertTrue(notificationMessage.contains(DESCRIPTION));
        assertTrue(recipients.contains(FRIEND_1));
        assertTrue(recipients.contains(FRIEND_2));

        assertEquals(expectedMessage, result, "Should return the correct success message.");
    }

    @Test
    void testExecute_ThrowsExceptionOnInvalidAmountFormat() {
        String[] invalidArgs = {"split-group", "invalidAmount", GROUP_NAME, DESCRIPTION};

        assertThrows(NumberFormatException.class, () -> command.execute(invalidArgs, clientChannelMock),
            "Should throw NumberFormatException when amount is invalid.");
    }

    @Test
    void testExecute_GroupHasNoOtherMembers_ReturnsCorrectMessage() {
        when(groupServiceMock.getGroupMembers(GROUP_NAME)).thenReturn(Set.of(LOGGED_USERNAME));

        String[] validArgs = {"split-group", String.valueOf(AMOUNT), GROUP_NAME, DESCRIPTION};
        String expectedMessage = String.format(
            "You successfully split %.2f BGN with group %s. They owe you %.2f BGN.",
            AMOUNT, GROUP_NAME, 0.0);

        String result = command.execute(validArgs, clientChannelMock);

        verify(validatorMock).validate(validArgs, clientChannelMock);
        verify(expenseServiceMock).addFriendExpense(LOGGED_USERNAME, AMOUNT, DESCRIPTION, Set.of());
        assertEquals(expectedMessage, result, "Should return correct message when no other members exist.");
    }

}
