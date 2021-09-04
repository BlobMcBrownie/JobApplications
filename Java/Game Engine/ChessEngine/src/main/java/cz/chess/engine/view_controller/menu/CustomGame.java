package cz.chess.engine.view_controller.menu;

import cz.chess.engine.model.PlayingSide;
import cz.chess.engine.model.board.Board;
import cz.chess.engine.view_controller.ChessBoard;
import cz.chess.engine.view_controller.GraphicsUtils;
import cz.chess.engine.view_controller.boxes.AlertBox;
import cz.chess.engine.view_controller.ingame.GameView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static cz.chess.engine.view_controller.App.*;

/**
 * Scene for creating a custom game.
 * User can set a custom board layout and other settings like who plays next
 * Can be accessed from MainMenu
 *
 * @author Vojtěch Sýkora
 */
public class CustomGame {

    private static ChoiceBox<String> whoPlaysNext, whoIsTheOpponent;
    private static CheckBox whiteKing, whiteQueen, blackKing, blackQueen, standardBoard;
    private static TextField enPassantTargetSquare, halfmoveClock, fullmoveNumber;

    /**
     * Creates the Scene
     *
     * @return CustomGame Scene
     */
    public static Scene create() {
        BorderPane layout = new BorderPane();
//        layout.setStyle("-fx-background-color: #2F3437;");

        // TOP ---------------------------------
        BorderPane topMenu = createTopMenu();
        layout.setTop(topMenu);

        // LEFT ---------------------------------
        GridPane leftMenu = createLeftMenu();
        layout.setLeft(leftMenu);

        // RIGHT
        CustomPiecesPanel customPiecesPanel = new CustomPiecesPanel();
        layout.setRight(customPiecesPanel);

        // CENTER -------------------------------------
        // cz.chess.engine.view_controller.ChessBoard;
        layout.setCenter(customChessBoard);

        // OVERALL
//        topMenu.setStyle("-fx-border-color: black");
//        leftMenu.setStyle("-fx-border-color: black");
//        customPiecesPanel.setStyle("-fx-border-color: black");

        return new Scene(layout, 1280, 720);
    }

    private static BorderPane createTopMenu() {
        BorderPane topMenu = new BorderPane();

        Label heading = new Label("CUSTOM GAME menu");
        heading.setTextFill(Color.BLACK);
        heading.setFont(Font.font("Times New Roman", FontWeight.BOLD, 40));

        Button backButton = GraphicsUtils.createBackButton();

        topMenu.setCenter(heading);
        topMenu.setRight(backButton);

        return topMenu;
    }

    private static GridPane createLeftMenu() {
        GridPane leftMenu = createLeftMenuLayout();

        // WHO PLAYS NEXT
        Label whoPlaysNextLabel = new Label("Who plays next:");
        GridPane.setConstraints(whoPlaysNextLabel, 0,0);

        whoPlaysNext = createWhoPlaysNext();

        // CASTLING AVAILABILITY
        Label castlingAvailabilityLabel = new Label("Castling availability");
        GridPane.setConstraints(castlingAvailabilityLabel, 0,1);
        GridPane castlingCheckBoxes = createCastlingCheckboxes();

        // EN PASSANT TARGET SQUARE
        Label enPassantLabel = new Label("En passant target square");
        GridPane.setConstraints(enPassantLabel, 0,2);
        enPassantTargetSquare = new TextField("-");
        GridPane.setConstraints(enPassantTargetSquare, 1,2);

        // HALFMOVE CLOCK
        Label halfmoveClockLabel = new Label("Halfmove clock");
        GridPane.setConstraints(halfmoveClockLabel, 0,3);
        halfmoveClock = new TextField("0");
        GridPane.setConstraints(halfmoveClock, 1,3);

        // FULLMOVE NUMBER
        Label fullmoveLabel = new Label("Fullmove number");
        GridPane.setConstraints(fullmoveLabel, 0,4);
        fullmoveNumber = new TextField("1");
        GridPane.setConstraints(fullmoveNumber, 1,4);

        //PLAYER VS ???
        Label whoIsTheOpponentLabel = new Label("Opponent:");
        GridPane.setConstraints(whoIsTheOpponentLabel, 0,5);

        whoIsTheOpponent = createWhoIsTheOpponent();

        // Standard board
        standardBoard = new CheckBox("Standard Board");
        GridPane.setConstraints(standardBoard, 1, 6);

        // SAVE CHANGES BUTTON
        Button saveButton = createSaveButton();
            gameView = GameView.create(chessBoard);
            window.setScene(gameView);

        leftMenu.getChildren().addAll(
                whoPlaysNextLabel,
                whoPlaysNext,
                castlingAvailabilityLabel,
                castlingCheckBoxes,
                enPassantLabel,
                enPassantTargetSquare,
                halfmoveClockLabel,
                halfmoveClock,
                fullmoveLabel,
                fullmoveNumber,
                whoIsTheOpponentLabel,
                whoIsTheOpponent,
                standardBoard,
                saveButton);

        return leftMenu;
    }

