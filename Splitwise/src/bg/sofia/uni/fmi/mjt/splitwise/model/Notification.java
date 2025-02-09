package bg.sofia.uni.fmi.mjt.splitwise.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Notification {

    private final String message;
    private final LocalDateTime timestamp;
    private boolean seen;

    public Notification(String message) {
        this(message, LocalDateTime.now(), false);
    }

    // full constructor if needed
    public Notification(String message, LocalDateTime timestamp, boolean seen) {

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Notification message cannot be null/blank");
        }
        this.message = message;
        this.timestamp = timestamp;
        this.seen = seen;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void markAsSeen() {
        this.seen = true;
    }

    @Override
    public String toString() {
        return String.format("Notification [message='%s', time=%s]",
            message, timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return seen == that.seen && Objects.equals(message, that.message) &&
            Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, timestamp, seen);
    }
}