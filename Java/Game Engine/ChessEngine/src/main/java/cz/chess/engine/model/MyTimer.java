package cz.chess.engine.model;

import cz.chess.engine.view_controller.boxes.GameEndBox;
import javafx.application.Platform;

import java.util.logging.Level;
import java.util.logging.Logger;

import static cz.chess.engine.view_controller.ingame.GameView.blackTimer;
import static cz.chess.engine.view_controller.ingame.GameView.whiteTimer;

/**
 * Chess Timer working on a separate thread
 *
 * @author Vojtěch Sýkora
 */
public class MyTimer extends Thread {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public int whiteSeconds = 0;
    public int blackSeconds = 0;
    private boolean whitePlays = true;
    private boolean stopTime = false;

    public MyTimer() {
        whiteSeconds = 15*60; // classic game 15 minutes for each player
        blackSeconds = 15*60;
    }

    @Override
    public void run() {
        playTheTime();
    }

    /**
     * Repeatedly subtracts the time of the current player
     * and ends when one of the players reaches 0 seconds
     */
    private void playTheTime() {
        while (!stopTime && whiteSeconds > 0 && blackSeconds > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Thread.sleep() failed");
                this.interrupt();
                break;
            }

            if (whitePlays) {
                whiteSeconds--;
            } else {
                blackSeconds--;
            }
            setGUITimer();
        }
        checkForEnding();
    }

    private void checkForEnding() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (whiteSeconds <= 0) {
                    GameEndBox.display("WHITE'S TIME RAN OUT", "0:1");
                } else if (blackSeconds <= 0) {
                    GameEndBox.display("BLACK'S TIME RAN OUT", "1:0");
                }
            }
        });
    }

    /**
     * Rewrites the text on the GUI Labels showing the time each player has
     */
    public void setGUITimer() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                whiteTimer.setText(String.format("%02d:%02d", whiteSeconds / 60, whiteSeconds % 60));
                blackTimer.setText(String.format("%02d:%02d", blackSeconds / 60, blackSeconds % 60));
            }
        });
    }

    public void setWhoPlays(final String color) {
        whitePlays = color.equals("WHITE") ? true : false;
    }

    public void changeWhoPlays() {
        whitePlays = whitePlays ? false : true;
    }

    public void setWhiteSeconds(int whiteSeconds) {
        this.whiteSeconds = whiteSeconds;
    }

    public void setBlackSeconds(int blackSeconds) {
        this.blackSeconds = blackSeconds;
    }
}