    private static GridPane createLeftMenuLayout() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10,10,10,10));
        gridPane.setVgap(10);
        gridPane.setHgap(5);
        return gridPane;
    }

    private static ChoiceBox<String> createWhoPlaysNext() {
        ChoiceBox choiceBox = new ChoiceBox<>();
        GridPane.setConstraints(choiceBox, 1,0);
        choiceBox.getItems().addAll("WHITE", "BLACK");
        choiceBox.setValue("WHITE");
        choiceBox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
            System.out.println(newValue + " plays next!");
            customChessBoard.setBuilderWhoPlaysNext(newValue == "WHITE" ? PlayingSide.WHITE : PlayingSide.BLACK);
            myTimer.setWhoPlays(newValue == "WHITE" ? "WHITE" : "BLACK");
        });
        return choiceBox;
    }

    private static ChoiceBox<String> createWhoIsTheOpponent() {
        ChoiceBox choiceBox = new ChoiceBox<>();
        GridPane.setConstraints(choiceBox, 1,5);
        choiceBox.getItems().addAll("HUMAN", "AI");
        choiceBox.setValue("HUMAN");
        choiceBox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
            System.out.println(newValue + " is the opponent");
            againstHuman = newValue.equals("AI") ? false : true;
        });
        return choiceBox;
    }

    private static Button createSaveButton() {
        Button button = new Button("PLAY");
        GridPane.setConstraints(button, 1, 8);
        button.setOnAction(e -> {
            System.out.println("Saving changes... and PLAYING");
            if (standardBoard.isSelected()) {
                chessBoard = new ChessBoard(Board.createStartingBoard());
                gameView = GameView.create(chessBoard);
                window.setScene(gameView);
            } else if (validBoard()) {
                saveAll();
                chessBoard = new ChessBoard(customChessBoard.builderBuildAndSetBoard());
                gameView = GameView.create(chessBoard);
                window.setScene(gameView);
            } else {
                AlertBox.display("Illegal Game Layout",
                        "You didn't add either a White King or a Black King or Both. " +
                                "Close this window and add them and then press PLAY.");
            }

        });
        return button;
    }

    private static boolean validBoard() {
        return customChessBoard.getBuilder().getKingPosition(PlayingSide.WHITE) != -1 &&
               customChessBoard.getBuilder().getKingPosition(PlayingSide.BLACK) != -1;
    }

    private static GridPane createCastlingCheckboxes() {
        GridPane castlingCheckBoxes = new GridPane();
        castlingCheckBoxes.setAlignment(Pos.CENTER);
        castlingCheckBoxes.setVgap(5);
        castlingCheckBoxes.setHgap(5);
        GridPane.setConstraints(castlingCheckBoxes, 1, 1);

        whiteKing = new CheckBox("whiteKing");
        GridPane.setConstraints(whiteKing, 0,0);
        whiteKing.setSelected(true);

        whiteQueen = new CheckBox("whiteQueen");
        GridPane.setConstraints(whiteQueen, 1,0);
        whiteQueen.setSelected(true);

        blackKing = new CheckBox("blackKing");
        GridPane.setConstraints(blackKing, 0,1);
        blackKing.setSelected(true);

        blackQueen = new CheckBox("blackQueen");
        GridPane.setConstraints(blackQueen, 1,1);
        blackQueen.setSelected(true);

        castlingCheckBoxes.getChildren().addAll(whiteKing, whiteQueen, blackKing, blackQueen);

        return castlingCheckBoxes;
    }

    private static void saveAll() {

        String castlingAvailability = getCastlingAvailability();
        customChessBoard.setBuilderCastling(
                castlingAvailability.contains("K"),
                castlingAvailability.contains("Q"),
                castlingAvailability.contains("k"),
                castlingAvailability.contains("q")
        );

        if (!enPassantTargetSquare.equals("-")) {
            customChessBoard.setBuilderEnPassantPiece(enPassantTargetSquare.getText());
        }
    }

    private static String getCastlingAvailability() {
        String castlingAvailability = "";
        if (whiteKing.isSelected()) {
            castlingAvailability += "K";
        }
        if (whiteQueen.isSelected()) {
            castlingAvailability += "Q";
        }
        if (blackKing.isSelected()) {
            castlingAvailability += "k";
        }
        if (blackQueen.isSelected()) {
            castlingAvailability += "q";
        }
        if (castlingAvailability.equals("")) {
            castlingAvailability = "-";
        }
        return castlingAvailability;
    }


}
