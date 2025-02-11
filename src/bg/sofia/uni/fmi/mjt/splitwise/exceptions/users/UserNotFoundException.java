package bg.sofia.uni.fmi.mjt.splitwise.exceptions.users;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.SplitWiseExceptions;

public class UserNotFoundException extends SplitWiseExceptions {
    public UserNotFoundException(String message) {
        super(message);
    }
}
