package cz.chess.engine.model.board;

import com.google.common.collect.Iterables;
import cz.chess.engine.model.PlayingSide;
import cz.chess.engine.model.pieces.*;
import cz.chess.engine.model.player.BlackPlayer;
import cz.chess.engine.model.player.Player;
import cz.chess.engine.model.player.WhitePlayer;

import java.util.*;
import java.util.logging.Logger;

import static cz.chess.engine.model.board.Move.MoveCreator.createMove;


/**
 * Is a representation of a chessboard with tiles and pieces and players.
 * Tiles are indexed from 0 to 63 starting on the 'a1' bottom left corner Tile.
 * For constructing is used a Builder class.
 *
 * @author Vojtěch Sýkora
 */
public class Board {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final List<Tile> gameTiles;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;
    private Player currentPlayer;
    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private Pawn enPassantPawn;


    private Board(final Builder builder) {
        this.gameTiles = createGameBoard(builder);
        this.whitePieces = calculateLivePieces(this.gameTiles, PlayingSide.WHITE);
        this.blackPieces = calculateLivePieces(this.gameTiles, PlayingSide.BLACK);

        final Collection<Move> whiteLegalMoves = calculateAllLegalMoves(this.whitePieces);
        final Collection<Move> blackLegalMoves = calculateAllLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteLegalMoves, blackLegalMoves);
        this.blackPlayer = new BlackPlayer(this, blackLegalMoves, whiteLegalMoves);
        this.currentPlayer = builder.whoPlaysNext.playerVersion(this.whitePlayer, this.blackPlayer);

