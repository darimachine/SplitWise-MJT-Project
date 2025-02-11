package bg.sofia.uni.fmi.mjt.splitwise.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationTest {

    @Test
    void testConstructor_ValidMessage_SetsFieldsCorrectly() {
        String message = "You have a new expense!";
        Notification notification = new Notification(message);

        assertEquals(message, notification.getMessage(), "Message should be set correctly.");
        assertNotNull(notification.getTimestamp(), "Timestamp should be initialized.");
        assertFalse(notification.isSeen(), "Notification should be unseen by default.");
    }

    @Test
    void testConstructor_WithTimestampAndSeenStatus_SetsFieldsCorrectly() {
        String message = "Payment approved!";
        LocalDateTime timestamp = LocalDateTime.of(2024, 2, 1, 12, 30);
        boolean seen = true;

        Notification notification = new Notification(message, timestamp, seen);

        assertEquals(message, notification.getMessage(), "Message should be set correctly.");
        assertEquals(timestamp, notification.getTimestamp(), "Timestamp should match the provided one.");
        assertTrue(notification.isSeen(), "Seen status should match the provided one.");
    }

    @Test
    void testConstructor_NullOrBlankMessage_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Notification(null),
            "Should throw an exception when message is null.");
        assertThrows(IllegalArgumentException.class, () -> new Notification("  "),
            "Should throw an exception when message is blank.");
    }

    @Test
    void testMarkAsSeen_UpdatesSeenStatus() {
        Notification notification = new Notification("Test notification");

        assertFalse(notification.isSeen(), "Notification should initially be unseen.");
        notification.markAsSeen();
        assertTrue(notification.isSeen(), "Notification should be marked as seen.");
    }

    @Test
    void testToString_ReturnsCorrectFormat() {
        String message = "Group added!";
        LocalDateTime timestamp = LocalDateTime.of(2024, 2, 10, 15, 45);
        Notification notification = new Notification(message, timestamp, false);

        String expected = String.format("Notification [message='%s', time=%s]",
            message, timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        assertEquals(expected, notification.toString(), "toString() should return correctly formatted string.");
    }

    @Test
    void testEqualsAndHashCode_SameValues_AreEqual() {
        String message = "Reminder: Pay debt!";
        LocalDateTime timestamp = LocalDateTime.of(2024, 2, 5, 10, 0);
        boolean seen = false;

        Notification n1 = new Notification(message, timestamp, seen);
        Notification n2 = new Notification(message, timestamp, seen);

        assertEquals(n1, n2, "Equal notifications should be considered equal.");
        assertEquals(n1.hashCode(), n2.hashCode(), "Hash codes of equal notifications should match.");
    }

    @Test
    void testEquals_DifferentValues_NotEqual() {
        Notification n1 = new Notification("Reminder", LocalDateTime.of(2024, 2, 5, 10, 0), false);
        Notification n2 = new Notification("Different message", LocalDateTime.of(2024, 2, 5, 10, 0), false);
        Notification n3 = new Notification("Reminder", LocalDateTime.of(2024, 2, 6, 10, 0), false);
        Notification n4 = new Notification("Reminder", LocalDateTime.of(2024, 2, 5, 10, 0), true);

        assertNotEquals(n1, n2, "Notifications with different messages should not be equal.");
        assertNotEquals(n1, n3, "Notifications with different timestamps should not be equal.");
        assertNotEquals(n1, n4, "Notifications with different seen status should not be equal.");
    }

    @Test
    void testEquals_DifferentObjectTypes_NotEqual() {
        Notification notification = new Notification("Test");
        String otherObject = "I am not a notification";

        assertNotEquals(notification, otherObject, "Notification should not be equal to an object of different type.");
    }

    @Test
    void testHashCode_DifferentValues_AreDifferent() {
        Notification n1 = new Notification("Message 1", LocalDateTime.now(), false);
        Notification n2 = new Notification("Message 2", LocalDateTime.now(), false);

        assertNotEquals(n1.hashCode(), n2.hashCode(), "Different notifications should have different hash codes.");
    }
}
