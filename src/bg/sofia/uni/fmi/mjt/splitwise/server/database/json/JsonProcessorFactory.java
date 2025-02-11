package bg.sofia.uni.fmi.mjt.splitwise.server.database.json;

public class JsonProcessorFactory {
    private static UserJsonProcessor userJsonProcessor;
    private static ObligationJsonProcessor obligationJsonProcessor;
    private static GroupJsonProcessor groupJsonProcessor;
    private static NotificationJsonProcessor notificationJsonProcessor;
    private static ExpenseJsonProcessor expenseJsonProcessor;

    private JsonProcessorFactory() {
    }

    public static UserJsonProcessor getUserJsonProcessor() {
        if (userJsonProcessor == null) {
            userJsonProcessor = new UserJsonProcessor();
        }
        return userJsonProcessor;
    }

    public static ObligationJsonProcessor getObligationJsonProcessor() {
        if (obligationJsonProcessor == null) {
            obligationJsonProcessor = new ObligationJsonProcessor();
        }
        return obligationJsonProcessor;
    }

    public static GroupJsonProcessor getGroupJsonProcessor() {
        if (groupJsonProcessor == null) {
            groupJsonProcessor = new GroupJsonProcessor();
        }
        return groupJsonProcessor;
    }

    public static NotificationJsonProcessor getNotificationJsonProcessor() {
        if (notificationJsonProcessor == null) {
            notificationJsonProcessor = new NotificationJsonProcessor();
        }
        return notificationJsonProcessor;
    }

    public static ExpenseJsonProcessor getExpenseJsonProcessor() {
        if (expenseJsonProcessor == null) {
            expenseJsonProcessor = new ExpenseJsonProcessor();
        }
        return expenseJsonProcessor;
    }

}
