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
 * Representation of the Bishop chess piece
 *
 * @author Vojtěch Sýkora
 */
public class Bishop extends Piece {

    private final int[] POSSIBLE_MOVE_VECTORS = {-9, -7, 7, 9};

    public Bishop(int piecePosition, PlayingSide playingSide) {
        super(piecePosition, playingSide, PieceType.BISHOP);
    }

    public Bishop(int piecePosition, PlayingSide playingSide, boolean isFirstMove) {
        super(piecePosition, playingSide, PieceType.BISHOP, isFirstMove);
    }

    /**
     * Finds all legal moves of this Bishop which can be found on the @param board
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
                    || isEightColumnMoveException(possibleDestinationCoordinate, currentVector)) {
                    break;
                }
                possibleDestinationCoordinate += currentVector;

                if (Utils.isValidCoordinate(possibleDestinationCoordinate)) {
                    Tile possibleDestinationTile = board.getTile(possibleDestinationCoordinate);
                    if (!possibleDestinationTile.isFull()) {
                        legalMoves.add(new NormalMove(board, this, possibleDestinationCoordinate));
                    } else {
                        final Piece pieceAtDestination = possibleDestinationTile.getPiece();
                        final PlayingSide piecePlayingSide = pieceAtDestination.getPlayingSide();
                        if (piecePlayingSide != this.playingSide) {
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
        return PieceType.BISHOP.toString();
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
        return "bishop";
    }

    /**
     * Returns a new Bishop which represents the Bishop after the @param move
     *
     * @param move
     * @return new moved Bishop instance
     */
    @Override
    public Bishop moveThisPiece(final Move move) {
        return new Bishop(move.getFinalCoordinate(), move.getMovedPiece().playingSide);
    }

    /**
     * Checks if the Bishop wants to do a legal move.
     * It happens when a Piece is at the edge of a Board and wants to move outside of the Board and inside through the other side.
     *
     * @param currentPosition
     * @param vectorOffset
     * @return true if the currentPosition+vectorOffset doesn't follow chess rules, else false
     */
    private static boolean isFirstColumnMoveException(final int currentPosition, final int vectorOffset) {
        return (Utils.isNthColumn(currentPosition,0) &&
                (vectorOffset == -9 || vectorOffset == 7));
    }

    /**
     * Checks if the Bishop wants to do a legal move.
     * It happens when a Piece is at the edge of a Board and wants to move outside of the Board and inside through the other side.
     *
     * @param currentPosition
     * @param vectorOffset
     * @return true if the currentPosition+vectorOffset doesn't follow chess rules, else false
     */
    private static boolean isEightColumnMoveException(final int currentPosition, final int vectorOffset) {
        return (Utils.isNthColumn(currentPosition, 7) &&
                (vectorOffset == -7 || vectorOffset == 9));
    }
}
