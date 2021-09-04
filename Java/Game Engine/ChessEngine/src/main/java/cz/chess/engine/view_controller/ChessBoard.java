package cz.chess.engine.view_controller;

import cz.chess.engine.model.PlayingSide;
import cz.chess.engine.model.board.*;
import cz.chess.engine.model.board.Board.Builder;
import cz.chess.engine.model.board.Move.MoveCreator;
import cz.chess.engine.model.pieces.Piece;
import cz.chess.engine.view_controller.boxes.GameEndBox;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static cz.chess.engine.view_controller.App.*;
import static cz.chess.engine.view_controller.ingame.GameView.whosTurnItIs;

/**
 * The class that represents the GUI chessboard
 * It is a GridPane 8x8 filled with TilePanels
 *
 * @author Vojtěch Sýkora
 */
public class ChessBoard extends GridPane {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static int TILE_SIZE = 80;
    private static boolean firstMove = true;
    final List<TilePanel> boardTiles;
    private Piece humanMovedPiece;
    private Tile sourceTile;
    private Tile destinationTile;
    private Board board;
    private MoveLog moveLog;
    private Builder builder;

    /**
     * Constructor for the normal playing board
     *
     * @param board
     */
    public ChessBoard(final Board board) {
        super();

        this.boardTiles = new ArrayList<>();
        this.board = board;
        this.moveLog = new MoveLog();
        myTimer.setWhiteSeconds(15*60);
        myTimer.setBlackSeconds(15*60);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(5,5,5,5));

