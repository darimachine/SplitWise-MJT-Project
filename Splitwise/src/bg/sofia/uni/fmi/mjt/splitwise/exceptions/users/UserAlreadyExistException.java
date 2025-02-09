package bg.sofia.uni.fmi.mjt.splitwise.exceptions.users;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.SplitWiseExceptions;

public class UserAlreadyExistException extends SplitWiseExceptions {
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
