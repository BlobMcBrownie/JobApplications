package cz.chess.engine.model.board;

import cz.chess.engine.model.board.Board.Builder;
import cz.chess.engine.model.pieces.Pawn;
import cz.chess.engine.model.pieces.Piece;
import cz.chess.engine.model.pieces.Rook;
import cz.chess.engine.view_controller.boxes.PawnPromotionChoiceBox;

import java.util.Objects;

import static cz.chess.engine.model.board.Utils.getAlphaNotationFromCoordinate;

/**
 * A representation of a move on a chess board.
 * This class is a base class for multiple special move classes.
 *
 * @author Vojtěch Sýkora
 */
public abstract class Move {

    protected final Board board;
    protected final Piece movedPiece;
    protected final int finalCoordinate;
    protected final boolean isFirstMove; // for pawns and castling

    private Move(final Board board, final Piece movedPiece, final int finalCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.finalCoordinate = finalCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    private Move(final Board board, final int finalCoordinate) { // for InvalidMove
        this.board = board;
        this.finalCoordinate = finalCoordinate;
        this.movedPiece = null;
        this.isFirstMove = false;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return Objects.equals(board, move.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board);
    }

    public int getFinalCoordinate() {
        return finalCoordinate;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public int getCurrentCoordinate() {
        return this.movedPiece.getPiecePosition();
    }

    public Board getBoard() {
        return this.board;
    }

    public boolean isOffense() {
        return false;
    }

    public boolean isCastlingMove() {
        return false;
    }

    public Piece getAttackedPiece() {
        return null;
    }

    /**
     * Executes the move which calls this method.
     * A new Board is built with all the pieces in their place except for the ones affected by the move.
     * The ones affected are moved accordingly.
     *
     * @return Board of how it should look after the Move is executed
     */
    public Board execute() {
        final Builder builder = new Builder();

        for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getLivePieces()) {
            builder.setPiece(piece);
        }

        for (final Piece piece : this.board.getCurrentPlayer().getLivePieces()) {
            if (!this.movedPiece.equals(piece)) {
                builder.setPiece(piece);
            }
        }

        builder.setPiece(this.movedPiece.moveThisPiece(this));
        builder.setWhoPlaysNext(this.board.getCurrentPlayer().getOpponent().getPlayingSide());

        return builder.build();
    }

    /**
     * Used for PGN Notation
     * where it is needed to distinguish between which one of two same pieces is moved.
     *
     * @return String of the file letter the moved piece was originally in
     */
    public String ifDoubles() {
        for(final Move move : this.board.getCurrentPlayer().getLegalMoves()) {
            if(move.getFinalCoordinate() == this.finalCoordinate && !this.equals(move) &&
                    this.movedPiece.getPieceType().equals(move.getMovedPiece().getPieceType())) {
                return getAlphaNotationFromCoordinate(this.movedPiece.getPiecePosition()).substring(0, 1);
            }
        }
        return "";
    }

    /**
     * A normal non-attacking move for moving a piece from its Tile to another empty Tile.
     */
    // -------- SUBCLASSES --------
    public static final class NormalMove extends Move {

        public NormalMove(final Board board, final Piece movedPiece, final int finalCoordinate) {
            super(board, movedPiece, finalCoordinate);
        }

        @Override
        public boolean equals(final Object o) {
            return super.equals(o);
        }

        /**
         * @return String corresponding to the PGN notation format
         */
        @Override
        public String toString() {
            return movedPiece.getPieceType().toString() +
                    ifDoubles() +
                    getAlphaNotationFromCoordinate(this.finalCoordinate);
        }
    }


    /**
     * A base attacking move.
     */
    public static class OffensiveMove extends Move {

        final Piece attackedPiece;

        public OffensiveMove(final Board board,
                             final Piece movedPiece,
                             final int finalCoordinate,
                             final Piece attackedPiece) {
            super(board, movedPiece, finalCoordinate);
            this.attackedPiece = attackedPiece;
        }

        /**
         * The attacking piece moves as if it was a normal move, however the attacked piece is removed from the game.
         *
         * @return Board of how it should look after the Move is executed
         */
        @Override
        public Board execute() {
            final Builder builder = new Builder();

            for (final Piece piece : this.board.getCurrentPlayer().getLivePieces()) {
                if (!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getLivePieces()) {
                if (!this.attackedPiece.equals(piece)) {
                    builder.setPiece(piece);
                    // this.board.getPiece(this.finalCoordinate) / this.attackedPiece
                    // TODO this is the piece which will get out and we want to mark it down somewhere
                }
            }

            builder.setPiece(this.movedPiece.moveThisPiece(this));
            builder.setWhoPlaysNext(this.board.getCurrentPlayer().getOpponent().getPlayingSide());

            return builder.build();
        }

        @Override
        public boolean isOffense() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            OffensiveMove that = (OffensiveMove) o;
            return Objects.equals(attackedPiece, that.attackedPiece);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), attackedPiece);
        }

