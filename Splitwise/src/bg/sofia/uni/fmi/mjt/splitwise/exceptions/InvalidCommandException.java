package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class InvalidCommandException extends SplitWiseExceptions {
    public InvalidCommandException(String message) {
        super(message);
    }

    public InvalidCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
