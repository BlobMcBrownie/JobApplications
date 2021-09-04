package cz.chess.engine.model.pieces;

import com.google.common.collect.ImmutableList;
import cz.chess.engine.model.PlayingSide;
import cz.chess.engine.model.board.Board;
import cz.chess.engine.model.board.Move;
import cz.chess.engine.model.board.Move.NormalMove;
import cz.chess.engine.model.board.Move.OffensiveMove;
import cz.chess.engine.model.board.Tile;
import cz.chess.engine.model.board.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of the King chess piece
 *
 * @author Vojtěch Sýkora
 */
public class King extends Piece {

    private final int[] POSSIBLE_MOVE_VECTORS = {-9, -8, -7,-1, 1, 7, 8, 9};
    private final boolean hasCastled;
    private boolean canCastleKingSide;
    private boolean canCastleQueenSide;

    public King(final int piecePosition,
                final PlayingSide playingSide,
                final boolean canCastleKingSide,
                final boolean canCastleQueenSide
                ) {
        super(piecePosition, playingSide, PieceType.KING);
        this.canCastleKingSide = canCastleKingSide;
        this.canCastleQueenSide = canCastleQueenSide;
        this.hasCastled = false;
    }

    public King(int piecePosition,
                PlayingSide playingSide,
                boolean isFirstMove,
                final boolean canCastleKingSide,
                final boolean canCastleQueenSide,
                final boolean hasCastled
            ) {
        super(piecePosition, playingSide, PieceType.KING, isFirstMove);
        this.canCastleKingSide = canCastleKingSide;
        this.canCastleQueenSide = canCastleQueenSide;
        this.hasCastled = hasCastled;
    }

    /**
     * Finds all legal moves of this King which can be found on the @param board
     *
     * @param board
     * @return List of the possible legal moves
     */
    @Override
    public List<Move> getLegalMoves(final Board board) {
        List<Move> legalMoves = new ArrayList<>();
        int possibleDestinationCoordinate;

        for (final int currentVector : POSSIBLE_MOVE_VECTORS) {
            possibleDestinationCoordinate = this.piecePosition + currentVector;

            if (Utils.isValidCoordinate(possibleDestinationCoordinate)) {
                if (isFirstColumnMoveException(this.piecePosition, currentVector)
                    || isEightColumnMoveException(this.piecePosition, currentVector)) {
                    continue;
                }

                Tile possibleDestinationTile = board.getTile(possibleDestinationCoordinate);
                if (!possibleDestinationTile.isFull()) {
                    legalMoves.add(new NormalMove(board, this, possibleDestinationCoordinate));
                } else {
                    final Piece pieceAtDestination = possibleDestinationTile.getPiece();
                    if (pieceAtDestination.getPlayingSide() != this.playingSide) {
                        legalMoves.add(new OffensiveMove(board, this, possibleDestinationCoordinate, pieceAtDestination));
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public String toString() {
        return PieceType.KING.toString();
    }

    @Override
    public String asWord() {
        return "king";
    }

    @Override
    public boolean isKing() {
        return true;
    }

    @Override
    public boolean isRook() {
        return false;
    }

    /**
     * Returns a new King which represents the King after the @param move
     *
     * @param move
     * @return new moved King instance
     */
    @Override
    public King moveThisPiece(final Move move) {
        return new King(move.getFinalCoordinate(),
                move.getMovedPiece().playingSide,
                false,
                false);
    }

    /**
     * Checks if the King wants to do a legal move.
     * It happens when a Piece is at the edge of a Board and wants to move outside of the Board and inside through the other side.
     *
     * @param currentPosition
     * @param vectorOffset
     * @return true if the currentPosition+vectorOffset doesn't follow chess rules, else false
     */
    private static boolean isFirstColumnMoveException(final int currentPosition, final int vectorOffset) {
        return (Utils.isNthColumn(currentPosition,0) &&
                (vectorOffset == -9 || vectorOffset == -1 || vectorOffset == 7));
    }

    /**
     * Checks if the King wants to do a legal move.
     * It happens when a Piece is at the edge of a Board and wants to move outside of the Board and inside through the other side.
     *
     * @param currentPosition
     * @param vectorOffset
     * @return true if the currentPosition+vectorOffset doesn't follow chess rules, else false
     */
    private static boolean isEightColumnMoveException(final int currentPosition, final int vectorOffset) {
        return (Utils.isNthColumn(currentPosition, 7) &&
                (vectorOffset == -7 || vectorOffset == 1 || vectorOffset == 9));
    }

    public boolean canCastleKingSide() {
        return canCastleKingSide;
    }

    public boolean canCastleQueenSide() {
        return canCastleQueenSide;
    }

    public void setCastling(final boolean canCastleKingSide, final boolean canCastleQueenSide) {
        this.canCastleKingSide = canCastleKingSide;
        this.canCastleQueenSide = canCastleQueenSide;
    }
}