        /**
         * @return String corresponding to the PGN notation format
         */
        @Override
        public String toString() {
            return movedPiece.getPieceType() +
                    ifDoubles() +
                    "x" +
                    getAlphaNotationFromCoordinate(this.finalCoordinate);
        }
    }


    /**
     * Representation of an invalid/illegal move.
     * A move which is against the chess rules.
     */
    public static final class InvalidMove extends Move {

        public InvalidMove() {
            super(null, -1);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Cannot execute InvalidMove!");
        }

        @Override
        public int getCurrentCoordinate() {
            return -1;
        }

        /**
         * @return String corresponding to the PGN notation format
         */
        @Override
        public String toString() {
            return "InvalidMove";
        }
    }


    /**
     * Representation of a pawn move.
     * The move is to the Tile above the current one which in our indexes means +8.
     */
    public static class PawnMove extends Move {

        public PawnMove(final Board board, final Piece movedPiece, final int finalCoordinate) {
            super(board, movedPiece, finalCoordinate);
        }

        @Override
        public boolean equals(final Object o) {
            return super.equals(o);
        }

        /**
         * @return String corresponding to the PGN notation format
         */
        @Override
        public String toString() {
            return getAlphaNotationFromCoordinate(this.finalCoordinate);
        }
    }


    /**
     * Pawns attack in different directions than their normal move
     * so this representation takes care of this inconvenience.
     */
    public static class PawnOffensiveMove extends OffensiveMove {

        public PawnOffensiveMove(final Board board,
                                 final Piece movedPiece,
                                 final int finalCoordinate,
                                 final Piece attackedPiece) {
            super(board, movedPiece, finalCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object o) {
            return super.equals(o);
        }

        /**
         * @return String corresponding to the PGN notation format
         */
        @Override
        public String toString() {
            return getAlphaNotationFromCoordinate(this.movedPiece.getPiecePosition()).substring(0, 1) +
                    "x" +
                    getAlphaNotationFromCoordinate(this.finalCoordinate);
        }
    }


    /**
     * Representation of a Pawn jump.
     * This jump can only occur as the Pawn's first move.
     * The move is to the second Tile above the current one which in our indexes means +16.
     */
    public static final class PawnJumpMove extends Move {

        public PawnJumpMove(final Board board, final Piece movedPiece, final int finalCoordinate) {
            super(board, movedPiece, finalCoordinate);
        }

        /**
         * A normal move with the addition of noting the Pawn which jumped as EnPassantable for the next round.
         *
         * @return Board of how it should look after the Move is executed
         */
        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getLivePieces()) {
                builder.setPiece(piece);
            }

            for (final Piece piece : this.board.getCurrentPlayer().getLivePieces()) {
                if (!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            final Pawn movedPawn = (Pawn) this.movedPiece.moveThisPiece(this);
            movedPawn.isEnPassantable = true;
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);

            builder.setWhoPlaysNext(this.board.getCurrentPlayer().getOpponent().getPlayingSide());

            return builder.build();
        }

        @Override
        public boolean equals(final Object o) {
            return super.equals(o);
        }

        /**
         * @return String corresponding to the PGN notation format
         */
        @Override
        public String toString() {
            return getAlphaNotationFromCoordinate(this.finalCoordinate);
        }
    }


    /**
     * Offensive move specially for the EnPassant rule.
     */
    public static final class PawnEnPassantOffensiveMove extends PawnOffensiveMove {

        public PawnEnPassantOffensiveMove(final Board board,
                                          final Piece movedPiece,
                                          final int finalCoordinate,
                                          final Piece attackedPiece) {
            super(board, movedPiece, finalCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object o) {
            return super.equals(o);
        }
    }


    /**
     * Move promoting a Pawn to one these four Pieces
     * Queen, Knight, Rook, Bishop
     * This move occurs when a pawn is about tu jump from rank 7 to rank 8.
     */
    public static final class PawnPromotionMove extends PawnMove {

        protected final Move wrappedMove;
        protected final Pawn promotionPawn;
        protected String promotionPieceStr;

        public PawnPromotionMove(final Move wrappedMove, final String promotionPieceStr) {
            super(wrappedMove.getBoard(), wrappedMove.getMovedPiece(), wrappedMove.getFinalCoordinate());
            this.wrappedMove = wrappedMove;
            this.promotionPawn = (Pawn) wrappedMove.getMovedPiece();
            this.promotionPieceStr = promotionPieceStr;
        }

        /**
         * @return String corresponding to the PGN notation format
         */
        @Override
        public String toString() {
            return getAlphaNotationFromCoordinate(this.movedPiece.getPiecePosition()) +
                    "-" +
                    getAlphaNotationFromCoordinate(this.finalCoordinate) +
                    "=" +
                    this.promotionPieceStr;
        }

        /**
         * It executes the move and then sets on the Board the chosen new Piece instead of the Pawn
         *
         * @return Board of how it should look after the Move is executed
         */
        @Override
        public Board execute() {
            final Board boardOfPawnMove = this.wrappedMove.execute();
            final Builder builder = new Builder();

            for (final Piece piece : boardOfPawnMove.getCurrentPlayer().getOpponent().getLivePieces()) {
                builder.setPiece(piece);
            }

            for (final Piece piece : boardOfPawnMove.getCurrentPlayer().getLivePieces()) {
                if (!piece.equals(this.promotionPawn)) {
                    builder.setPiece(piece);
                }
            }

            builder.setPiece(this.promotionPawn.getPromotionPiece(this.promotionPieceStr).moveThisPiece(this));
            builder.setWhoPlaysNext(boardOfPawnMove.getCurrentPlayer().getPlayingSide()); // TODO opponent??

            return builder.build();
        }

        @Override
        public Piece getAttackedPiece() {
            return this.wrappedMove.getAttackedPiece();
        }

        @Override
        public boolean isOffense() {
            return this.wrappedMove.isOffense();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            PawnPromotionMove that = (PawnPromotionMove) o;
            return Objects.equals(wrappedMove, that.wrappedMove) && Objects.equals(promotionPawn, that.promotionPawn);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), wrappedMove, promotionPawn);
        }

        public void setPromotionPieceStr(final String promotionPieceStr) {
            this.promotionPieceStr = promotionPieceStr;
        }
    }


    /**
     * Base class for both King side and Queen side castling moves.
     */
    static abstract class CastlingMove extends Move {

        protected final Rook rook;
        protected final int rookTo;

        public CastlingMove(final Board board,
                            final Piece movedPiece,
                            final int finalCoordinate,
                            final Rook rook,
                            final int rookTo) {
            super(board, movedPiece, finalCoordinate);
            this.rook = rook;
            this.rookTo = rookTo;
        }

        /**
         * Executes the castling move following chess rules.
         *
         * @return Board of how it should look after the Move is executed
         */
        @Override
        public Board execute() {
            final Builder builder = new Builder();

            for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getLivePieces()) {
                builder.setPiece(piece);
            }

            for (final Piece piece : this.board.getCurrentPlayer().getLivePieces()) {
                if (!this.movedPiece.equals(piece) && !this.rook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            builder.setPiece(this.movedPiece.moveThisPiece(this));
            builder.setPiece(new Rook(this.rookTo, this.rook.getPlayingSide()));
            builder.setWhoPlaysNext(this.board.getCurrentPlayer().getOpponent().getPlayingSide());

            return builder.build();
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            CastlingMove that = (CastlingMove) o;
            return rookTo == that.rookTo && Objects.equals(rook, that.rook);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), rook, rookTo);
        }

        public Rook getRook() {
            return this.rook;
        }
    }


    /**
     * Castling move representation when player castles at the Queen side
     */
    public static final class QueenSideCastlingMove extends CastlingMove {

        public QueenSideCastlingMove(final Board board,
                                     final Piece movedPiece,
                                     final int finalCoordinate,
                                     final Rook rook,
                                     final int rookTo) {
            super(board, movedPiece, finalCoordinate, rook, rookTo);
        }

        @Override
        public boolean equals(final Object o) {
            return super.equals(o);
        }

        /**
         * @return String corresponding to the PGN notation format
         */
        @Override
        public String toString() { // PGN convention
            return "O-O-O";
        }
    }

    /**
     * Castling move representation when player castles at the King side
     */
    public static final class KingSideCastlingMove extends CastlingMove {

        public KingSideCastlingMove(final Board board,
                                    final Piece movedPiece,
                                    final int finalCoordinate,
                                    final Rook rook,
                                    final int rookTo) {
            super(board, movedPiece, finalCoordinate, rook, rookTo);
        }

        @Override
        public boolean equals(final Object o) {
            return super.equals(o);
        }

        /**
         * @return String corresponding to the PGN notation format
         */
        @Override
        public String toString() { // PGN convention
            return "O-O";
        }
    }


    /**
     * Class for creating an instance of a Move
     */
    public static class MoveCreator {

        private static final Move INVALID_MOVE = new InvalidMove();

        private MoveCreator() {
            throw new RuntimeException("MoveCreator class is not instantiable!");
        }

        /**
         * There only needs to be one instance of an Invalid move in the whole program.
         *
         * @return instance of InvalidMove
         */
        public static Move getInvalidMove() {
            return INVALID_MOVE;
        }

        /**
         * Creates an instance of a valid Move if it is a legal move in the @param board.
         * If the move is not legal, it returns an InvalidMove
         *
         * @param board
         * @param currentCoordinate
         * @param finalCoordinate
         * @return Move instance
         */
        public static Move createMove(final Board board,
                                      final int currentCoordinate,
                                      final int finalCoordinate) {

            for (final Move move : board.getAllLegalMoves()) {
                if (move.getCurrentCoordinate() == currentCoordinate
                        && move.getFinalCoordinate() == finalCoordinate) {
                    if (move instanceof PawnPromotionMove) {
                        ((PawnPromotionMove) move).setPromotionPieceStr(PawnPromotionChoiceBox.display());
                    }
                    return move;
                }
            }
            System.out.println("move creator: returning invalid move");
            return INVALID_MOVE;
        }
    }
}
