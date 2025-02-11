package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators;

import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;

public class ServiceValidatorFactory {

    private static ServiceValidatorFactory instance;
    private final UserValidator userValidator;
    private final GroupValidator groupValidator;
    private final ObligationValidator obligationValidator;
    private final FriendshipValidator friendshipValidator;
    private final NotificationValidator notificationValidator;
    private final ExpenseValidator expenseValidator;

    private ServiceValidatorFactory(UserServiceAPI userService, ObligationServiceAPI obligationService,
                                    GroupServiceAPI groupService, ExpenseServiceAPI expenseService,
                                    NotificationServiceAPI notificationService) {

        this.userValidator = new UserValidator(userService);
        this.groupValidator = new GroupValidator(groupService);
        this.obligationValidator = new ObligationValidator(obligationService);
        this.friendshipValidator = new FriendshipValidator(userService, obligationService);
        this.notificationValidator = new NotificationValidator();
        this.expenseValidator = new ExpenseValidator(expenseService, userValidator);
    }

    public static synchronized ServiceValidatorFactory getInstance(UserServiceAPI userService,
                                                                   ObligationServiceAPI obligationService,
                                                                   GroupServiceAPI groupService,
                                                                   ExpenseServiceAPI expenseService,
                                                                   NotificationServiceAPI notificationService) {
        if (instance == null) {
            instance = new ServiceValidatorFactory(userService, obligationService, groupService, expenseService, notificationService);
        }
        return instance;
    }

    public UserValidator getUserValidator() {
        return userValidator;
    }

    public GroupValidator getGroupValidator() {
        return groupValidator;
    }

    public ObligationValidator getObligationValidator() {
        return obligationValidator;
    }

    public FriendshipValidator getFriendshipValidator() {
        return friendshipValidator;
    }

    public NotificationValidator getNotificationValidator() {
        return notificationValidator;
    }

    public ExpenseValidator getExpenseValidator() {
        return expenseValidator;
    }
}
