package cz.chess.engine.view_controller.menu;

import cz.chess.engine.model.board.FEN;
import cz.chess.engine.model.board.PGN;
import cz.chess.engine.view_controller.ChessBoard;
import cz.chess.engine.view_controller.GraphicsUtils;
import cz.chess.engine.view_controller.boxes.AlertBox;
import cz.chess.engine.view_controller.ingame.GameView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static cz.chess.engine.view_controller.App.*;

/**
 * Scene for loading a game using FEN or PGN
 * Can be accessed from MainMenu
 *
 * @author Vojtěch Sýkora
 */
public class LoadGame {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Creates the LoadGame Scene which has links to FEN and PGN load Scenes
     *
     * @return LoadGame Scene
     */
    public static Scene create() {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #2F3437;");

        // BACK BUTTON
        HBox top = GraphicsUtils.createBackButtonOnRight();
        layout.setTop(top);

        // CENTER
        GridPane center = createCenterGrid();
        Button usingFEN = createButton("using FEN", loadFENScene);
        Button usingPGN = createButton("using PGN", loadPGNScene);
        center.add(usingFEN, 0, 0);
        center.add(usingPGN, 1, 0);
        layout.setCenter(center);

        return new Scene(layout, 1280, 720);
    }

    private static GridPane createCenterGrid() {
        GridPane center = new GridPane();
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(10,10,10,10));
        center.setVgap(10);
        center.setHgap(10);
        return center;
    }

    private static Button createButton(String text, Scene scene) {
        Button button = new Button(text);
        button.setOnAction(e -> {
            System.out.println("User chose " + text);
            window.setScene(scene);
        });
        button.setPrefSize(200, 100);
        button.setFont(Font.font("Times New Roman", FontWeight.BOLD, 30));
        return button;
    }

    /**
     * Creates the Scene where user can load a game from FEN
     *
     * @return load FEN Scene
     */
    public static Scene createFENScene() {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #2F3437;");

        VBox center = new VBox();
        center.setAlignment(Pos.CENTER);
            Label label = new Label("Load game from FEN");
            label.setTextFill(Color.WHITE);
            TextArea textArea = new TextArea();
            textArea.setPromptText("Paste FEN string here");
            Button saveButton = new Button("Click here to create game from inputted FEN");
            saveButton.setOnAction(e -> {
                final String fenString = textArea.getText();
                chessBoard = new ChessBoard(FEN.createBoardFromFEN(fenString));
                gameView = GameView.create(chessBoard);
                window.setScene(gameView);
            });
        center.getChildren().addAll(label, textArea, saveButton);
        layout.setCenter(center);

        HBox top = GraphicsUtils.createBackButtonOnRight();
        layout.setTop(top);

        return new Scene(layout, 1280, 720);
    }

    /**
     * Creates the Scene where user can load a game from PGN
     *
     * @return load PGN Scene
     */
    public static Scene createPGNScene() {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #2F3437;");


        final FileChooser fileChooser = new FileChooser();
        final Button fileOpenButton = createFileOpenButton();
        fileOpenButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(window);
            if (file != null) {
                if (isTxtFile(file)) {
                    System.out.println("PGNFilename: " + file.getName());
                    workWithFile(file);
                } else {
                    AlertBox.display("wrong filetype", "You need to choose a '.txt' file!");
                }
            }
        });
        fileChooser.setTitle("Open Resource File");
        layout.setCenter(fileOpenButton);

        HBox top = GraphicsUtils.createBackButtonOnRight();
        layout.setTop(top);

        return new Scene(layout, 1280, 720);
    }

    private static void workWithFile(File file) {
        String line;

        try (final BufferedReader br = new BufferedReader(new FileReader(file))) {
            while((line = br.readLine()) != null) { // skip to the moves
                if (line.isEmpty()) {
                    break;
                }
            }

            if ((line = br.readLine()) != null) { // moves line
                chessBoard = new ChessBoard(PGN.createGameFromPGN(line));
                gameView = GameView.create(chessBoard);
                window.setScene(gameView);
            }
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "PGN file not found", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Problem with file IO", e);
        }
    }

    private static Button createFileOpenButton() {
        Button button = new Button("Open a PGN file");
//        button.setPrefSize(250, 100);
        button.setFont(Font.font("Times New Roman", FontWeight.BOLD, 40));
        return button;
    }

    private static boolean isTxtFile(File file) {
        return file.getName().substring(file.getName().length() - 4).equals(".txt");
    }


}
