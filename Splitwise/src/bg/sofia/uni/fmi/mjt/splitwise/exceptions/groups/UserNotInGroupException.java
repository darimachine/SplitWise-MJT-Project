package bg.sofia.uni.fmi.mjt.splitwise.exceptions.groups;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.SplitWiseExceptions;

public class UserNotInGroupException extends SplitWiseExceptions {
    public UserNotInGroupException(String message) {
        super(message);
    }
}
