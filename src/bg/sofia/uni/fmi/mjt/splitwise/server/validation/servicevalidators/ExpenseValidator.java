package bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.expense.ExpenseWithThatUserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.expense.NegativeAmountException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.expense.UserParticipatingInHisOwnExpenseException;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseServiceAPI;

import java.util.Set;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_AMOUNT_MESSAGE;

public class ExpenseValidator {

    private final UserValidator userValidator;
    private final ExpenseServiceAPI expenseService;

    public ExpenseValidator(ExpenseServiceAPI expenseService, UserValidator userValidator) {
        this.userValidator = userValidator;
        this.expenseService = expenseService;
    }

    public void validateUserExpenseExists(String username) {
        if (expenseService.getUserExpenses(username).isEmpty()) {
            throw new ExpenseWithThatUserNotFoundException("No recorded expenses for user: " + username);
        }
    }

    public void validateExpenseInputs(String payer, double amount, String reason, Set<String> participants) {
        userValidator.validateUserExist(payer);
        if (amount <= 0) {
            throw new NegativeAmountException(INVALID_AMOUNT_MESSAGE);
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason cannot be null or empty");
        }
        if (participants == null || participants.isEmpty()) {
            throw new IllegalArgumentException("Participants cannot be null or empty");
        }
        validateParticipants(payer, participants);
    }

    public void validateExpenseInputOnGroups(String payer, double amount, String reason) {
        userValidator.validateUserExist(payer);
        if (amount <= 0) {
            throw new NegativeAmountException(INVALID_AMOUNT_MESSAGE);
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason cannot be null or empty");
        }
    }

    private void validateParticipants(String payer, Set<String> participants) {
        for (String participant : participants) {
            if (participant.equals(payer)) {
                throw new UserParticipatingInHisOwnExpenseException(
                    "Payer cannot be a participant in their own expense.");
            }
            userValidator.validateUserExist(participant);
        }
    }
}
