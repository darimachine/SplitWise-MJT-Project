package bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators;

import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

class ServiceValidatorFactoryTest {

    private UserServiceAPI userServiceMock;
    private ObligationServiceAPI obligationServiceMock;
    private GroupServiceAPI groupServiceMock;
    private ExpenseServiceAPI expenseServiceMock;
    private NotificationServiceAPI notificationServiceMock;

    private ServiceValidatorFactory serviceValidatorFactory;

    @BeforeEach
    void setUp() {
        userServiceMock = mock(UserServiceAPI.class);
        obligationServiceMock = mock(ObligationServiceAPI.class);
        groupServiceMock = mock(GroupServiceAPI.class);
        expenseServiceMock = mock(ExpenseServiceAPI.class);
        notificationServiceMock = mock(NotificationServiceAPI.class);

        // Reset singleton instance for isolated tests
        ServiceValidatorFactory.getInstance(userServiceMock, obligationServiceMock, groupServiceMock,
            expenseServiceMock, notificationServiceMock);
        serviceValidatorFactory = ServiceValidatorFactory.getInstance(userServiceMock, obligationServiceMock,
            groupServiceMock, expenseServiceMock, notificationServiceMock);
    }

    @Test
    void testGetInstance_CreatesSingleton() {
        ServiceValidatorFactory instance1 = ServiceValidatorFactory.getInstance(userServiceMock, obligationServiceMock,
            groupServiceMock, expenseServiceMock, notificationServiceMock);
        ServiceValidatorFactory instance2 = ServiceValidatorFactory.getInstance(userServiceMock, obligationServiceMock,
            groupServiceMock, expenseServiceMock, notificationServiceMock);

        assertSame(instance1, instance2, "ServiceValidatorFactory should return the same singleton instance.");
    }

    @Test
    void testGetUserValidator_ReturnsNotNull() {
        assertNotNull(serviceValidatorFactory.getUserValidator(),
            "UserValidator should not be null.");
    }

    @Test
    void testGetGroupValidator_ReturnsNotNull() {
        assertNotNull(serviceValidatorFactory.getGroupValidator(),
            "GroupValidator should not be null.");
    }

    @Test
    void testGetObligationValidator_ReturnsNotNull() {
        assertNotNull(serviceValidatorFactory.getObligationValidator(),
            "ObligationValidator should not be null.");
    }

    @Test
    void testGetFriendshipValidator_ReturnsNotNull() {
        assertNotNull(serviceValidatorFactory.getFriendshipValidator(),
            "FriendshipValidator should not be null.");
    }

    @Test
    void testGetNotificationValidator_ReturnsNotNull() {
        assertNotNull(serviceValidatorFactory.getNotificationValidator(),
            "NotificationValidator should not be null.");
    }

    @Test
    void testGetExpenseValidator_ReturnsNotNull() {
        assertNotNull(serviceValidatorFactory.getExpenseValidator(),
            "ExpenseValidator should not be null.");
    }
}
