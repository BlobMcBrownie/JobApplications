package cz.chess.engine;

/**
 * Information about the versions of java used
 *
 * @author Vojtěch Sýkora
 */
public class SystemInfo {

    public static String javaVersion() {
        return System.getProperty("java.version");
    }

    public static String javafxVersion() {
        return System.getProperty("javafx.version");
    }

}