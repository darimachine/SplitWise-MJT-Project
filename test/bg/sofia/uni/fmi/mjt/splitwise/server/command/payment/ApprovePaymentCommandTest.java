package bg.sofia.uni.fmi.mjt.splitwise.server.command.payment;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApprovePaymentCommandTest {

    private static final String PAYER_USERNAME = "payer123";
    private static final String LOGGED_USERNAME = "receiver456";
    private static final double PAYMENT_AMOUNT = 50.00;
    private static final String[] VALID_ARGS = {"approve-payment", PAYER_USERNAME, String.valueOf(PAYMENT_AMOUNT)};

    private ObligationServiceAPI obligationServiceMock;
    private NotificationServiceAPI notificationServiceMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;
    private ApprovePaymentCommand command;

    @BeforeEach
    void setUp() {
        AuthenticationManager authManagerMock = mock(AuthenticationManager.class);
        obligationServiceMock = mock(ObligationServiceAPI.class);
        notificationServiceMock = mock(NotificationServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);

        command =
            new ApprovePaymentCommand(authManagerMock, obligationServiceMock, notificationServiceMock, validatorMock);

        User userMock = mock(User.class);
        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(userMock);
        when(userMock.getUsername()).thenReturn(LOGGED_USERNAME);
        when(userMock.getFullName()).thenReturn("John Doe");
    }

    @Test
    void testExecute_ValidArguments_PaysObligationAndSendsNotification() {
        String expectedMessage = PAYER_USERNAME + " succesfully payed you " + PAYMENT_AMOUNT + ".";

        String result = command.execute(VALID_ARGS, clientChannelMock);

        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
        verify(obligationServiceMock).payObligation(PAYER_USERNAME, LOGGED_USERNAME, PAYMENT_AMOUNT);

        String expectedNotification = LOGGED_USERNAME + " (John Doe) approved your payment of "
            + PAYMENT_AMOUNT + ".";
        verify(notificationServiceMock).addNotification(expectedNotification, PAYER_USERNAME);

        assertEquals(expectedMessage, result, "Should return confirmation message of payment.");
    }

    @Test
    void testExecute_InvalidPaymentAmount_ThrowsException() {
        String[] invalidArgs = {"approve-payment", PAYER_USERNAME, "invalidAmount"};

        assertThrows(NumberFormatException.class, () -> command.execute(invalidArgs, clientChannelMock),
            "Should throw NumberFormatException if amount is not a valid number.");
    }
}
