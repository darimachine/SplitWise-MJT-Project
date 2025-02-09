package bg.sofia.uni.fmi.mjt.splitwise.exceptions.expense;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.SplitWiseExceptions;

public class ExpenseWithThatUserNotFoundException extends SplitWiseExceptions {
    public ExpenseWithThatUserNotFoundException(String message) {
        super(message);
    }
}
