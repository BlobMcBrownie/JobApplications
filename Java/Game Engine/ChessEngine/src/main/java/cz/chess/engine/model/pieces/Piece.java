package cz.chess.engine.model.pieces;

import cz.chess.engine.model.PlayingSide;
import cz.chess.engine.model.board.Board;
import cz.chess.engine.model.board.Move;
import cz.chess.engine.model.board.MoveExecution;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cz.chess.engine.model.board.Move.MoveCreator.createMove;

/**
 * Base class for all chess Pieces
 *
 * @author Vojtěch Sýkora
 */
public abstract class Piece {

    protected final int piecePosition;
    protected final PlayingSide playingSide;
    protected boolean isFirstMove;
    protected final PieceType pieceType;

    public Piece(int piecePosition, PlayingSide playingSide, PieceType pieceType) {
        this.piecePosition = piecePosition;
        this.playingSide = playingSide;
        this.pieceType = pieceType;
        this.isFirstMove = true;
    }

    public Piece(int piecePosition, PlayingSide playingSide, PieceType pieceType, final boolean isFirstMove) {
        this.piecePosition = piecePosition;
        this.playingSide = playingSide;
        this.pieceType = pieceType;
        this.isFirstMove = isFirstMove;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return piecePosition == piece.piecePosition
                && isFirstMove == piece.isFirstMove
                && playingSide == piece.playingSide
                && pieceType == piece.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(piecePosition, playingSide, isFirstMove, pieceType);
    }

    /**
     * Finds all legal moves of this Piece which can be found on the @param board
     *
     * @param board
     * @return List of the possible legal moves
     */
    public abstract List<Move> getLegalMoves(final Board board);

    /**
     * Returns a new Piece which represents the Piece after the @param move
     *
     * @param move
     * @return new moved Piece instance
     */
    public abstract Piece moveThisPiece(final Move move);

    /**
     * @return true if the Piece is a King, else false
     */
    public abstract boolean isKing();

    /**
     * @return true if the Piece is a Rook, else false
     */
    public abstract boolean isRook();


    /**
     * @return String as the name of the Piece to lowercase
     */
    public abstract String asWord();

    public int getPiecePosition() {
        return piecePosition;
    }

    public PlayingSide getPlayingSide() {
        return playingSide;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }

    /**
     * Filters out the legalMoves which would have resulted into a check
     *
     * @param legalMoves
     * @param board
     * @return filtered list of legal moves
     */
    public List<Move> filerLegalMoves(List<Move> legalMoves, final Board board) {
        List<Move> newMoves = new ArrayList<>();
        for (final Move move : legalMoves) {
            final MoveExecution me = board.getCurrentPlayer().makeMove(createMove(board,
                    move.getCurrentCoordinate(),
                    move.getFinalCoordinate()));
            if(!me.getAfterBoard().getCurrentPlayer().isInCheck() ||
                   !me.getAfterBoard().getCurrentPlayer().isInCheckMate() ||
                    !me.getAfterBoard().getCurrentPlayer().isInStaleMate()
            ) {
                newMoves.add(move);
            }
        }
        return newMoves;
    }


    /**
     * Enum for working with piece types and their corresponding toString for a one letter Piece representation
     */
    public enum PieceType {
        KING("K"),
        QUEEN("Q"),
        ROOK("R"),
        BISHOP("B"),
        KNIGHT("N"),
        PAWN("P");

        private final String pieceName;

        PieceType(final String pieceName) {
            this.pieceName = pieceName;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }
    }
}
