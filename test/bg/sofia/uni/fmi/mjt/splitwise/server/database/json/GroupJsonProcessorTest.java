package bg.sofia.uni.fmi.mjt.splitwise.server.database.json;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;


class GroupJsonProcessorTest {

    private static final Path TEST_FILE_PATH = Path.of("resources/test_groups.json");
    private GroupJsonProcessor processor;
    @BeforeEach
    void setUp() {
        processor = new GroupJsonProcessor();
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(TEST_FILE_PATH);
    }

    @Test
    void testLoadData_FileDoesNotExist_ReturnsDefaultData() {
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(any(Path.class))).thenReturn(false);

            Map<String, Set<String>> result = processor.loadData();

            assertNotNull(result, "Returned map should not be null.");
            assertTrue(result.isEmpty(), "Returned map should be empty when file is missing.");
        }
    }

    @Test
    void testLoadData_FileExistsAndIsValid_ReturnsParsedData() throws IOException {
        String json = "{\"group1\": [\"user1\", \"user2\"]}";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(json));

        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            filesMock.when(() -> Files.newBufferedReader(any(Path.class))).thenReturn(bufferedReader);

            Map<String, Set<String>> loadedData = processor.loadData();
            assertNotNull(loadedData, "Loaded data should not be null.");
            assertTrue(loadedData.containsKey("group1"), "Loaded data should contain 'group1'.");
            assertEquals(Set.of("user1", "user2"), loadedData.get("group1"), "Group members should match.");
        }
    }

    @Test
    void testSaveData_ThrowsRuntimeExceptionOnWriteFailure() throws IOException {
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.newBufferedWriter(any(Path.class)))
                .thenThrow(new IOException("Write failure"));

            Map<String, Set<String>> dataToSave = new HashMap<>();
            dataToSave.put("Group1", Set.of("user1", "user2"));

            assertThrows(RuntimeException.class, () -> processor.saveData(dataToSave),
                "Should throw RuntimeException on write failure.");
        }
    }

}
