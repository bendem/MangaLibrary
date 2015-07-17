package be.bendem.manga.library.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Log {

    private static class CustomLogger extends Logger {
        protected CustomLogger() {
            super("MangaLibrary", null);

            setLevel(Level.ALL);

            ConsoleHandler handler = new ConsoleHandler();
            handler.setFormatter(new Formatter() {

                private final DateFormat format = new SimpleDateFormat("HH.mm.ss");

                @Override
                public String format(LogRecord record) {
                    return String.format(
                        "%s: %s\n",
                        format.format(new Date()),
                        record.getMessage()
                    );
                }
            });
            handler.setLevel(Level.ALL);

            addHandler(handler);
        }
    }

    private static final Logger LOGGER = new CustomLogger();

    public static void debug(String message) {
        LOGGER.log(Level.FINE, message);
    }

    public static void debug(String message, Throwable throwable) {
        LOGGER.log(Level.FINE, message, throwable);
    }

    public static void info(String message) {
        LOGGER.log(Level.INFO, message);
    }

    public static void info(String message, Throwable throwable) {
        LOGGER.log(Level.INFO, message, throwable);
    }

    public static void warn(String message) {
        LOGGER.log(Level.WARNING, message);
    }

    public static void warn(String message, Throwable throwable) {
        LOGGER.log(Level.WARNING, message, throwable);
    }

    public static void err(String message) {
        LOGGER.log(Level.SEVERE, message);
    }

    public static void err(String message, Throwable throwable) {
        LOGGER.log(Level.SEVERE, message, throwable);
    }

}
