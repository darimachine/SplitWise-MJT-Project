package bg.sofia.uni.fmi.mjt.splitwise.server.database.json;

import bg.sofia.uni.fmi.mjt.splitwise.model.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

class NotificationJsonProcessorTest {

    private NotificationJsonProcessor processor;
    private static final String VALID_JSON =
        """
            {
               "user1": [
                  {
                     "message": "New Expense Added",
                     "timestamp": "2025-02-07 22:28:38",
                     "seen": false
                  }
               ]
            }
            """;

    @BeforeEach
    void setUp() {
        processor = new NotificationJsonProcessor();
    }

    @Test
    void testLoadData_FileDoesNotExist_ReturnsDefaultData() {
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(any(Path.class))).thenReturn(false);

            Map<String, List<Notification>> result = processor.loadData();

            assertNotNull(result, "Returned map should not be null.");
            assertTrue(result.isEmpty(), "Returned map should be empty when file is missing.");
        }
    }

    @Test
    void testLoadData_FileExistsWithValidJson_ReturnsParsedData() throws IOException {
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(any(Path.class))).thenReturn(true);

            // Provide a valid JSON via a Reader
            Reader validJsonReader = new StringReader(VALID_JSON);
            BufferedReader bufferedReader = new BufferedReader(validJsonReader);

            // Mock newBufferedReader to return our bufferedReader
            filesMock.when(() -> Files.newBufferedReader(any(Path.class))).thenReturn(bufferedReader);

            Map<String, List<Notification>> loaded = processor.loadData();
            assertNotNull(loaded, "Returned data should not be null.");
            assertTrue(loaded.containsKey("user1"), "Parsed data should contain user1.");
            List<Notification> user1Notifications = loaded.get("user1");
            assertEquals(1, user1Notifications.size(), "Should have 1 notification for user1.");
            Notification notification = user1Notifications.get(0);
            assertEquals("New Expense Added", notification.getMessage(), "Notification message should match.");
            assertFalse(notification.isSeen(), "Notification should be unseen.");
        }
    }

    @Test
    void testSaveData_ThrowsRuntimeExceptionOnWriteFailure() throws IOException {
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.newBufferedWriter(any(Path.class)))
                .thenThrow(new IOException("Write failure"));

            Map<String, List<Notification>> dataToSave = new HashMap<>();
            dataToSave.put("user1", List.of(new Notification("New Expense Added",LocalDateTime.now(), false)));

            assertThrows(RuntimeException.class, () -> processor.saveData(dataToSave),
                "Should throw RuntimeException on write failure.");
        }
    }
}
