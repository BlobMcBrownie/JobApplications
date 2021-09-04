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
 * Representation of the Knight chess piece
 *
 * @author Vojtěch Sýkora
 */
public class Knight extends Piece {

    private final int[] POSSIBLE_MOVE_VECTORS = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(final int piecePosition, final PlayingSide playingSide) {
        super(piecePosition, playingSide, PieceType.KNIGHT);
    }

    public Knight(int piecePosition, PlayingSide playingSide, boolean isFirstMove) {
        super(piecePosition, playingSide, PieceType.KNIGHT, isFirstMove);
    }

    /**
     * Finds all legal moves of this Knight which can be found on the @param board
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
                        || isSecondColumnMoveException(this.piecePosition, currentVector)
                        || isSeventhColumnMoveException(this.piecePosition, currentVector)
                        || isEightColumnMoveException(this.piecePosition, currentVector)
                ) {
                    continue;
                }

                final Tile possibleDestinationTile = board.getTile(possibleDestinationCoordinate);

                if (!possibleDestinationTile.isFull()) {
                    legalMoves.add(new NormalMove(board, this, possibleDestinationCoordinate));
                } else {
                    final Piece pieceAtDestination = possibleDestinationTile.getPiece();
                    final PlayingSide piecePlayingSide = pieceAtDestination.getPlayingSide();

                    if (piecePlayingSide != this.playingSide) {
                        legalMoves.add(new OffensiveMove(board, this, possibleDestinationCoordinate, pieceAtDestination));
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public String toString() {
        return PieceType.KNIGHT.toString();
    }

    @Override
    public boolean isKing() {
        return false;
    }

    @Override
    public boolean isRook() {
        return false;
    }

    @Override
    public String asWord() {
        return "knight";
    }

    /**
     * Returns a new Knight which represents the Knight after the @param move
     *
     * @param move
     * @return new moved Knight instance
     */
    @Override
    public Knight moveThisPiece(final Move move) {
        return new Knight(move.getFinalCoordinate(), move.getMovedPiece().playingSide);
    }

    /**
     * Checks if the Knight wants to do a legal move.
     * It happens when a Piece is at the edge of a Board and wants to move outside of the Board and inside through the other side.
     *
     * @param currentPosition
     * @param vectorOffset
     * @return true if the currentPosition+vectorOffset doesn't follow chess rules, else false
     */
    private static boolean isFirstColumnMoveException(final int currentPosition, final int vectorOffset) {
        return (Utils.isNthColumn(currentPosition,0) &&
                (vectorOffset == -17 || vectorOffset == -10 || vectorOffset == 6 || vectorOffset == 15));
    }

    /**
     * Checks if the Knight wants to do a legal move.
     * It happens when a Piece is at the edge of a Board and wants to move outside of the Board and inside through the other side.
     *
     * @param currentPosition
     * @param vectorOffset
     * @return true if the currentPosition+vectorOffset doesn't follow chess rules, else false
     */
    private static boolean isSecondColumnMoveException(final int currentPosition, final int vectorOffset) {
        return (Utils.isNthColumn(currentPosition,1) &&
                (vectorOffset == -10 || vectorOffset == 6));
    }

    /**
     * Checks if the Knight wants to do a legal move.
     * It happens when a Piece is at the edge of a Board and wants to move outside of the Board and inside through the other side.
     *
     * @param currentPosition
     * @param vectorOffset
     * @return true if the currentPosition+vectorOffset doesn't follow chess rules, else false
     */
    private static boolean isSeventhColumnMoveException(final int currentPosition, final int vectorOffset) {
        return (Utils.isNthColumn(currentPosition, 6) &&
                (vectorOffset == -6 || vectorOffset == 10));
    }

    /**
     * Checks if the Knight wants to do a legal move.
     * It happens when a Piece is at the edge of a Board and wants to move outside of the Board and inside through the other side.
     *
     * @param currentPosition
     * @param vectorOffset
     * @return true if the currentPosition+vectorOffset doesn't follow chess rules, else false
     */
    private static boolean isEightColumnMoveException(final int currentPosition, final int vectorOffset) {
        return (Utils.isNthColumn(currentPosition, 7) &&
                (vectorOffset == -15 || vectorOffset == -6 || vectorOffset == 10 || vectorOffset == 17));
    }
}
