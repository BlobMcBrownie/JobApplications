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

/* TODO:
 *   CASTLING
 */

/**
 * Representation of the Rook chess piece
 *
 * @author Vojtěch Sýkora
 */
public class Rook extends Piece {

    private final int[] POSSIBLE_MOVE_VECTORS = {-8, -1, 1, 8};

    public Rook(int piecePosition, PlayingSide playingSide) {
        super(piecePosition, playingSide, PieceType.ROOK);
    }

    public Rook(int piecePosition, PlayingSide playingSide, boolean isFirstMove) {
        super(piecePosition, playingSide, PieceType.ROOK, isFirstMove);
    }

    /**
     * Finds all legal moves of this Rook which can be found on the @param board
     *
     * @param board
     * @return List of the possible legal moves
     */
    @Override
    public List<Move> getLegalMoves(final Board board) {
        List<Move> legalMoves = new ArrayList<>();
        int possibleDestinationCoordinate;

        for (final int currentVector : POSSIBLE_MOVE_VECTORS) {
            possibleDestinationCoordinate = this.piecePosition;

            while (Utils.isValidCoordinate(possibleDestinationCoordinate)) {
                if (isFirstColumnMoveException(possibleDestinationCoordinate, currentVector)
                    || isEightColumnMoveException(possibleDestinationCoordinate, currentVector)
                ) {
                    break;
                }
                possibleDestinationCoordinate += currentVector;

                if (Utils.isValidCoordinate(possibleDestinationCoordinate)) {
                    Tile possibleDestinationTile = board.getTile(possibleDestinationCoordinate);
                    if (!possibleDestinationTile.isFull()) {
                        legalMoves.add(new NormalMove(board, this, possibleDestinationCoordinate));
                    } else {
                        final Piece pieceAtDestination = possibleDestinationTile.getPiece();
                        if (pieceAtDestination.getPlayingSide() != this.playingSide) {
                            legalMoves.add(new OffensiveMove(board, this, possibleDestinationCoordinate, pieceAtDestination));
                        }
                        break;
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public String toString() {
        return PieceType.ROOK.toString();
    }

    @Override
    public boolean isKing() {
        return false;
    }

    @Override
    public boolean isRook() {
        return true;
    }

    @Override
    public String asWord() {
        return "rook";
    }

    /**
     * Returns a new Rook which represents the Rook after the @param move
     *
     * @param move
     * @return new moved Rook instance
     */
    @Override
    public Rook moveThisPiece(final Move move) {
        return new Rook(move.getFinalCoordinate(), move.getMovedPiece().playingSide);
    }

    /**
     * Checks if the Rook wants to do a legal move.
     * It happens when a Piece is at the edge of a Board and wants to move outside of the Board and inside through the other side.
     *
     * @param currentPosition
     * @param vectorOffset
     * @return true if the currentPosition+vectorOffset doesn't follow chess rules, else false
     */
    private static boolean isFirstColumnMoveException(final int currentPosition, final int vectorOffset) {
        return (Utils.isNthColumn(currentPosition,0) && vectorOffset == -1);
    }

    /**
     * Checks if the Rook wants to do a legal move.
     * It happens when a Piece is at the edge of a Board and wants to move outside of the Board and inside through the other side.
     *
     * @param currentPosition
     * @param vectorOffset
     * @return true if the currentPosition+vectorOffset doesn't follow chess rules, else false
     */
    private static boolean isEightColumnMoveException(final int currentPosition, final int vectorOffset) {
        return (Utils.isNthColumn(currentPosition, 7) && vectorOffset == 1);
    }
}
