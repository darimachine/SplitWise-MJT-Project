package bg.sofia.uni.fmi.mjt.splitwise.exceptions.groups;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.SplitWiseExceptions;

public class GroupAlreadyExistException extends SplitWiseExceptions {
    public GroupAlreadyExistException(String message) {
        super(message);
    }
}
