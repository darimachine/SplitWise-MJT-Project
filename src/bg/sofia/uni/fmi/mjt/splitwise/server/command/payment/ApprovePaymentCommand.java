package bg.sofia.uni.fmi.mjt.splitwise.server.command.payment;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;

import java.nio.channels.SocketChannel;

public class ApprovePaymentCommand implements Command {
    private final AuthenticationManager authManger;
    private final ObligationServiceAPI obligationService;
    private final NotificationServiceAPI notificationService;
    private final CommandValidator validator;
    private static final int USERNAME_INDEX = 1;
    private static final int PAYMENT_INDEX = 2;

    public ApprovePaymentCommand(AuthenticationManager authManger, ObligationServiceAPI obligationService,
                                 NotificationServiceAPI notificationService, CommandValidator validator) {
        this.authManger = authManger;
        this.notificationService = notificationService;
        this.obligationService = obligationService;
        this.validator = validator;
    }

    @Override
    public String execute(String[] arguments, SocketChannel clientChannel) {
        validator.validate(arguments, clientChannel);
        String payerUsername = arguments[USERNAME_INDEX];
        double amount = Double.parseDouble(arguments[PAYMENT_INDEX]);
        User loggedUser = authManger.getAuthenticatedUser(clientChannel);
        String loggedUsername = loggedUser.getUsername();
        obligationService.payObligation(payerUsername, loggedUsername, amount);
        String notification =
            String.format("%s (%s) approved your payment of %s.", loggedUsername, loggedUser.getFullName(), amount);
        notificationService.addNotification(notification, payerUsername);
        return String.format("%s succesfully payed you %s.", payerUsername, amount);
    }
}
