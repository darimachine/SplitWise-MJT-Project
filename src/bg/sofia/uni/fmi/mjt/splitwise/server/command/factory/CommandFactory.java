package bg.sofia.uni.fmi.mjt.splitwise.server.command.factory;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.HelpCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.ShowStatusCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.authentication.LogOutCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.authentication.LoginCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.authentication.RegisterCommand;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.currency.GetCurrentCurrencyCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.currency.SwitchCurrencyCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.expenses.ShowExpensesCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.friend.AddFriendCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.friend.AddObligationWithFriendCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.friend.AreFriendsCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.friend.FindObligationWithFriendCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.friend.RemoveFriendCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.friend.ShowFriendsCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.friend.SplitWithFriendCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.group.AddFriendToGroupCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.group.CreateGroupCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.group.RemoveFriendFromGroupCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.group.ShowGroupMembersCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.group.ShowGroupObligationsCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.group.ShowUserInWhichGroupsParticipateCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.group.SplitWithGroupCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.notification.ShowAllNotificationsCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.notification.ShowNewNotificationsCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.payment.ApprovePaymentCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidatorFactory;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_COMMAND_MESSAGE;

public class CommandFactory {


    private final AuthenticationManager authManager;
    private final UserServiceAPI userService;
    private final FriendshipServiceAPI friendshipService;
    private final GroupServiceAPI groupService;
    private final ExpenseServiceAPI expenseService;
    private final ObligationServiceAPI obligationService;
    private final NotificationServiceAPI notificationService;
    private final CommandValidatorFactory commandValidatorFactory;

    public CommandFactory(AuthenticationManager authManager, UserServiceAPI userService,
                          FriendshipServiceAPI friendshipService, GroupServiceAPI groupService,
                          ExpenseServiceAPI expenseService, ObligationServiceAPI obligationService,
                          NotificationServiceAPI notificationService, CommandValidatorFactory commandValidatorFactory) {
        this.authManager = authManager;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.groupService = groupService;
        this.expenseService = expenseService;
        this.obligationService = obligationService;
        this.notificationService = notificationService;
        this.commandValidatorFactory = commandValidatorFactory;
    }

    public Command createCommand(String commandName) {
        return switch (commandName) {
            case "help" -> new HelpCommand();
            case "login" ->
                new LoginCommand(authManager, notificationService, commandValidatorFactory.getValidator("login"));
            case "register" -> new RegisterCommand(userService, commandValidatorFactory.getValidator("register"));
            case "logout" -> new LogOutCommand(authManager, commandValidatorFactory.getValidator("logout"));
            case "add-friend" -> new AddFriendCommand(authManager, friendshipService, notificationService,
                commandValidatorFactory.getValidator("add-friend"));
            case "remove-friend" -> new RemoveFriendCommand(authManager, friendshipService,
                commandValidatorFactory.getValidator("remove-friend"));
            case "my-friends" -> new ShowFriendsCommand(authManager, obligationService,
                commandValidatorFactory.getValidator("my-friends"));
            case "are-friends" ->
                new AreFriendsCommand(friendshipService, commandValidatorFactory.getValidator("are-friends"));
            case "create-group" -> new CreateGroupCommand(authManager, groupService, notificationService, userService,
                commandValidatorFactory.getValidator("create-group"));
            case "my-groups" -> new ShowGroupObligationsCommand(authManager, groupService, obligationService,
                commandValidatorFactory.getValidator("my-groups"));
            case "group-info" ->
                new ShowGroupMembersCommand(groupService, commandValidatorFactory.getValidator("group-info"));
            case "add-friend-to-group" ->
                new AddFriendToGroupCommand(authManager, userService, groupService, notificationService,
                    commandValidatorFactory.getValidator("add-friend-to-group"));
            case "remove-friend-from-group" -> new RemoveFriendFromGroupCommand(authManager, userService, groupService,
                notificationService, commandValidatorFactory.getValidator("remove-friend-from-group"));
            case "getUserGroups" -> new ShowUserInWhichGroupsParticipateCommand(userService,
                commandValidatorFactory.getValidator("getUserGroups"));
            case "my-expenses" -> new ShowExpensesCommand(authManager, expenseService,
                commandValidatorFactory.getValidator("my-expenses"));
            case "split" -> new SplitWithFriendCommand(authManager, expenseService,
                notificationService, commandValidatorFactory.getValidator("split"));
            case "split-group" ->
                new SplitWithGroupCommand(authManager, groupService, expenseService, notificationService,
                    commandValidatorFactory.getValidator("split-group"));
            case "get-status" -> new ShowStatusCommand(new ShowFriendsCommand(authManager, obligationService,
                commandValidatorFactory.getValidator("my-friends")),
                new ShowGroupObligationsCommand(authManager, groupService, obligationService,
                    commandValidatorFactory.getValidator("my-groups")));
            case "payed" -> new ApprovePaymentCommand(authManager, obligationService, notificationService,
                commandValidatorFactory.getValidator("payed"));
            case "all-notifications" -> new ShowAllNotificationsCommand(authManager, notificationService,
                commandValidatorFactory.getValidator("all-notifications"));
            case "notifications" -> new ShowNewNotificationsCommand(authManager, notificationService,
                commandValidatorFactory.getValidator("notifications"));
            case "add-obligation" ->
                new AddObligationWithFriendCommand(authManager, obligationService, notificationService,
                    commandValidatorFactory.getValidator("add-obligation"));
            case "find-obligation" -> new FindObligationWithFriendCommand(authManager, obligationService,
                commandValidatorFactory.getValidator("find-obligation"));
            case "switch-currency" -> new SwitchCurrencyCommand(authManager, userService,
                commandValidatorFactory.getValidator("switch-currency"));
            case "current-currency" -> new GetCurrentCurrencyCommand(authManager,
                commandValidatorFactory.getValidator("current-currency"));
            default -> throw new InvalidCommandException(INVALID_COMMAND_MESSAGE + commandName);
        };
    }
}
