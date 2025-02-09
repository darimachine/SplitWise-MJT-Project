package bg.sofia.uni.fmi.mjt.splitwise.exceptions.groups;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.SplitWiseExceptions;

public class UserAlreadyPartInTheGroupException extends SplitWiseExceptions {
    public UserAlreadyPartInTheGroupException(String message) {
        super(message);
    }
}
