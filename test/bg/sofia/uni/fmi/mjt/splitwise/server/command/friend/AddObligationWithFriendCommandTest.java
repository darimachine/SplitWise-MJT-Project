package bg.sofia.uni.fmi.mjt.splitwise.server.command.friend;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AddObligationWithFriendCommandTest {

    private AddObligationWithFriendCommand command;
    private AuthenticationManager authManagerMock;
    private ObligationServiceAPI obligationServiceMock;
    private NotificationServiceAPI notificationServiceMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;
    private User userMock;

    private static final String[] VALID_ARGS = {"add-obligation", "friendUser", "50.0"};
    private static final String[] INVALID_ARGS = {"add-obligation", "friendUser", "invalidAmount"};
    private static final String CURRENT_USER = "testUser";
    private static final String FRIEND_USER = "friendUser";
    private static final double OBLIGATION_AMOUNT = 50.0;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        obligationServiceMock = mock(ObligationServiceAPI.class);
        notificationServiceMock = mock(NotificationServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        userMock = mock(User.class);

        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(userMock);
        when(userMock.getUsername()).thenReturn(CURRENT_USER);

        command = new AddObligationWithFriendCommand(authManagerMock, obligationServiceMock, notificationServiceMock, validatorMock);
    }

    @Test
    void testExecute_SuccessfullyAddsObligation() {
        String result = command.execute(VALID_ARGS, clientChannelMock);
        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
        verify(authManagerMock).getAuthenticatedUser(clientChannelMock);
        verify(obligationServiceMock).addObligation(CURRENT_USER, FRIEND_USER, OBLIGATION_AMOUNT);
        verify(notificationServiceMock).addNotification(
            CURRENT_USER + " added an obligation of " + OBLIGATION_AMOUNT + " to you.", FRIEND_USER);

        assertEquals("You added an obligation of " + OBLIGATION_AMOUNT + " to " + FRIEND_USER + ".", result,
            "The success message should be returned.");
    }

    @Test
    void testExecute_ValidatorIsCalled() {
        command.execute(VALID_ARGS, clientChannelMock);
        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
    }

    @Test
    void testExecute_AuthenticationManagerIsCalled() {
        command.execute(VALID_ARGS, clientChannelMock);
        verify(authManagerMock).getAuthenticatedUser(clientChannelMock);
    }

    @Test
    void testExecute_ObligationServiceIsCalled() {
        command.execute(VALID_ARGS, clientChannelMock);
        verify(obligationServiceMock).addObligation(CURRENT_USER, FRIEND_USER, OBLIGATION_AMOUNT);
    }

    @Test
    void testExecute_NotificationServiceIsCalled() {
        command.execute(VALID_ARGS, clientChannelMock);
        verify(notificationServiceMock).addNotification(
            CURRENT_USER + " added an obligation of " + OBLIGATION_AMOUNT + " to you.", FRIEND_USER);
    }

    @Test
    void testExecute_InvalidAmount_ThrowsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> command.execute(INVALID_ARGS, clientChannelMock),
            "Should throw NumberFormatException when an invalid amount format is provided.");
    }
}