        createBoardOfTiles();
        if (board != null) {
            this.fillBoard(board);
        }
    }

    /**
     * Constructor for the CustomGame menu board
     *
     * @param custom
     */
    public ChessBoard(boolean custom) {
        super();
        this.boardTiles = new ArrayList<>();
        this.board = null;
        this.moveLog = null;
        this.builder = new Builder();
        this.builder.setWhoPlaysNext(PlayingSide.WHITE);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(5,5,5,5));
        createBoardOfCustomTiles();
    }

    public Board getBoard() {
        return board;
    }

    public Board builderBuildAndSetBoard() {
        this.board = this.builder.build();
        return this.board;
    }

    public MoveLog getMoveLog() {
        return moveLog;
    }

    public Builder getBuilder() {
        return builder;
    }

    public void setBuilderWhoPlaysNext(PlayingSide playingSide) {
        this.builder.setWhoPlaysNext(playingSide);
    }

    public void setBuilderCastling(final boolean whiteKingSide,
                                   final boolean whiteQueenSide,
                                   final boolean blackKingSide,
                                   final boolean blackQueenSide) {
        this.builder.setCastling(builder.getKingPosition(PlayingSide.WHITE), whiteKingSide, whiteQueenSide);
        this.builder.setCastling(builder.getKingPosition(PlayingSide.BLACK), blackKingSide, blackQueenSide);
    }

    public void setBuilderEnPassantPiece(final String text) {
        this.builder.setEnPassantPawn(this.builder.findEnPassantPawn(Utils.getCoordinateFromAlphaNotation(text)));
    }

    private void createBoardOfTiles() {
        int index = 0;
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                TilePanel tilePanel = new TilePanel(row, column, index);
                this.add(tilePanel, column, 7 - row);
                this.boardTiles.add(tilePanel);
                index++;
            }
        }
    }

    private void createBoardOfCustomTiles() {
        int index = 0;
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                TilePanel tilePanel = new TilePanel(row, column, index, true);
                this.add(tilePanel, column, 7 - row);
                this.boardTiles.add(tilePanel);
                index++;
            }
        }
    }

    /**
     * Adds file+rank notation on each Tile in the GUI.
     * For testing purposes.
     */
    public void addNotationOnBoard() {
        for (TilePanel tilePanel : boardTiles) {
            tilePanel.getChildren().add(new Label(Utils.getAlphaNotationFromCoordinate(tilePanel.index)));
        }
    }

    /**
     * Adds the image representing the @param piece to the TilePanel where it should graphically be.
     *
     * @param piece
     */
    public void addPieceImageOnTileBoard(Piece piece) {
        TilePanel tilePanel = boardTiles.get(piece.getPiecePosition());
        tilePanel.addPieceImageOnTile(piece.asWord(), piece.getPlayingSide());
        boardTiles.set(tilePanel.index, tilePanel);
        GridPane.setConstraints(tilePanel, tilePanel.column, 7 - tilePanel.row);
        this.getChildren().set(tilePanel.index, tilePanel);
    }

    /**
     * Removes the image of a piece from a TilePanel at @param index
     * represents removing a piece from a Tile
     *
     * @param index
     */
    public void removeImageFromTileBoard(final int index) {
        TilePanel tilePanel = boardTiles.get(index);
        if ( tilePanel.removePieceImageFromTile() ) { // if there was an image to remove
            boardTiles.set(index, tilePanel);
            GridPane.setConstraints(tilePanel, tilePanel.column, 7 - tilePanel.row);
            this.getChildren().set(index, tilePanel);
        }
    }

    /**
     * Fills the GUI board with the same layout as is encoded in the @param board
     *
     * @param board
     */
    public void fillBoard(final Board board) {
        this.board = board;
        for (Tile tile : board.getGameTiles() ) {
            if (tile.isFull()) {
                Piece piece = tile.getPiece();
                addPieceImageOnTileBoard(piece);
            }
            else {
                removeImageFromTileBoard(tile.getTileCoordinate());
            }
        }
        this.board.recalculateLegalMoves();
    }

    private void redrawBoard(final Board board) {

        this.board = board;
        fillBoard(board);
        removeNonsenseFromBoard();
    }

    private void removeNonsenseFromBoard() {
        for (TilePanel tilePanel : this.boardTiles) {
            tilePanel.removeNonsenseFromTile();
        }
    }

    private void addImageOnTile(int index, String url) {
        TilePanel tilePanel = boardTiles.get(index);
        tilePanel.getChildren().add(new ImageView(new Image(url)));
        boardTiles.set(index, tilePanel);
        GridPane.setConstraints(tilePanel, tilePanel.column, 7 - tilePanel.row);
        this.getChildren().set(index, tilePanel);
    }



    /**
     * Class representing the GUI version of a Tile
     * It is a StackPane for combining a background color with a piece image, a border image and a target image
     */
    private class TilePanel extends StackPane {

        final int row;
        final int column;
        final int index;
        ImageView imageView;

        /**
         * Constructor for classic game with only legal moves.
         *
         * @param row
         * @param column
         * @param index
         */
        public TilePanel(int row, int column, int index) {
            super();
            this.row = row;
            this.column = column;
            this.index = index;
            this.imageView = null;

            // TILE COLOR
            initTileColor();

            this.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) { // LEFT CLICK
                    tileLeftClick();
                }
                else if (e.getButton() == MouseButton.SECONDARY) { // RIGHT CLICK
                    clearSelection();
                    removeNonsenseFromBoard();
                }
            });
        }

        private void tileLeftClick() {
            if (sourceTile == null) { // FIRST CLICK
                sourceTile = board.getTile(this.index);
                humanMovedPiece = sourceTile.getPiece();
                if (clickedOnEmptyOrOpponentsTile()) {
                    sourceTile = null;
                } else { // clicked on full tile
                    removeNonsenseFromBoard();
                    board.recalculateLegalMoves();
                    highlightLegalMoves(board);
                    highlightTile();
                }
            }
            else { // SECOND CLICK
                destinationTile = board.getTile(this.index);
                if (sourceTile == destinationTile) {
                    clearSelection();
                    removeNonsenseFromBoard();
                    return;
                }
                board.recalculateLegalMoves();

                final MoveExecution moveExecution = createAndExecuteTheMove();
                if (!moveExecution.getMoveState().equals(MoveState.ILLEGAL)) {
                    if (firstMove) {
                        firstMove = false;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                myTimer.start();
                            }
                        });
                    }
                    finishTheMoveExecution();
                } else {
                    LOGGER.info("ChessBoard: Trying to make an illegal move!");
                    removeNonsenseFromBoard();
                }
                clearSelection();
            }
        }

        private void finishTheMoveExecution() {
            Platform.runLater(new Runnable() { // user move
                @Override
                public void run() {
                    redrawBoard(board);
                    myTimer.changeWhoPlays();
                    myTimer.setGUITimer();
                    whosTurnItIs.setText(whosTurnItIs.getText() == "WHITE" ? "BLACK" : "WHITE");
                }
            });

            if (!checkIfGameEnded()) {
                if (!againstHuman) { // if playing against AI
                    Platform.runLater(new Runnable() { // make an AI move
                        @Override
                        public void run() {
                            System.out.println("COMPUTER IS MAKING A MOVE");
                            makeComputerPlay();
                            redrawBoard(board);
                            myTimer.changeWhoPlays();
                            myTimer.setGUITimer();
                            whosTurnItIs.setText(whosTurnItIs.getText() == "WHITE" ? "BLACK" : "WHITE");
                            checkIfGameEnded();
                        }
                    });
                }
            }
        }

        private boolean clickedOnEmptyOrOpponentsTile() {
            return humanMovedPiece == null || // clicked on empty tile
                   !humanMovedPiece.getPlayingSide().equals(board.getCurrentPlayer().getPlayingSide());
        }

        /**
         * Constructor for tile used in custom menu when creating a custom game.
         * Pieces can be placed anywhere and user can add as many Queens as they like.
         *
         * @param row
         * @param column
         * @param index
         * @param custom
         */
        public TilePanel(int row, int column, int index, boolean custom) { // CUSTOM GAME TILE
            super();
            this.row = row;
            this.column = column;
            this.index = index;
            this.imageView = null;

            initTileColor();

            this.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) { // LEFT CLICK
                    tileLeftClickCustom();
                }
                else if (e.getButton() == MouseButton.SECONDARY) { // RIGHT CLICK
                    builder.removePiece(this.index);
                    removePieceImageFromTile();
                }
            });
        }

        private void tileLeftClickCustom() {
            if (customSelectedPiece != null) {
                Piece newCustomPiece = Utils.returnNewCustomPiece(customSelectedPiece, this.index);
                builder.setPiece(newCustomPiece);
                addPieceImageOnTileBoard(newCustomPiece);
            } else {
                LOGGER.warning("Custom piece not selected but tried to add");
            }
        }

        private void initTileColor() {
            Canvas canvas = new Canvas(TILE_SIZE, TILE_SIZE);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            if ((row + column) % 2 == 0) {
                gc.setFill(Color.SLATEGREY);
            } else {
                gc.setFill(Color.PAPAYAWHIP);
            }
            gc.fillRect(0, 0, TILE_SIZE, TILE_SIZE);
            this.getChildren().add(canvas);
        }

        private boolean checkIfGameEnded() {
            boolean ended = false;
            if (board.getCurrentPlayer().isInCheckMate()) {
                ended = true;
                GameEndBox.display("CheckMate",
                        board.getCurrentPlayer().getOpponent().getPlayingSide().isWhite() ? "1:0" : "0:1");
            } else if (board.getCurrentPlayer().isInStaleMate()) {
                ended = true;
                GameEndBox.display("StaleMate", null);
            }
            return ended;
        }

        private MoveExecution createAndExecuteTheMove() {
            final Move move = MoveCreator.createMove(board,
                    sourceTile.getTileCoordinate(),
                    destinationTile.getTileCoordinate());
            final MoveExecution moveExecution = board.getCurrentPlayer().makeMove(move);
            if (moveExecution.getMoveState().isFinished()) {
                board = moveExecution.getAfterBoard();
                moveLog.addMove(move);
            }
            return moveExecution;
        }

        private void makeComputerPlay() {
            final Move move = board.getCurrentPlayer().chooseAMove();
            sourceTile = board.getTile(move.getMovedPiece().getPiecePosition());
            destinationTile = board.getTile(move.getFinalCoordinate());
            createAndExecuteTheMove();
        }

        private void highlightTile() {
            this.getChildren().add(new ImageView(new Image("border.png")));
        }

        private void clearSelection() {
            humanMovedPiece = null;
            sourceTile = null;
            destinationTile = null;
        }

        @Override
        public String toString() {
            return "Tile: r" + this.row + ", c" + this.column;
        }

        /**
         * Adds the image of a piece on this TilePanel.
         *
         * @param pieceAsWord
         * @param playingSide
         */
        public void addPieceImageOnTile(String pieceAsWord, PlayingSide playingSide) {
            boolean removed = false;
            String color = playingSide.name().toLowerCase();
            String piece_str = pieceAsWord + "_" + color;

            if (imageView != null && !imageView.getImage().getUrl().substring(65).equals(piece_str + ".png")) {
                removed = removePieceImageFromTile();
            }
            if (removed || imageView == null) {
                LOGGER.finest("ADDING: " + piece_str + ", i:" + this.index + ", c:" + this.column + ", r:" + this.row);
                imageView = new ImageView(new Image(piecesFolder + piece_str + ".png"));
                this.getChildren().add(imageView);
            }
        }

        /**
         * Removes an image of a piece from this TilePanel
         *
         * @return true if the image got removed, false if there was no image to remove
         */
        public boolean removePieceImageFromTile() {
            if (imageView != null) {
                LOGGER.finest("Removing " + imageView.getImage().getUrl().substring(65) + " from index " + this.index);
                this.getChildren().remove(imageView);
                imageView = null;
                return true;
            }
            return false;
        }

        private void highlightLegalMoves(final Board board) {
            if (hintsTurnedOn) {
                for (final Move move : selectedPieceLegalMoves(board)) {
                    addImageOnTile(move.getFinalCoordinate(), "red_aim.png");
                }
            }
        }

        private Collection<Move> selectedPieceLegalMoves(final Board board) { // TODO find moves in legalMoves collection
            Collection<Move> selectedMoves = new ArrayList<>();
            if (humanMovedPiece != null && humanMovedPiece.getPlayingSide() == board.getCurrentPlayer().getPlayingSide()) {
                Collection<Move> allMoves = board.getCurrentPlayer().getLegalMoves();
                for (final Move move : allMoves) {
                    if (move.getCurrentCoordinate() == humanMovedPiece.getPiecePosition()) {
                        selectedMoves.add(move);
                    }
                }
                return selectedMoves;

            }
            return Collections.emptyList();
        }

        private void removeNonsenseFromTile() {
            for (int i = 0; i < this.getChildren().size(); i++) {
                if (!(this.getChildren().get(i) instanceof Canvas) && !(this.getChildren().get(i).equals(imageView))) {
                    this.getChildren().remove(i);
                }
            }
        }
    } // -----------

}
