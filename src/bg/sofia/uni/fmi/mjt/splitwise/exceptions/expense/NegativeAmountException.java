package bg.sofia.uni.fmi.mjt.splitwise.exceptions.expense;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.SplitWiseExceptions;

public class NegativeAmountException extends SplitWiseExceptions {
    public NegativeAmountException(String message) {
        super(message);
    }
}
