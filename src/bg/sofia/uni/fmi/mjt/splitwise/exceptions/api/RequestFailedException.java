package bg.sofia.uni.fmi.mjt.splitwise.exceptions.api;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.SplitWiseExceptions;

public class RequestFailedException extends SplitWiseExceptions {
    public RequestFailedException(String message) {
        super(message);
    }

    public RequestFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
