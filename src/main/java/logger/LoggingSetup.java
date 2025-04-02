package logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Configures the global logging level for the entire application.
 * This ensures that all loggers adhere to a consistent logging level.
 */
public class LoggingSetup {

    /**
     * Sets up global logging by resetting the log manager, configuring a console handler,
     * and setting the logging level.
     */
    public static void configureGlobalLogging() {
        LogManager.getLogManager().reset();
        Logger rootLogger = Logger.getLogger("");

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.WARNING);
        rootLogger.addHandler(handler);
        rootLogger.setLevel(Level.WARNING);
    }
}
