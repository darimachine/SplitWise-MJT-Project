package bg.sofia.uni.fmi.mjt.splitwise.server.database.json;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
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

class UserJsonProcessorTest {

    private UserJsonProcessor processor;
    private static final String VALID_JSON =
        """
            {
                "alex": {
                   "username": "alex",
                   "password": "password123",
                   "firstName": "Alex",
                   "lastName": "Johnson",
                   "friends": [
                     "maria",
                     "john"
                   ],
                   "groups": [
                     "Roommates",
                     "Random",
                     "Bebe"
                   ],
                   "preferredCurrency": "BGN"
                 }
            }
            """;

    @BeforeEach
    void setUp() {
        processor = new UserJsonProcessor();
    }

    @Test
    void testLoadData_FileDoesNotExist_ReturnsDefaultData() {
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(any(Path.class))).thenReturn(false);

            Map<String, User> result = processor.loadData();

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

            Map<String, User> loaded = processor.loadData();
            assertNotNull(loaded, "Returned data should not be null.");
            assertTrue(loaded.containsKey("alex"), "Parsed data should contain 'alex'.");
            User user = loaded.get("alex");
            assertEquals("alex", user.getUsername(), "Username should be 'alex'.");
            assertEquals("password123", user.getPassword(), "Password should match.");
            assertEquals("Alex", user.getFirstName(), "First name should be Alex.");
            assertEquals("Johnson", user.getLastName(), "Last name should be Johnson.");
        }
    }

    @Test
    void testSaveData_ThrowsRuntimeExceptionOnWriteFailure() throws IOException {
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.newBufferedWriter(any(Path.class)))
                .thenThrow(new IOException("Write failure"));

            Map<String, User> dataToSave = new HashMap<>();
            dataToSave.put("alex", new User("alex", "password123", "Alex", "Johnson"));

            assertThrows(RuntimeException.class, () -> processor.saveData(dataToSave),
                "Should throw RuntimeException on write failure.");
        }
    }
}
