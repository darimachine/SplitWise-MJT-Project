package bg.sofia.uni.fmi.mjt.splitwise.util;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerUtil {
    private static final Logger LOGGER = Logger.getLogger(LoggerUtil.class.getName());

    static {
        try {
            // Create a FileHandler to log messages to a file
            FileHandler fileHandler = new FileHandler("errors/server-errors.log", true);
            fileHandler.setFormatter(new SimpleFormatter()); // Set log format

            // Add handlers to logger (console + file)
            LOGGER.addHandler(fileHandler);
            LOGGER.addHandler(new ConsoleHandler());

            LOGGER.setLevel(Level.SEVERE); // Log only severe errors
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    public static void logError(String message, Exception e, String user) {
        String userInfo = (user != null) ? "User: " + user + " | " : "";
        LOGGER.severe(userInfo + message + " | " + e.toString());

        // Print stack trace to log file
        for (StackTraceElement element : e.getStackTrace()) {
            LOGGER.severe("\tat " + element);
        }
    }
}