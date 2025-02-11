package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators;

import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.authentication.LogOutCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.authentication.LoginCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.authentication.RegisterCommandValidator;

import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.currency.GetCurrentCurrencyCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.currency.SwitchCurrencyCommandValidator;

import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.expenses.ShowExpensesCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.friend.AddFriendCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators
    .friend.AddObligationWithFriendCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.friend.AreFriendsCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators
    .friend.FindObligationWithFriendCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators
    .friend.RemoveFriendCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators
    .friend.ShowFriendsCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators
    .friend.SplitWithFriendCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators
    .group.AddFriendToGroupCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators
    .group.CreateGroupCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators
    .group.RemoveFriendFromGroupCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators
    .group.ShowGroupMembersCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators
    .group.ShowGroupObligationsCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators
    .group.ShowUserInWhichGroupsParticipateCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators
    .group.SplitWithGroupCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators
    .notification.ShowAllNotificationsCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators
    .notification.ShowNewNotificationsCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators
    .payment.ApprovePaymentCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.ServiceValidatorFactory;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.ExpenseValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.FriendshipValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.GroupValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.NotificationValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.ObligationValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.UserValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandValidatorFactory {
    private final Map<String, CommandValidator> validators;

    private static class Holder {
        private static CommandValidatorFactory instance;
    }

    // **Singleton Constructor**
    private CommandValidatorFactory(AuthenticationManager authManager, ServiceValidatorFactory validatorFactory) {

        validators = new ConcurrentHashMap<>();

        UserValidator userValidator = validatorFactory.getUserValidator();
        ExpenseValidator expenseValidator = validatorFactory.getExpenseValidator();
        GroupValidator groupValidator = validatorFactory.getGroupValidator();
        FriendshipValidator friendshipValidator = validatorFactory.getFriendshipValidator();
        ObligationValidator obligationValidator = validatorFactory.getObligationValidator();
        NotificationValidator notificationValidator = validatorFactory.getNotificationValidator();

        validators.put("register", new RegisterCommandValidator(authManager, userValidator));
        validators.put("login", new LoginCommandValidator(authManager, userValidator));
        validators.put("logout", new LogOutCommandValidator(authManager));
        validators.put("add-friend", new AddFriendCommandValidator(friendshipValidator, authManager));
        validators.put("remove-friend", new RemoveFriendCommandValidator(authManager, friendshipValidator));
        validators.put("my-friends", new ShowFriendsCommandValidator(authManager, friendshipValidator));
        validators.put("are-friends", new AreFriendsCommandValidator(authManager, friendshipValidator));
        validators.put("create-group", new CreateGroupCommandValidator(authManager, groupValidator, userValidator));
        validators.put("my-groups", new ShowGroupObligationsCommandValidator(authManager, userValidator));
        validators.put("group-info", new ShowGroupMembersCommandValidator(authManager, groupValidator));
        validators.put("my-expenses", new ShowExpensesCommandValidator(authManager));
        validators.put("add-friend-to-group",
            new AddFriendToGroupCommandValidator(authManager, groupValidator, userValidator, friendshipValidator));
        validators.put("remove-friend-from-group",
            new RemoveFriendFromGroupCommandValidator(authManager, groupValidator, userValidator));
        validators.put("getUserGroups",
            new ShowUserInWhichGroupsParticipateCommandValidator(authManager, userValidator));
        validators.put("split",
            new SplitWithFriendCommandValidator(authManager, expenseValidator, friendshipValidator));
        validators.put("split-group",
            new SplitWithGroupCommandValidator(authManager, expenseValidator, groupValidator));
        validators.put("payed", new ApprovePaymentCommandValidator(authManager, obligationValidator, userValidator));
        validators.put("all-notifications",
            new ShowAllNotificationsCommandValidator(authManager, notificationValidator));
        validators.put("notifications", new ShowNewNotificationsCommandValidator(authManager, notificationValidator));
        validators.put("add-obligation",
            new AddObligationWithFriendCommandValidator(authManager, userValidator, obligationValidator));
        validators.put("find-obligation",
            new FindObligationWithFriendCommandValidator(authManager, userValidator, obligationValidator));
        validators.put("switch-currency", new SwitchCurrencyCommandValidator(authManager));
        validators.put("current-currency", new GetCurrentCurrencyCommandValidator(authManager));

    }

    public static synchronized CommandValidatorFactory getInstance(AuthenticationManager authManager,
                                                                   ServiceValidatorFactory validatorFactory) {
        if (Holder.instance == null) {
            Holder.instance = new CommandValidatorFactory(authManager, validatorFactory);
        }
        return Holder.instance;
    }

    public CommandValidator getValidator(String command) {
        return validators.get(command);
    }
}