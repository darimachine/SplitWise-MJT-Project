package bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.expense.ExpenseWithThatUserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.expense.NegativeAmountException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.expense.UserParticipatingInHisOwnExpenseException;
import bg.sofia.uni.fmi.mjt.splitwise.model.Expense;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseServiceAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class ExpenseValidatorTest {

    private ExpenseValidator expenseValidator;
    private ExpenseServiceAPI expenseServiceMock;
    private UserValidator userValidatorMock;

    @BeforeEach
    void setUp() {
        expenseServiceMock = mock(ExpenseServiceAPI.class);
        userValidatorMock = mock(UserValidator.class);

        expenseValidator = new ExpenseValidator(expenseServiceMock, userValidatorMock);
    }

    @Test
    void testValidateUserExpenseExists_NoExpenses_Throws() {
        // userE
        // xpenses => empty => throws
        when(expenseServiceMock.getUserExpenses("alice"))
            .thenReturn(Collections.emptyList());

        assertThrows(ExpenseWithThatUserNotFoundException.class,
            () -> expenseValidator.validateUserExpenseExists("alice"),
            "Expected exception if user has no expenses");
    }

    @Test
    void testValidateUserExpenseExists_HasExpenses_NoThrow() {
        // userExpenses => non-empty => no exception
        when(expenseServiceMock.getUserExpenses("alex"))
            .thenReturn(List.of(mock(Expense.class)));

        assertDoesNotThrow(() -> expenseValidator.validateUserExpenseExists("alex"));
    }

    @Test
    void testValidateExpenseInputs_Success_MultipleParticipants() {

        doNothing().when(userValidatorMock).validateUserExist("john");
        doNothing().when(userValidatorMock).validateUserExist("alice");
        doNothing().when(userValidatorMock).validateUserExist("bob");

        assertDoesNotThrow(() -> expenseValidator.validateExpenseInputs(
            "john", 100.0, "Reason", Set.of("alice", "bob"))
        );

        verify(userValidatorMock).validateUserExist("john");
        verify(userValidatorMock).validateUserExist("alice");
        verify(userValidatorMock).validateUserExist("bob");
    }

    @Test
    void testValidateExpenseInputs_AmountZero_ThrowsNegativeAmountException() {
        assertThrows(NegativeAmountException.class,
            () -> expenseValidator.validateExpenseInputs("john", 0.0, "Reason", Set.of("alice")));
    }

    @Test
    void testValidateExpenseInputs_AmountNegative_ThrowsNegativeAmountException() {
        assertThrows(NegativeAmountException.class,
            () -> expenseValidator.validateExpenseInputs("john", -1.0, "Reason", Set.of("alice")));
    }

    @Test
    void testValidateExpenseInputs_NullReason_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> expenseValidator.validateExpenseInputs("john", 50.0, null, Set.of("alice")));
    }

    @Test
    void testValidateExpenseInputs_BlankReason_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> expenseValidator.validateExpenseInputs("john", 50.0, "  ", Set.of("alice")));
    }

    @Test
    void testValidateExpenseInputs_NullParticipants_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> expenseValidator.validateExpenseInputs("john", 10.0, "Reason", null));
    }

    @Test
    void testValidateExpenseInputs_EmptyParticipants_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> expenseValidator.validateExpenseInputs("john", 10.0, "Reason", Set.of()));
    }

    @Test
    void testValidateExpenseInputs_ParticipantIsPayer_Throws() {
        assertThrows(UserParticipatingInHisOwnExpenseException.class,
            () -> expenseValidator.validateExpenseInputs("john", 10.0, "Reason", Set.of("alice", "john")));
    }

    @Test
    void testValidateExpenseInputOnGroups_Success() {
        // Suppose userValidator is fine with "john"
        doNothing().when(userValidatorMock).validateUserExist("john");

        assertDoesNotThrow(() ->
            expenseValidator.validateExpenseInputOnGroups("john", 123.0, "Group reason")
        );
        verify(userValidatorMock).validateUserExist("john");
    }

    @Test
    void testValidateExpenseInputOnGroups_ZeroAmount_ThrowsNegativeAmount() {
        assertThrows(NegativeAmountException.class,
            () -> expenseValidator.validateExpenseInputOnGroups("alex", 0.0, "Group reason"));
    }

    @Test
    void testValidateExpenseInputOnGroups_NegativeAmount_ThrowsNegativeAmount() {
        assertThrows(NegativeAmountException.class,
            () -> expenseValidator.validateExpenseInputOnGroups("alex", -5.0, "Group reason"));
    }

    @Test
    void testValidateExpenseInputOnGroups_NullReason_Throws() {
        assertThrows(IllegalArgumentException.class,
            () -> expenseValidator.validateExpenseInputOnGroups("alex", 50.0, null));
    }

    @Test
    void testValidateExpenseInputOnGroups_BlankReason_Throws() {
        assertThrows(IllegalArgumentException.class,
            () -> expenseValidator.validateExpenseInputOnGroups("alex", 50.0, "   "));
    }
}
