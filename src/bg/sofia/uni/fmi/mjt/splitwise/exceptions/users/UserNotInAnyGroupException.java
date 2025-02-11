package bg.sofia.uni.fmi.mjt.splitwise.exceptions.users;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.SplitWiseExceptions;

public class UserNotInAnyGroupException extends SplitWiseExceptions {
    public UserNotInAnyGroupException(String message) {
        super(message);
    }
}
