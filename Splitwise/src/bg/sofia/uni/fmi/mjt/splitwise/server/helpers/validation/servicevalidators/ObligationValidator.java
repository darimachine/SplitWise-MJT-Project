package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.obligation.InvalidPaymentAmountException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.obligation.ObligationNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;

import java.util.Map;

public class ObligationValidator {
    private final ObligationServiceAPI obligationService;

    public ObligationValidator(ObligationServiceAPI obligationService) {
        this.obligationService = obligationService;
    }

    public void validatePositiveAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Obligation Amount must be positive!");
        }
    }

    public void validateObligationExists(String fromUser, String toUser) {
        Map<String, Map<String, Double>> allObligations = obligationService.getAllObligations();
        if (!allObligations.containsKey(fromUser) || !allObligations.get(fromUser).containsKey(toUser)) {
            throw new ObligationNotFoundException("No obligation found between users!");
        }
    }

    public void validatePaymentDoesNotExceedObligation(String fromUser, String toUser, double amount) {
        Map<String, Double> inner = obligationService.getAllObligations().get(fromUser);
        if (inner.get(toUser) < amount) {
            throw new InvalidPaymentAmountException("Payment Amount exceeds the obligation!");
        }
    }

    public void validateAddObligation(String fromUser, String toUser, double amount) {
        if (fromUser.equals(toUser)) {
            throw new IllegalArgumentException("Cannot add obligation to the same user!");
        }
        validatePositiveAmount(amount);
    }

}
