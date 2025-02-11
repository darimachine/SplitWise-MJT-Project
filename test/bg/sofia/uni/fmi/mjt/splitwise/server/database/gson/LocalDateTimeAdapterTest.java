package bg.sofia.uni.fmi.mjt.splitwise.server.database.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class LocalDateTimeAdapterTest {

    private LocalDateTimeAdapter adapter;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        adapter = new LocalDateTimeAdapter();
    }

    @Test
    void testSerialize_ValidLocalDateTime() {
        LocalDateTime now = LocalDateTime.of(2024, 2, 8, 14, 30, 45);
        JsonElement result = adapter.serialize(now, LocalDateTime.class, mock(JsonSerializationContext.class));

        assertEquals(now.format(FORMATTER), result.getAsString(),
            "Serialized LocalDateTime should match expected string format.");
    }

    @Test
    void testDeserialize_ValidJsonElement() {
        String dateTimeString = "2024-02-08 14:30:45";
        JsonElement jsonElement = new JsonPrimitive(dateTimeString);
        LocalDateTime expectedDateTime = LocalDateTime.parse(dateTimeString, FORMATTER);

        LocalDateTime result =
            adapter.deserialize(jsonElement, LocalDateTime.class, mock(JsonDeserializationContext.class));

        assertEquals(expectedDateTime, result,
            "Deserialized LocalDateTime should match expected LocalDateTime object.");
    }

    @Test
    void testDeserialize_InvalidFormat_ThrowsException() {
        JsonElement invalidJsonElement = new JsonPrimitive("invalid-date-time");

        assertThrows(Exception.class,
            () -> adapter.deserialize(invalidJsonElement, LocalDateTime.class, mock(JsonDeserializationContext.class)),
            "Should throw an exception for incorrectly formatted date-time strings.");
    }

    @Test
    void testSerialize_NullValue_ThrowsException() {
        assertThrows(NullPointerException.class,
            () -> adapter.serialize(null, LocalDateTime.class, mock(JsonSerializationContext.class)),
            "Should throw NullPointerException when serializing null value.");
    }

    @Test
    void testDeserialize_NullValue_ThrowsException() {
        assertThrows(NullPointerException.class,
            () -> adapter.deserialize(null, LocalDateTime.class, mock(JsonDeserializationContext.class)),
            "Should throw NullPointerException when deserializing null value.");
    }
}
