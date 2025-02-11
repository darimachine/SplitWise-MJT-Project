package bg.sofia.uni.fmi.mjt.splitwise.server.command.expenses;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShowExpensesCommandTest {

    private ShowExpensesCommand command;
    private AuthenticationManager authManagerMock;
    private ExpenseServiceAPI expenseServiceMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;
    private User userMock;

    private static final String[] VALID_ARGS = {"my-expenses"};
    private static final String MOCK_EXPENSES = "Expense 1: Dinner - $20\nExpense 2: Taxi - $10";

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        expenseServiceMock = mock(ExpenseServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        userMock = mock(User.class);

        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(userMock);
        when(userMock.getUsername()).thenReturn("testUser");
        when(expenseServiceMock.getExpensesAsString("testUser")).thenReturn(MOCK_EXPENSES);

        command = new ShowExpensesCommand(authManagerMock, expenseServiceMock, validatorMock);
    }

    @Test
    void testExecute_SuccessfulExpenseRetrieval() {
        String result = command.execute(VALID_ARGS, clientChannelMock);
        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
        verify(authManagerMock).getAuthenticatedUser(clientChannelMock);
        verify(expenseServiceMock).getExpensesAsString("testUser");

        assertEquals(MOCK_EXPENSES, result, "Should return formatted expense details.");
    }

    @Test
    void testExecute_ValidatorIsCalled() {
        command.execute(VALID_ARGS, clientChannelMock);
        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
    }

    @Test
    void testExecute_AuthenticationManagerIsCalled() {
        command.execute(VALID_ARGS, clientChannelMock);
        verify(authManagerMock).getAuthenticatedUser(clientChannelMock);
    }

    @Test
    void testExecute_ExpenseServiceIsCalled() {
        command.execute(VALID_ARGS, clientChannelMock);
        verify(expenseServiceMock).getExpensesAsString("testUser");
    }
}
