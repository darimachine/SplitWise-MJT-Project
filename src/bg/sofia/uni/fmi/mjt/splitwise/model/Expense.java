package bg.sofia.uni.fmi.mjt.splitwise.model;

import java.time.LocalDateTime;
import java.util.Set;

public record Expense(
    String payerUsername,
    String description,
    double amount,
    Set<String> participants,
    LocalDateTime dateTime) {
    @Override
    public String toString() {
        return String.format("%s paid %.2f for %s. (Parcipants: %s) [%s]",
            payerUsername,
            amount,
            description,
            String.join(", ", participants),
            dateTime);
    }
}