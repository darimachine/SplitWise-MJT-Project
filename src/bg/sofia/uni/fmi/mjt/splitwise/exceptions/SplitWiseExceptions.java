package bg.sofia.uni.fmi.mjt.splitwise.exceptions;

public class SplitWiseExceptions extends RuntimeException {
    public SplitWiseExceptions(String message) {
        super(message);
    }

    public SplitWiseExceptions(String message, Throwable cause) {
        super(message, cause);
    }
}
