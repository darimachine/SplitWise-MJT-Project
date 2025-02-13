package bg.sofia.uni.fmi.mjt.splitwise.server.database.json;

import bg.sofia.uni.fmi.mjt.splitwise.model.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

class ExpenseJsonProcessorTest {

    private ExpenseJsonProcessor processor;
    private static final String VALID_JSON =
        """
            {
               "user1": [
                  {
                     "payerUsername": "user1",
                     "description": "Dinner",
                     "amount": 25.0,
                     "participants": ["user2"],
                     "dateTime": null
                  }
               ]
            }
            """;

    @BeforeEach
    void setUp() {
        processor = new ExpenseJsonProcessor();
    }

    @Test
    void testLoadData_FileDoesNotExist_ReturnsDefaultData() throws IOException {
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(any(Path.class))).thenReturn(false);

            Map<String, List<Expense>> result = processor.loadData();
            assertNotNull(result, "Returned map should not be null.");
            assertTrue(result.isEmpty(), "Returned map should be empty when file is missing.");
        }
    }

    @Test
    void testLoadData_FileExistsWithValidJson_ReturnsParsedData() throws IOException {
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(any(Path.class))).thenReturn(true);

            Reader validJsonReader = new StringReader(VALID_JSON);
            BufferedReader bufferedReader = new BufferedReader(validJsonReader);

            filesMock.when(() -> Files.newBufferedReader(any(Path.class))).thenReturn(bufferedReader);

            Map<String, List<Expense>> loaded = processor.loadData();
            assertNotNull(loaded, "Returned data should not be null.");
            assertTrue(loaded.containsKey("user1"), "Parsed data should contain user1.");
            List<Expense> user1Expenses = loaded.get("user1");
            assertEquals(1, user1Expenses.size(), "Should have 1 expense for user1.");
            Expense e = user1Expenses.get(0);
            assertEquals("user1", e.payerUsername(), "Payer should be user1.");
            assertEquals("Dinner", e.description(), "Reason should be Dinner.");
            assertEquals(25.0, e.amount(), 0.0001, "Amount should be 25.0");
            assertTrue(e.participants().contains("user2"), "Participants should contain user2.");
            assertNull(e.dateTime(), "Time should be null as per JSON.");
        }
    }

    @Test
    void testSaveData_ThrowsRuntimeExceptionOnWriteFailure() throws IOException {
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.newBufferedWriter(any(Path.class)))
                .thenThrow(new IOException("Write failure"));

            Map<String, List<Expense>> dataToSave = new HashMap<>();
            dataToSave.put("user1", List.of(
                new Expense("user1", "Dinner", 25.0, new HashSet<>(List.of("user2")), null)
            ));

            assertThrows(RuntimeException.class, () -> processor.saveData(dataToSave),
                "Should throw RuntimeException on write failure.");
        }
    }
}
