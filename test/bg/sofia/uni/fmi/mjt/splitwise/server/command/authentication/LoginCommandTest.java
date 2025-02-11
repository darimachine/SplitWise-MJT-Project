package bg.sofia.uni.fmi.mjt.splitwise.server.command.authentication;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidPasswordException;

import bg.sofia.uni.fmi.mjt.splitwise.model.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.model.enums.Currency;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginCommandTest {

    private LoginCommand loginCommand;
    private AuthenticationManager authManagerMock;
    private CommandValidator validatorMock;
    private NotificationServiceAPI notificationServiceMock;
    private SocketChannel clientChannelMock;

    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "SecurePass123";
    private static final String[] VALID_ARGS = {"login", USERNAME, PASSWORD};

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        validatorMock = mock(CommandValidator.class);
        notificationServiceMock = mock(NotificationServiceAPI.class);
        clientChannelMock = mock(SocketChannel.class);
        loginCommand = new LoginCommand(authManagerMock, notificationServiceMock, validatorMock);
    }

    @Test
    void testExecute_SuccessfulLoginWithNoNotifications() {
        when(authManagerMock.login(clientChannelMock, USERNAME, PASSWORD)).thenReturn(true);
        when(notificationServiceMock.getUnseenNotificationsForUser(USERNAME)).thenReturn(List.of());

        User mockUser = mock(User.class);
        when(mockUser.getPreferredCurrency()).thenReturn(Currency.USD);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);

        String result = loginCommand.execute(VALID_ARGS, clientChannelMock);

        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
        verify(authManagerMock).login(clientChannelMock, USERNAME, PASSWORD);
        verify(notificationServiceMock).getUnseenNotificationsForUser(USERNAME);
        verify(notificationServiceMock).markNotificationsAsSeen(USERNAME, List.of());
        assertTrue(result.contains("Welcome, " + USERNAME + "!"), "Welcome message should contain the username.");
        assertTrue(result.contains("No new notifications"), "Message should indicate no notifications.");
        assertTrue(result.contains("Your preferred currency is: USD"), "Should show correct preferred currency.");
    }

    @Test
    void testExecute_SuccessfulLoginWithNotifications() {
        Notification notification1 =
            new Notification("Expense added You owe 10 USD to Alex");
        Notification notification2 =
            new Notification("Payment received You received 15 USD from Maria");
        List<Notification> notifications = List.of(notification1, notification2);

        when(authManagerMock.login(clientChannelMock, USERNAME, PASSWORD)).thenReturn(true);
        when(notificationServiceMock.getUnseenNotificationsForUser(USERNAME)).thenReturn(notifications);

        User mockUser = mock(User.class);
        when(mockUser.getPreferredCurrency()).thenReturn(Currency.EUR);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);

        String result = loginCommand.execute(VALID_ARGS, clientChannelMock);

        verify(notificationServiceMock).markNotificationsAsSeen(USERNAME, notifications);
        assertTrue(result.contains(notification1.toString()), "Should contain first notification message.");
        assertTrue(result.contains(notification2.toString()), "Should contain second notification message.");
        assertTrue(result.contains("Your preferred currency is: EUR"), "Should display correct preferred currency.");
    }

    @Test
    void testExecute_InvalidPassword_ThrowsException() {
        when(authManagerMock.login(clientChannelMock, USERNAME, PASSWORD)).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> loginCommand.execute(VALID_ARGS, clientChannelMock),
            "Should throw InvalidPasswordException on failed login.");
    }

    @Test
    void testAuthenticateUser_InvalidPassword_ThrowsException() {
        when(authManagerMock.login(clientChannelMock, USERNAME, PASSWORD)).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> loginCommand.execute(VALID_ARGS, clientChannelMock),
            "Invalid password should throw an exception.");
    }

    @Test
    void testExecute_ValidatorIsCalled() {
        when(authManagerMock.login(clientChannelMock, USERNAME, PASSWORD)).thenReturn(true);
        User mockUser = mock(User.class);
        when(mockUser.getPreferredCurrency()).thenReturn(Currency.USD);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(mockUser);
        when(notificationServiceMock.getUnseenNotificationsForUser(USERNAME)).thenReturn(List.of());

        loginCommand.execute(VALID_ARGS, clientChannelMock);

        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
    }
}
