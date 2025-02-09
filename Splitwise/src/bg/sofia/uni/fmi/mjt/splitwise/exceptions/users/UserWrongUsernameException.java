package bg.sofia.uni.fmi.mjt.splitwise.exceptions.users;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.SplitWiseExceptions;

public class UserWrongUsernameException extends SplitWiseExceptions {
    public UserWrongUsernameException(String message) {
        super(message);
    }
}
