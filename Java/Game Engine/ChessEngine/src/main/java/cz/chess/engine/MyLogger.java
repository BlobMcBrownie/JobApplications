package cz.chess.engine;

import java.io.IOException;
import java.util.logging.*;

/**
 * Logger class to implement all my desired setting at one place
 *
 * @author Vojtěch Sýkora
 */
public class MyLogger {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Sets up the global logger so that it can be used anywhere with the same functions
     */
    public static void setupLogger() {
        LogManager.getLogManager().reset();
        LOGGER.setLevel(Level.ALL);

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.FINE);
        LOGGER.addHandler(ch);

        try {
            FileHandler fh = new FileHandler("myLogger.log", true);
            fh.setLevel(Level.WARNING);
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "File logger failed", e);
        }


    }
}

/*
 * SEVERE
 * WARNING
 * INFO
 * CONFIG
 * FINE
 * FINER
 * FINEST
 */
