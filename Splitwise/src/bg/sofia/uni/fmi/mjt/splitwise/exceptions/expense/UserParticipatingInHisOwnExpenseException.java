package bg.sofia.uni.fmi.mjt.splitwise.exceptions.expense;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.SplitWiseExceptions;

public class UserParticipatingInHisOwnExpenseException extends SplitWiseExceptions {
    public UserParticipatingInHisOwnExpenseException(String message) {
        super(message);
    }
}
