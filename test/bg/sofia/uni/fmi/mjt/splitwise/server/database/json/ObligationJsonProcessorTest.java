package bg.sofia.uni.fmi.mjt.splitwise.server.database.json;

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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

class ObligationJsonProcessorTest {

    private ObligationJsonProcessor processor;
    private static final String VALID_JSON =
        """
            {
               "user1": {
                  "user2": 50.0
               }
            }
            """;

    @BeforeEach
    void setUp() {
        processor = new ObligationJsonProcessor();
    }

    @Test
    void testLoadData_FileDoesNotExist_ReturnsDefaultData() {
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(any(Path.class))).thenReturn(false);

            Map<String, Map<String, Double>> result = processor.loadData();

            assertNotNull(result, "Returned map should not be null.");
            assertTrue(result.isEmpty(), "Returned map should be empty when file is missing.");
        }
    }

    @Test
    void testLoadData_FileExistsWithValidJson_ReturnsParsedData() throws IOException {
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(any(Path.class))).thenReturn(true);

            // Provide a valid JSON  Reader
            Reader validJsonReader = new StringReader(VALID_JSON);
            BufferedReader bufferedReader = new BufferedReader(validJsonReader);

            // Mock newBufferedReader to return our bufferedReader
            filesMock.when(() -> Files.newBufferedReader(any(Path.class))).thenReturn(bufferedReader);

            Map<String, Map<String, Double>> loaded = processor.loadData();
            assertNotNull(loaded, "Returned data should not be null.");
            assertTrue(loaded.containsKey("user1"), "Parsed data should contain 'user1'.");
            assertEquals(50.0, loaded.get("user1").get("user2"), 0.0001, "Obligation amount should match.");
        }
    }

    @Test
    void testSaveData_ThrowsRuntimeExceptionOnWriteFailure() throws IOException {
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.newBufferedWriter(any(Path.class)))
                .thenThrow(new IOException("Write failure"));

            Map<String, Map<String, Double>> dataToSave = new HashMap<>();
            dataToSave.put("user1", Map.of("user2", 50.0));

            assertThrows(RuntimeException.class, () -> processor.saveData(dataToSave),
                "Should throw RuntimeException on write failure.");
        }
    }
}
