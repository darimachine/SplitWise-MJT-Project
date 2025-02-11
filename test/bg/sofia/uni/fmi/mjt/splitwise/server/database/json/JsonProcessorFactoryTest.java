package bg.sofia.uni.fmi.mjt.splitwise.server.database.json;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JsonProcessorFactoryTest {

    @Test
    void testGetUserJsonProcessor_ReturnsSameInstance() {
        UserJsonProcessor instance1 = JsonProcessorFactory.getUserJsonProcessor();
        UserJsonProcessor instance2 = JsonProcessorFactory.getUserJsonProcessor();

        assertNotNull(instance1, "UserJsonProcessor instance should not be null.");
        assertSame(instance1, instance2, "UserJsonProcessor should return the same singleton instance.");
    }

    @Test
    void testGetObligationJsonProcessor_ReturnsSameInstance() {
        ObligationJsonProcessor instance1 = JsonProcessorFactory.getObligationJsonProcessor();
        ObligationJsonProcessor instance2 = JsonProcessorFactory.getObligationJsonProcessor();

        assertNotNull(instance1, "ObligationJsonProcessor instance should not be null.");
        assertSame(instance1, instance2, "ObligationJsonProcessor should return the same singleton instance.");
    }

    @Test
    void testGetGroupJsonProcessor_ReturnsSameInstance() {
        GroupJsonProcessor instance1 = JsonProcessorFactory.getGroupJsonProcessor();
        GroupJsonProcessor instance2 = JsonProcessorFactory.getGroupJsonProcessor();

        assertNotNull(instance1, "GroupJsonProcessor instance should not be null.");
        assertSame(instance1, instance2, "GroupJsonProcessor should return the same singleton instance.");
    }

    @Test
    void testGetNotificationJsonProcessor_ReturnsSameInstance() {
        NotificationJsonProcessor instance1 = JsonProcessorFactory.getNotificationJsonProcessor();
        NotificationJsonProcessor instance2 = JsonProcessorFactory.getNotificationJsonProcessor();

        assertNotNull(instance1, "NotificationJsonProcessor instance should not be null.");
        assertSame(instance1, instance2, "NotificationJsonProcessor should return the same singleton instance.");
    }

    @Test
    void testGetExpenseJsonProcessor_ReturnsSameInstance() {
        ExpenseJsonProcessor instance1 = JsonProcessorFactory.getExpenseJsonProcessor();
        ExpenseJsonProcessor instance2 = JsonProcessorFactory.getExpenseJsonProcessor();

        assertNotNull(instance1, "ExpenseJsonProcessor instance should not be null.");
        assertSame(instance1, instance2, "ExpenseJsonProcessor should return the same singleton instance.");
    }
}
