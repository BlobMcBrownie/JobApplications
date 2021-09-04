package cz.chess.engine.view_controller.ingame;

import cz.chess.engine.model.board.Board;
import cz.chess.engine.view_controller.ChessBoard;
import cz.chess.engine.view_controller.GraphicsUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static cz.chess.engine.view_controller.App.*;

/**
 * Creates the Scene where the playing takes place
 * Can be accessed from MainMenu
 *
 * @author Vojtěch Sýkora
 */
public class GameView {

    public static Label whosTurnItIs, blackTimer, whiteTimer;
    private static BorderPane gameViewLayout;

    /**
     * Creates the GameView Scene adding the @param ChessBoard to play on
     *
     * @param cb
     * @return GameView Scene
     */
    public static Scene create(ChessBoard cb) {
        gameViewLayout = new BorderPane();
        gameViewLayout.setStyle("-fx-background-color: #2F3437;");


        // CENTER
        gameViewLayout.setCenter(cb);

        // RIGHT
        AnchorPane rightSide = createRightSide();
        gameViewLayout.setRight(rightSide);


        return new Scene(gameViewLayout, 1280, 720);
    }

    private static void setBoard(ChessBoard cb) {
        gameViewLayout.setCenter(cb);
    }

    private static AnchorPane createRightSide() {
        AnchorPane rightSide = new AnchorPane();

        GridPane topRightMenu = createTopRightMenu();
        rightSide.getChildren().add(topRightMenu);
        AnchorPane.setTopAnchor(topRightMenu, 5.0);

        return rightSide;
    }

    private static GridPane createTopRightMenu() {
        GridPane topRightMenu = new GridPane();
        topRightMenu.setPadding(new Insets(50,20,10,10));
        topRightMenu.setVgap(10);
        topRightMenu.setHgap(10);

        // NEW GAME
        Button newGameButton = new Button("NEW GAME");
        newGameButton.setOnAction(e -> {
            System.out.println("User chose NEW GAME");
            chessBoard = new ChessBoard(Board.createStartingBoard());
            whosTurnItIs.setText("WHITE");
            myTimer.setWhoPlays("WHITE");
            setBoard(chessBoard);
        });
        topRightMenu.add(newGameButton, 0, 0);

        // MAIN MENU
        Button mainMenuButton = new Button("MAIN MENU");
        mainMenuButton.setOnAction(e -> {
            System.out.println("User chose MAIN MENU");
            window.setScene(mainMenu);
        });
        topRightMenu.add(mainMenuButton, 1, 0);

        // SAVE GAME
        Button saveGameButton = new Button("SAVE GAME");
        saveGameButton.setOnAction(e -> {
            System.out.println("User chose SAVE GAME");
            Scene saveGame = SaveGame.create();
            window.setScene(saveGame);
        });
        topRightMenu.add(saveGameButton, 0, 1);

        // GAME RECORD
        Button gameRecordButton = new Button("GAME RECORD");
        gameRecordButton.setOnAction(e -> {
            System.out.println("User chose GAME RECORD");
            Scene gameRecord = GameRecord.create();
            window.setScene(gameRecord);
        });
        topRightMenu.add(gameRecordButton, 1, 1);

        // HINTS
        Button hintsButton = createHintsButton();
        topRightMenu.add(hintsButton, 0, 2);

        // WHOS TURN IT IS
        Label whosTurnItisLabel = new Label("Current player:");
        whosTurnItisLabel.setTextFill(Color.WHITE);
        whosTurnItisLabel.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 15));
        topRightMenu.add(whosTurnItisLabel, 0, 4);

        whosTurnItIs = new Label(chessBoard.getBoard().getCurrentPlayer().getPlayingSide().isWhite() ? "WHITE" : "BLACK");
        whosTurnItIs.setTextFill(Color.WHITE);
        whosTurnItIs.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
        topRightMenu.add(whosTurnItIs, 1, 4);

        // BLACK TIMER
        Label blackTimerLabel = createTimerLabel("BLACK");
        topRightMenu.add(blackTimerLabel, 0, 9);

        blackTimer = GraphicsUtils.createTimerLabel();
        blackTimer.setText(String.format("%02d:%02d", myTimer.blackSeconds / 60, myTimer.blackSeconds % 60));
        topRightMenu.add(blackTimer, 1, 9);

        // WHITE TIMER
        Label whiteTimerLabel = createTimerLabel("WHITE");
        topRightMenu.add(whiteTimerLabel, 0, 11);

        whiteTimer = GraphicsUtils.createTimerLabel();
        whiteTimer.setText(String.format("%02d:%02d", myTimer.whiteSeconds / 60, myTimer.whiteSeconds % 60));
        topRightMenu.add(whiteTimer, 1, 11);

        return topRightMenu;
    }

    private static Label createTimerLabel(final String color) {
        Label timerLabel = new Label(color + " TIMER:");
        timerLabel.setTextFill(Color.WHITE);
        timerLabel.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 15));
        return timerLabel;
    }

    private static Button createHintsButton() {
        Button hintsButton = new Button("HINTS OFF");
        hintsButton.setOnAction(e -> {
            hintsTurnedOn = hintsTurnedOn ? false : true;
            System.out.println(hintsTurnedOn ? "HINTS ON" : "HINTS OFF");
            if (hintsTurnedOn) {
                hintsButton.setStyle("-fx-background-color: #FFDC49;");
                hintsButton.setText("HINTS ON");
            } else {
                hintsButton.setStyle(null);
                hintsButton.setText("HINTS OFF");
            }
        });
        return hintsButton;
    }

}
