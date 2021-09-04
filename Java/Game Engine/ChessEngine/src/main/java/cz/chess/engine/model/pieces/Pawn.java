package cz.chess.engine.model.pieces;

import com.google.common.collect.ImmutableList;
import cz.chess.engine.model.PlayingSide;
import cz.chess.engine.model.board.Board;
import cz.chess.engine.model.board.Move;
import cz.chess.engine.model.board.Move.*;
import cz.chess.engine.model.board.Tile;
import cz.chess.engine.model.board.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/* TODO:
*   EN PASSANT
*   PAWN PROMOTION
*/

/**
 * Representation of the Pawn chess piece
 *
 * @author Vojtěch Sýkora
 */
public class Pawn extends Piece {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /* If a Pawn jumps by 16, isEnPassantable becomes true
     * only for the next round and then it is forever false.
     */
    public boolean isEnPassantable = false;

    /* Normal moves {8, 16 (only if it's the first move)}
     *  Offensive moves {7,9}
     */
    private final int[] POSSIBLE_MOVE_VECTORS = {8, 16, 7, 9};

    public Pawn(final int piecePosition, final PlayingSide playingSide) {
        super(piecePosition, playingSide, PieceType.PAWN);
    }

    public Pawn(int piecePosition, PlayingSide playingSide, boolean isFirstMove, boolean isEnPassantable) {
        super(piecePosition, playingSide, PieceType.PAWN, isFirstMove);
        this.isEnPassantable = isEnPassantable;
    }

    /**
     * @return true if it's this Pawn's first move, else false
     */
    @Override
    public boolean isFirstMove() {
        if (this.playingSide.isWhite() && this.piecePosition > 7 && this.piecePosition < 16) {
            return true;
        } else return playingSide.isBlack() && this.piecePosition > 47 && this.piecePosition < 56;
    }

    /**
     * Finds all legal moves of this Pawn which can be found on the @param board
     * Taking into consideration PawnMove, PawnJump, EnPassant and PawnPromotion
     *
     * @param board
     * @return List of the possible legal moves
     */
    @Override
    public List<Move> getLegalMoves(final Board board) {
        List<Move> legalMoves = new ArrayList<>();
        int possibleDestinationCoordinate;
        for (final int currentVector : POSSIBLE_MOVE_VECTORS) {
            possibleDestinationCoordinate = this.piecePosition + (this.getPlayingSide().getDirection() * currentVector);

            if (Utils.isValidCoordinate(possibleDestinationCoordinate)) {

                if (currentVector == 8 && !board.getTile(possibleDestinationCoordinate).isFull()) {
                    if (this.playingSide.isPawnPromotionTile(possibleDestinationCoordinate)) { // pawn promotion
                        legalMoves.add(new PawnPromotionMove(new PawnMove(board,
                                this,
                                possibleDestinationCoordinate),
                                "Q"));
                    } else { // normal move
                        legalMoves.add(new PawnMove(board, this, possibleDestinationCoordinate));
                    }
                }
                else if (currentVector == 16
                        && this.isFirstMove()
                        && !board.getTile(possibleDestinationCoordinate).isFull()
                        && !board.getTile(this.piecePosition + (this.getPlayingSide().getDirection() * 8)).isFull()
                    ) {
                    legalMoves.add(new PawnJumpMove(board, this, possibleDestinationCoordinate));
                }
                else if (currentVector == 7 // Offensive Move
                        && !(
                                (this.playingSide.isWhite() && Utils.isNthColumn(this.piecePosition, 0))
                                || (this.playingSide.isBlack() && Utils.isNthColumn(this.piecePosition, 7))
                            )
                ) {
                    final Tile possibleDestinationTile = board.getTile(possibleDestinationCoordinate);
                    if (possibleDestinationTile.isFull()) {
                        final Piece pieceAtDestination = possibleDestinationTile.getPiece();
                        if (pieceAtDestination.getPlayingSide() != this.playingSide) {
                            if (this.playingSide.isPawnPromotionTile(possibleDestinationCoordinate)) { // pawn promotion
                                legalMoves.add(new PawnPromotionMove(new PawnOffensiveMove(board,
                                        this,
                                        possibleDestinationCoordinate,
                                        pieceAtDestination),
                                        "Q"));
                            } else {
                                legalMoves.add(new PawnOffensiveMove(board, this, possibleDestinationCoordinate, pieceAtDestination));
                            }
                        }
                    }
                    else if (board.getEnPassantPawn() != null) { // EN PASSANT
                        if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - this.playingSide.getDirection())) {
                            final Piece possibleEnPassantPiece = board.getEnPassantPawn();
                            if (possibleEnPassantPiece.getPlayingSide() != this.playingSide) {
                                legalMoves.add(new PawnEnPassantOffensiveMove(board, this, possibleDestinationCoordinate, possibleEnPassantPiece));
                            }
                        }
                    }
                }
                else if (currentVector == 9 // Offensive Move
                        && !(
                                (this.playingSide.isWhite() && Utils.isNthColumn(this.piecePosition, 7))
                                || (this.playingSide.isBlack() && Utils.isNthColumn(this.piecePosition, 0))
                            )
                ) {
                    final Tile possibleDestinationTile = board.getTile(possibleDestinationCoordinate);
                    if (possibleDestinationTile.isFull()) {
                        final Piece pieceAtDestination = possibleDestinationTile.getPiece();
                        if (pieceAtDestination.getPlayingSide() != this.playingSide) {
                            if (this.playingSide.isPawnPromotionTile(possibleDestinationCoordinate)) { // pawn promotion
                                legalMoves.add(new PawnPromotionMove(new PawnOffensiveMove(board,
                                        this,
                                        possibleDestinationCoordinate,
                                        pieceAtDestination),
                                        "Q"));
                            } else {
                                legalMoves.add(new PawnOffensiveMove(board, this, possibleDestinationCoordinate, pieceAtDestination));
                            }
                        }
                    }
                    else if (board.getEnPassantPawn() != null) { // EN PASSANT
                        if (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + this.playingSide.getDirection())) {
                            final Piece possibleEnPassantPiece = board.getEnPassantPawn();
                            if (possibleEnPassantPiece.getPlayingSide() != this.playingSide) {
                                legalMoves.add(new PawnEnPassantOffensiveMove(board, this, possibleDestinationCoordinate, possibleEnPassantPiece));
                            }
                        }
                    }
                }
            }
        }
//        LOGGER.log(Level.SEVERE, String.valueOf(legalMoves));
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
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
        return "pawn";
    }

    /**
     * Returns a new Pawn which represents the Pawn after the @param move
     *
     * @param move
     * @return new moved Pawn instance
     */
    @Override
    public Pawn moveThisPiece(final Move move) {
        this.isEnPassantable = move instanceof PawnJumpMove;
        return new Pawn(move.getFinalCoordinate(), move.getMovedPiece().playingSide);
    }

    /**
     * Used for choosing to which piece will the Pawn promote.
     *
     * @param pieceStr PieceType.toString()
     * @return new instance of the Piece which user wants to promote to with the same position and PlayingSide
     */
    public Piece getPromotionPiece(String pieceStr) {
        switch (pieceStr) {
            case "N":
                return new Knight(this.piecePosition, this.playingSide, false);
            case "R":
                return new Rook(this.piecePosition, this.playingSide, false);
            case "B":
                return new Bishop(this.piecePosition, this.playingSide, false);
            default:
                return new Queen(this.piecePosition, this.playingSide, false);
        }
    }
}
