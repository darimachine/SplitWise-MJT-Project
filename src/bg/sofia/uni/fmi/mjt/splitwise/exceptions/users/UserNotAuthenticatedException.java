package bg.sofia.uni.fmi.mjt.splitwise.exceptions.users;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.SplitWiseExceptions;

public class UserNotAuthenticatedException extends SplitWiseExceptions {
    public UserNotAuthenticatedException(String message) {
        super(message);
    }
}
