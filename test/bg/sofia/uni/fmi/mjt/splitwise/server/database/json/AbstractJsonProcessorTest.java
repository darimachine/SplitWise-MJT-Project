package bg.sofia.uni.fmi.mjt.splitwise.server.database.json;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AbstractJsonProcessorTest {

    private static final Type LIST_TYPE = new TypeToken<List<String>>() {}.getType();

    @TempDir
    Path tempDir;
    private Path testFile;
    private AbstractJsonProcessor<List<String>> processor;

    @BeforeEach
    void setUp() {
        testFile = tempDir.resolve("test.json");
        processor = new AbstractJsonProcessor<>(testFile.toString(), LIST_TYPE) {
            @Override
            protected List<String> createDefaultData() {
                return List.of("default");
            }
        };
    }

    @Test
    void testLoadData_FileDoesNotExist_ReturnsDefaultData() {
        List<String> result = processor.loadData();
        assertEquals(List.of("default"), result, "Should return default data when file does not exist.");
    }

    @Test
    void testLoadData_FileExists_ReturnsSavedData() throws IOException {
        Files.writeString(testFile, "[\"data1\", \"data2\"]");

        List<String> result = processor.loadData();
        assertEquals(List.of("data1", "data2"), result, "Should return data read from file.");
    }

    @Test
    void testSaveData_WritesToFileCorrectly() throws IOException {
        List<String> dataToSave = List.of("value1", "value2");

        processor.saveData(dataToSave);

        String fileContent = Files.readString(testFile);
        assertTrue(fileContent.contains("value1") && fileContent.contains("value2"),
            "File content should contain the saved data.");
    }

    @Test
    void testSaveData_ThrowsExceptionOnWriteFailure() {
        Path invalidPath = tempDir.resolve("invalid/test.json"); // Invalid path
        AbstractJsonProcessor<List<String>> failingProcessor = new AbstractJsonProcessor<>(invalidPath.toString(), LIST_TYPE) {
            @Override
            protected List<String> createDefaultData() {
                return List.of("default");
            }
        };

        assertThrows(RuntimeException.class, () -> failingProcessor.saveData(List.of("test")),
            "Should throw RuntimeException when writing to an invalid path.");
    }
}
