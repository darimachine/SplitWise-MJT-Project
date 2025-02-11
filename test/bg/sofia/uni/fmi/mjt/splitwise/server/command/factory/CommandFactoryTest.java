package bg.sofia.uni.fmi.mjt.splitwise.server.command.factory;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.HelpCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.ShowStatusCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.authentication.*;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.currency.GetCurrentCurrencyCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.currency.SwitchCurrencyCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.expenses.ShowExpensesCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.friend.*;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.group.*;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.notification.ShowAllNotificationsCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.notification.ShowNewNotificationsCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.payment.ApprovePaymentCommand;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidatorFactory;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_COMMAND_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommandFactoryTest {

    private CommandFactory commandFactory;
    private CommandValidatorFactory commandValidatorFactoryMock;

    @BeforeEach
    void setUp() {
        AuthenticationManager authManagerMock = mock(AuthenticationManager.class);
        UserServiceAPI userServiceMock = mock(UserServiceAPI.class);
        FriendshipServiceAPI friendshipServiceMock = mock(FriendshipServiceAPI.class);
        GroupServiceAPI groupServiceMock = mock(GroupServiceAPI.class);
        ExpenseServiceAPI expenseServiceMock = mock(ExpenseServiceAPI.class);
        ObligationServiceAPI obligationServiceMock = mock(ObligationServiceAPI.class);
        NotificationServiceAPI notificationServiceMock = mock(NotificationServiceAPI.class);
        commandValidatorFactoryMock = mock(CommandValidatorFactory.class);

        commandFactory = new CommandFactory(authManagerMock, userServiceMock, friendshipServiceMock,
            groupServiceMock, expenseServiceMock, obligationServiceMock, notificationServiceMock, commandValidatorFactoryMock);
    }

    @Test
    void testCreateCommand_ValidCommands() {
        when(commandValidatorFactoryMock.getValidator(any())).thenReturn(mock(CommandValidator.class));

        assertInstanceOf(HelpCommand.class, commandFactory.createCommand("help"));
        assertInstanceOf(LoginCommand.class, commandFactory.createCommand("login"));
        assertInstanceOf(RegisterCommand.class, commandFactory.createCommand("register"));
        assertInstanceOf(LogOutCommand.class, commandFactory.createCommand("logout"));
        assertInstanceOf(AddFriendCommand.class, commandFactory.createCommand("add-friend"));
        assertInstanceOf(RemoveFriendCommand.class, commandFactory.createCommand("remove-friend"));
        assertInstanceOf(ShowFriendsCommand.class, commandFactory.createCommand("my-friends"));
        assertInstanceOf(AreFriendsCommand.class, commandFactory.createCommand("are-friends"));
        assertInstanceOf(CreateGroupCommand.class, commandFactory.createCommand("create-group"));
        assertInstanceOf(ShowGroupObligationsCommand.class, commandFactory.createCommand("my-groups"));
        assertInstanceOf(ShowGroupMembersCommand.class, commandFactory.createCommand("group-info"));
        assertInstanceOf(AddFriendToGroupCommand.class, commandFactory.createCommand("add-friend-to-group"));
        assertInstanceOf(RemoveFriendFromGroupCommand.class, commandFactory.createCommand("remove-friend-from-group"));
        assertInstanceOf(ShowUserInWhichGroupsParticipateCommand.class, commandFactory.createCommand("getUserGroups"));
        assertInstanceOf(ShowExpensesCommand.class, commandFactory.createCommand("my-expenses"));
        assertInstanceOf(SplitWithFriendCommand.class, commandFactory.createCommand("split"));
        assertInstanceOf(SplitWithGroupCommand.class, commandFactory.createCommand("split-group"));
        assertInstanceOf(ShowStatusCommand.class, commandFactory.createCommand("get-status"));
        assertInstanceOf(ApprovePaymentCommand.class, commandFactory.createCommand("payed"));
        assertInstanceOf(ShowAllNotificationsCommand.class, commandFactory.createCommand("all-notifications"));
        assertInstanceOf(ShowNewNotificationsCommand.class, commandFactory.createCommand("notifications"));
        assertInstanceOf(AddObligationWithFriendCommand.class, commandFactory.createCommand("add-obligation"));
        assertInstanceOf(FindObligationWithFriendCommand.class, commandFactory.createCommand("find-obligation"));
        assertInstanceOf(SwitchCurrencyCommand.class, commandFactory.createCommand("switch-currency"));
        assertInstanceOf(GetCurrentCurrencyCommand.class, commandFactory.createCommand("current-currency"));
    }

    @Test
    void testCreateCommand_InvalidCommandThrowsException() {
        String invalidCommand = "invalidCommand";
        InvalidCommandException thrown = assertThrows(InvalidCommandException.class,
            () -> commandFactory.createCommand(invalidCommand));

        assertEquals(INVALID_COMMAND_MESSAGE + invalidCommand, thrown.getMessage(),
            "Should throw an InvalidCommandException with proper message.");
    }
}
