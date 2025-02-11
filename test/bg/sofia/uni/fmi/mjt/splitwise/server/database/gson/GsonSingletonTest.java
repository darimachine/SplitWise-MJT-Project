package bg.sofia.uni.fmi.mjt.splitwise.server.database.gson;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GsonSingletonTest {

    @Test
    void testGetInstance_ReturnsSameInstance() {
        Gson instance1 = GsonSingleton.getInstance();
        Gson instance2 = GsonSingleton.getInstance();

        assertNotNull(instance1, "Gson instance should not be null.");
        assertSame(instance1, instance2, "GsonSingleton should return the same instance (singleton pattern).");
    }

    @Test
    void testGsonSerialization_WithLocalDateTimeAdapter() {
        Gson gson = GsonSingleton.getInstance();
        LocalDateTime now = LocalDateTime.of(2024, 2, 8, 14, 30, 45);
        String json = gson.toJson(now);

        assertNotNull(json, "Serialized JSON should not be null.");
        assertTrue(json.contains("2024-02-08 14:30:45"),
            "Serialized JSON should contain the formatted LocalDateTime string.");
    }

    @Test
    void testGsonDeserialization_WithLocalDateTimeAdapter() {
        Gson gson = GsonSingleton.getInstance();
        String json = "\"2024-02-08 14:30:45\"";
        LocalDateTime expected = LocalDateTime.of(2024, 2, 8, 14, 30, 45);
        LocalDateTime deserialized = gson.fromJson(json, LocalDateTime.class);

        assertEquals(expected, deserialized, "Deserialized LocalDateTime should match expected object.");
    }
}
