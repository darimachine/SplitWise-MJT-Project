package bg.sofia.uni.fmi.mjt.splitwise.exceptions.obligation;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.SplitWiseExceptions;

public class InvalidPaymentAmountException extends SplitWiseExceptions {
    public InvalidPaymentAmountException(String message) {
        super(message);
    }
}