        this.enPassantPawn = builder.enPassantPawn;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for(int i = (Utils.NUM_TILES_PER_ROW - 1); i >= 0; i--) {
            for (int j = 0; j < Utils.NUM_TILES_PER_ROW; j++) {
                final int index = (i * 8) + j;
                final String tileAsString = getTile(index).toString();
                stringBuilder.append(String.format("%3s", tileAsString));
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Calculates all legal chess moves of the @param Collection of pieces.
     * The legal moves are calculated based on the Board instance which calls this method.
     *
     * @param pieces Collection of object of class Piece or its superior classes
     * @return Collection of the pieces legal moves
     */
    public Collection<Move> calculateAllLegalMoves(Collection<Piece> pieces) {
        final List<Move> allLegalMoves = new ArrayList<>();
        for (Piece piece : pieces) {
            allLegalMoves.addAll(piece.getLegalMoves(this));
        }
        return allLegalMoves;
    }

    private static Collection<Piece> calculateLivePieces(final List<Tile> gameTiles, final PlayingSide playingSide) {
        List<Piece> livePieces = new ArrayList<>();
        for (final Tile tile : gameTiles) {
            if (tile.isFull()) {
                final Piece piece = tile.getPiece();
                if (piece.getPlayingSide() == playingSide) {
                    livePieces.add(piece);
                }
            }
        }
        return livePieces;
    }

    private static List<Tile> createGameBoard(final Builder builder) {
        List<Tile> initTiles = new ArrayList<>(Utils.NUM_TILES);
        for (int i = 0; i < Utils.NUM_TILES; i++) {
            initTiles.add( Tile.createTile(i, builder.gamePieces.get(i)) );
        }
        return initTiles;
    }

    /**
     * Creates an instance of Board which corresponds to the worldwide standard of a starting chess board.
     * Uses the Builder class to achieve it.
     *
     * @return Board instance of the standard chess starting board
     */
    public static Board createStartingBoard() {
        final Builder builder = new Builder();
        // WHITE starting layout (at the bottom)
        builder.setPiece(new Rook(0, PlayingSide.WHITE));
        builder.setPiece(new Knight(1, PlayingSide.WHITE));
        builder.setPiece(new Bishop(2, PlayingSide.WHITE));
        builder.setPiece(new Queen(3, PlayingSide.WHITE));
        builder.setPiece(new King(
                                4,
                                PlayingSide.WHITE,
                                true,
                                true,
                                true,
                                false));
        builder.setPiece(new Bishop(5, PlayingSide.WHITE));
        builder.setPiece(new Knight(6, PlayingSide.WHITE));
        builder.setPiece(new Rook(7, PlayingSide.WHITE));
        builder.setPiece(new Pawn(8, PlayingSide.WHITE));
        builder.setPiece(new Pawn(9, PlayingSide.WHITE));
        builder.setPiece(new Pawn(10, PlayingSide.WHITE));
        builder.setPiece(new Pawn(11, PlayingSide.WHITE));
        builder.setPiece(new Pawn(12, PlayingSide.WHITE));
        builder.setPiece(new Pawn(13, PlayingSide.WHITE));
        builder.setPiece(new Pawn(14, PlayingSide.WHITE));
        builder.setPiece(new Pawn(15, PlayingSide.WHITE));

        // BLACK starting layout (at the top)
        builder.setPiece(new Pawn(48, PlayingSide.BLACK));
        builder.setPiece(new Pawn(49, PlayingSide.BLACK));
        builder.setPiece(new Pawn(50, PlayingSide.BLACK));
        builder.setPiece(new Pawn(51, PlayingSide.BLACK));
        builder.setPiece(new Pawn(52, PlayingSide.BLACK));
        builder.setPiece(new Pawn(53, PlayingSide.BLACK));
        builder.setPiece(new Pawn(54, PlayingSide.BLACK));
        builder.setPiece(new Pawn(55, PlayingSide.BLACK));
        builder.setPiece(new Rook(56, PlayingSide.BLACK));
        builder.setPiece(new Knight(57, PlayingSide.BLACK));
        builder.setPiece(new Bishop(58, PlayingSide.BLACK));
        builder.setPiece(new Queen(59, PlayingSide.BLACK));
        builder.setPiece(new King(
                                60,
                                PlayingSide.BLACK,
                                true,
                                true,
                                true,
                                false));
        builder.setPiece(new Bishop(61, PlayingSide.BLACK));
        builder.setPiece(new Knight(62, PlayingSide.BLACK));
        builder.setPiece(new Rook(63, PlayingSide.BLACK));

        builder.setWhoPlaysNext(PlayingSide.WHITE);
        return builder.build();
    }

    /**
     * Returns a Tile object with the corresponding coordinate.
     * 
     * @param coordinate of the Tile wanted
     * @return Tile object from the gameTiles List
     */
    public Tile getTile(final int coordinate) {
        return this.gameTiles.get(coordinate);
    }

    public Pawn getEnPassantPawn() {
        return enPassantPawn;
    }

    public void setEnPassantPawn(Pawn enPassantPawn) {
        this.enPassantPawn = enPassantPawn;
    }

    public List<Tile> getGameTiles() {
        return gameTiles;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Collection<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    public Collection<Piece> getWhitePieces() {
        return this.whitePieces;
    }

    public Player getWhitePlayer() {
        return this.whitePlayer;
    }

    public Player getBlackPlayer() {
        return this.blackPlayer;
    }

    public void setCurrentPlayer(String color) {
        this.currentPlayer = color.equals("WHITE") ? this.whitePlayer : this.blackPlayer;
    }

    /**
     * Returns a Piece object from the Tile with the corresponding coordinate.
     *
     * @param coordinate
     * @return Piece object
     */
    public Piece getPiece(final int coordinate) {
        final Tile tile = this.gameTiles.get(coordinate);
        return tile.getPiece();
    }

    /**
     * Returns the concatenated Iterable of all current legal moves of both players.
     *
     * @return Iterable containing all current legal moves
     */
    public Iterable<Move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(
                this.whitePlayer.getLegalMoves(),
                this.blackPlayer.getLegalMoves()
        ));
    }

    /**
     * Used to recalculate legal moves when the board layout  is changed
     */
    public void recalculateLegalMoves() {
        final Collection<Move> whiteLegalMoves = calculateAllLegalMoves(this.whitePieces);
        final Collection<Move> blackLegalMoves = calculateAllLegalMoves(this.blackPieces);

        this.whitePlayer.setAllLegalMoves(whiteLegalMoves, blackLegalMoves);
        this.blackPlayer.setAllLegalMoves(blackLegalMoves, whiteLegalMoves);
    }

    private Collection<Move> filterLegalMoves(Collection<Move> legalMoves, final Board board) {
        final List<Move> toRemove = new ArrayList<>();
        for (final Move move : legalMoves) {
            final MoveExecution me = board.getCurrentPlayer().makeMove(createMove(board,
                    move.getCurrentCoordinate(),
                    move.getFinalCoordinate()));
            if(me.getAfterBoard().getCurrentPlayer().isInCheck() ||
                    me.getAfterBoard().getCurrentPlayer().isInCheckMate() ||
                    me.getAfterBoard().getCurrentPlayer().isInStaleMate()
            ) {
                toRemove.add(move);
            }
        }
        legalMoves.removeAll(toRemove);
        return legalMoves;
    }

    /**
     * Used to build a chess board representation as an instance of the Board class
     */
    //    --- SUBCLASS ---
    public static class Builder {

        private final Map<Integer, Piece> gamePieces;
        private PlayingSide whoPlaysNext;
        private Pawn enPassantPawn;

        public Builder() {
            this.gamePieces = new HashMap<>();
        }

        /**
         * Places the @param piece on the board where the piece belongs.
         * The index at which the piece is to be placed is stored in the Piece class.
         *
         * @param piece
         * @return Builder with the new piece added
         */
        public Builder setPiece(final Piece piece) {
            this.gamePieces.put(piece.getPiecePosition(), piece);
            return this;
        }

        public Builder removePiece(final int position) {
            this.gamePieces.remove(position);
            return this;
        }

        public Builder setWhoPlaysNext(PlayingSide whoPlaysNext) {
            this.whoPlaysNext = whoPlaysNext;
            return this;
        }

        /**
         * Sets the castling booleans to the king on @param position
         *
         * @param position
         * @param kingSide
         * @param queenSide
         */
        public void setCastling(final int position, final boolean kingSide, final boolean queenSide) {
            King kingPiece = (King) this.gamePieces.remove(position);
            kingPiece.setCastling(kingSide, queenSide);
            setPiece(kingPiece);
        }

        /**
         * When the Builder is ready to be built into a Board, this method takes care of it.
         *
         * @return Board
         */
        public Board build() {
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn movedPawn) {
            this.enPassantPawn = movedPawn;
        }

        /**
         * Finds the kings position.
         * The king to be found is of @param playingSide
         * 
         * @param playingSide 
         * @return
         */
        public int getKingPosition(PlayingSide playingSide) {
            for(Map.Entry<Integer, Piece> entry : this.gamePieces.entrySet()) {
                final Piece piece = entry.getValue();
                if (piece.isKing() && piece.getPlayingSide().equals(playingSide)) {
                    return piece.getPiecePosition();
                }
            }
            LOGGER.warning(playingSide.toString() + " KingPosition not found");
            return -1;
        }

        /**
         * Finds on which Tile lays the enPassantPawn
         * 
         * @param index 
         * @return the enPassantPawn Piece
         */
        public Pawn findEnPassantPawn(final int index) {
            return (Pawn) this.gamePieces.get(index);
        }
    }

}
