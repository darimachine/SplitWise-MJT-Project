package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import bg.sofia.uni.fmi.mjt.splitwise.model.Expense;

import java.util.List;
import java.util.Set;

public interface ExpenseServiceAPI {

    List<Expense> getUserExpenses(String username);

    void addFriendExpense(String payer, double amount, String reason, Set<String> participants);

    String getExpensesAsString(String username);
}
