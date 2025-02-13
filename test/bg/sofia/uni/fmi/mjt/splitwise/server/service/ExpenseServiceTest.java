package bg.sofia.uni.fmi.mjt.splitwise.server.service;


import bg.sofia.uni.fmi.mjt.splitwise.model.Expense;
import bg.sofia.uni.fmi.mjt.splitwise.server.database.json.ExpenseJsonProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExpenseServiceTest {

    private ObligationServiceAPI obligationServiceMock;
    private ExpenseJsonProcessor processorMock;

    private ExpenseService expenseService;

    private Map<String, List<Expense>> fakeExpenses;

    @BeforeEach
    void setUp() {
        obligationServiceMock = mock(ObligationServiceAPI.class);
        processorMock = mock(ExpenseJsonProcessor.class);

        fakeExpenses = new HashMap<>();
        when(processorMock.loadData()).thenReturn(fakeExpenses);

        expenseService = new ExpenseService(obligationServiceMock, processorMock);
    }

    @Test
    void testGetUserExpenses_NoSuchUser_ReturnsEmptyList() {
        List<Expense> result = expenseService.getUserExpenses("alice");
        assertTrue(result.isEmpty(), "Should return empty list for unknown user");
    }

    @Test
    void testGetUserExpenses_NonEmpty() {
        List<Expense> bobList = new ArrayList<>();
        bobList.add(new Expense("bob", "Dinner", 50.0, Set.of("charlie"), LocalDateTime.now()));
        bobList.add(new Expense("bob", "Taxi", 20.0, Set.of("charlie", "alex"), LocalDateTime.now()));

        fakeExpenses.put("bob", bobList);

        List<Expense> result = expenseService.getUserExpenses("bob");
        assertEquals(2, result.size(), "Should return bob's 2 expenses");
        assertEquals("Dinner", result.get(0).description(), "Check first expense reason");
        assertEquals("Taxi", result.get(1).description(), "Check second expense reason");
    }


    @Test
    void testAddFriendExpense_BasicFlow() {
        Set<String> participants = Set.of("bob", "charlie");

        expenseService.addFriendExpense("alice", 100.0, "Trip cost", participants);

        assertTrue(fakeExpenses.containsKey("alice"), "alice must be in the map");
        List<Expense> aliceExpenses = fakeExpenses.get("alice");
        assertEquals(1, aliceExpenses.size(), "Should have exactly 1 expense added");
        Expense e = aliceExpenses.getFirst();
        assertEquals("alice", e.payerUsername(), "Payer is alice");
        assertEquals("Trip cost", e.description(), "Reason matches");
        assertEquals(100.0, e.amount(), 1e-9, "Amount is 100");
        assertTrue(e.participants().contains("bob") && e.participants().contains("charlie"),
            "Check participants in expense");

        double expectedSplit = 100.0 / 3.0;
        verify(obligationServiceMock).addObligation("bob", "alice", expectedSplit);
        verify(obligationServiceMock).addObligation("charlie", "alice", expectedSplit);

        verify(processorMock).saveData(fakeExpenses);
    }

    @Test
    void testGetExpensesAsString_UserHasExpenses() {
        List<Expense> alexList = new ArrayList<>();
        alexList.add(new Expense("alex", "Concert tickets", 80.0, Set.of("john"), LocalDateTime.of(2025, 5, 10, 20, 0)));
        fakeExpenses.put("alex", alexList);

        String result = expenseService.getExpensesAsString("alex");

        assertTrue(result.contains("****Expenses*****"), "Should contain the header");
        assertTrue(result.contains("Concert tickets"), "Should mention the reason");
        assertTrue(result.contains("alex"), "Should mention the payer");
        assertTrue(result.contains("john"), "Should mention participant");
    }

    @Test
    void testGetExpensesAsString_NoExpenses() {
        String result = expenseService.getExpensesAsString("bob");
        assertTrue(result.contains("****Expenses*****"), "Must contain header");
    }
}
