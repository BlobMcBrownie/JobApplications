package cz.chess.engine.view_controller;

import cz.chess.engine.MyLogger;
import cz.chess.engine.model.MyTimer;
import cz.chess.engine.model.board.Board;
import cz.chess.engine.view_controller.boxes.ConfirmBox;
import cz.chess.engine.view_controller.ingame.GameView;
import cz.chess.engine.view_controller.menu.UserManual;
import cz.chess.engine.view_controller.menu.CustomGame;
import cz.chess.engine.view_controller.menu.LoadGame;
import cz.chess.engine.view_controller.menu.MainMenu;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * JavaFX App - Chess Engine GUI
 * Main Class of this project
 * Run this class to start the program
 *
 * @author Vojtěch Sýkora
 */
public class App extends Application {

    public static Stage window;
    public static Scene mainMenu, gameView, customScene, loadScene, userManualScene, loadFENScene, loadPGNScene;
    public static ChessBoard chessBoard, customChessBoard;
    public static boolean hintsTurnedOn = false;
    public static String piecesFolder = "";
    private static Board board;
    public static boolean againstHuman = true;
    public static String customSelectedPiece = null;
    public static MyTimer myTimer;

    public static void main(String[] args) {
        launch();
    }

    /**
     * Sets up the Logger
     * Initializes all Scenes
     * Starts up the GUI
     *
     * @param stage
     */
    @Override
    public void start(Stage stage) {
        window = stage;
        MyLogger.setupLogger();
        myTimer = new MyTimer();

        initialiseAllScenes();

        window.setTitle("King's Gambit");
        window.setOnCloseRequest(e -> {
            e.consume();
            closeProgram();
        });
        window.setScene(mainMenu);
        window.show();
    }

    private void initialiseAllScenes() {
        board = Board.createStartingBoard();
        System.out.println(board);

        chessBoard = new ChessBoard(board);
        customChessBoard = new ChessBoard(true);

        mainMenu = MainMenu.create();
        customScene = CustomGame.create();
        userManualScene = UserManual.create();
        loadFENScene = LoadGame.createFENScene();
        loadPGNScene = LoadGame.createPGNScene();
        gameView = GameView.create(chessBoard);
        loadScene = LoadGame.create();
    }

    private void closeProgram() {
        Boolean answer = ConfirmBox.display("You wanna exit?", "You sure you want to exit?");
        if (answer) {
            window.close();
            myTimer.interrupt();
        }
    }

}