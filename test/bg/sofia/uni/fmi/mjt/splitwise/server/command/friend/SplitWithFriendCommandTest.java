package bg.sofia.uni.fmi.mjt.splitwise.server.command.friend;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.model.enums.Currency;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SplitWithFriendCommandTest {

    private SplitWithFriendCommand command;
    private AuthenticationManager authManagerMock;
    private ExpenseServiceAPI expenseServiceMock;
    private NotificationServiceAPI notificationServiceMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;
    private User mockUser;

    private static final String LOGGED_USER = "john_doe";
    private static final String FRIEND_USERNAME = "alice";
    private static final String DESCRIPTION = "Dinner";
    private static final double AMOUNT = 20.0;
    private static final Currency USER_CURRENCY = Currency.EUR;
    private static final String[] VALID_ARGS = {"split", "20.0", "alice", "Dinner"};

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        expenseServiceMock = mock(ExpenseServiceAPI.class);
        notificationServiceMock = mock(NotificationServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        mockUser = mock(User.class);

        command = new SplitWithFriendCommand(authManagerMock, expenseServiceMock, notificationServiceMock, validatorMock);
    }

    @Test
    void testExecute_ValidatorIsCalled() {
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);
        when(mockUser.getUsername()).thenReturn(LOGGED_USER);
        when(mockUser.getPreferredCurrency()).thenReturn(USER_CURRENCY);

        command.execute(VALID_ARGS, clientChannelMock);
        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
    }

    @Test
    void testExecute_SuccessfullySplitsExpense() {
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);
        when(mockUser.getUsername()).thenReturn(LOGGED_USER);
        when(mockUser.getPreferredCurrency()).thenReturn(USER_CURRENCY);
        when(mockUser.getFullName()).thenReturn("John Doe");

        String expectedMessage = String.format(
            "You successfully split %.2f %s with %s. They owe you %.2f %s.",
            AMOUNT, USER_CURRENCY.getCurrency(), FRIEND_USERNAME, AMOUNT / 2, USER_CURRENCY.getCurrency());

        String result = command.execute(VALID_ARGS, clientChannelMock);

        verify(expenseServiceMock).addFriendExpense(LOGGED_USER, AMOUNT, DESCRIPTION, Set.of(FRIEND_USERNAME));
        verify(notificationServiceMock).addNotification(
            "John Doe paid a total of 20.00 EUR for you (10.00 each). Reason: Dinner.",
            FRIEND_USERNAME);

        assertEquals(expectedMessage, result, "Returned message should confirm the expense split.");
    }

    @Test
    void testExecute_ThrowsExceptionForInvalidAmount() {
        String[] invalidArgs = {"split", "invalidAmount", "alice", "Dinner"};
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);
        when(mockUser.getUsername()).thenReturn(LOGGED_USER);

        assertThrows(NumberFormatException.class, () -> command.execute(invalidArgs, clientChannelMock),
            "Should throw NumberFormatException for invalid amount format.");
    }

    @Test
    void testExecute_CorrectlyFormatsNotificationMessage() {
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);
        when(mockUser.getUsername()).thenReturn(LOGGED_USER);
        when(mockUser.getPreferredCurrency()).thenReturn(USER_CURRENCY);
        when(mockUser.getFullName()).thenReturn("John Doe");

        command.execute(VALID_ARGS, clientChannelMock);

        verify(notificationServiceMock).addNotification(
            "John Doe paid a total of 20.00 EUR for you (10.00 each). Reason: Dinner.",
            FRIEND_USERNAME);
    }
}
