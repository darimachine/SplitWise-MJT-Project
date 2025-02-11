package bg.sofia.uni.fmi.mjt.splitwise.exceptions.users;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.SplitWiseExceptions;

public class UserAlreadyAuthenticatedException extends SplitWiseExceptions {
    public UserAlreadyAuthenticatedException(String message) {
        super(message);
    }
}
