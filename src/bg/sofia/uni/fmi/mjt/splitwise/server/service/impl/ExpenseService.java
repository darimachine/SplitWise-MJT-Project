package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.model.Expense;
import bg.sofia.uni.fmi.mjt.splitwise.server.database.json.ExpenseJsonProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExpenseService implements ExpenseServiceAPI {

    private final ExpenseJsonProcessor processor;
    private final Map<String, List<Expense>> expenses;
    private final ObligationServiceAPI obligationService;

    public ExpenseService(ObligationServiceAPI obligationService, ExpenseJsonProcessor processor) {
        this.processor = processor;
        this.expenses = processor.loadData();
        this.obligationService = obligationService;
    }

    @Override
    public List<Expense> getUserExpenses(String username) {
        return expenses.getOrDefault(username, List.of());
    }

    @Override
    public void addFriendExpense(String payer, double amount, String reason, Set<String> participants) {
        double splitAmount = amount / (participants.size() + 1);
        Expense expense = new Expense(payer, reason, amount, new HashSet<>(participants), LocalDateTime.now());
        expenses.putIfAbsent(payer, new ArrayList<>());
        expenses.get(payer).add(expense);
        for (String participant : participants) {
            obligationService.addObligation(participant, payer, splitAmount);
        }
        processor.saveData(expenses);
    }

    @Override
    public String getExpensesAsString(String username) {
        List<Expense> userExpenses = getUserExpenses(username);
        StringBuilder sb = new StringBuilder("   ****Expenses*****  " + ":\n");
        for (Expense expense : userExpenses) {
            sb.append(expense.toString()).append(System.lineSeparator());
        }

        return sb.toString();
    }
}
