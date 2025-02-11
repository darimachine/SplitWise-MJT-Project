package bg.sofia.uni.fmi.mjt.splitwise.exceptions.groups;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.SplitWiseExceptions;

public class GroupDoesntExistsException extends SplitWiseExceptions {
    public GroupDoesntExistsException(String message) {
        super(message);
    }
}
