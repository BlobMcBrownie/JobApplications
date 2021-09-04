package cz.chess.engine.model.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import cz.chess.engine.model.PlayingSide;
import cz.chess.engine.model.board.*;
import cz.chess.engine.model.pieces.King;
import cz.chess.engine.model.pieces.Piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import static cz.chess.engine.model.board.Move.*;

/**
 * Base class for both black and white players
 *
 * @author Vojtěch Sýkora
 */
public abstract class Player {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    protected final Board board;
    protected final King theKing;
    protected Collection<Move> legalMoves;
    protected Collection<Move> opponentsLegalMoves;
    protected boolean hasCastled, canCastleKingSide, canCastleQueenSide;

    public Player(Board board, Collection<Move> myLegalMoves, Collection<Move> opponentsLegalMoves) {
        this.board = board;
        this.theKing = checkForKing(board);
        this.opponentsLegalMoves = opponentsLegalMoves;
        this.hasCastled = false;
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(myLegalMoves, getCastlingMoves(opponentsLegalMoves)));
        this.canCastleKingSide = theKing.canCastleKingSide();
        this.canCastleQueenSide = theKing.canCastleQueenSide();
    }

    public Player(Board board, Collection<Move> myLegalMoves, Collection<Move> opponentsLegalMoves, boolean hasCastled) {
        this.board = board;
        this.theKing = checkForKing(board);
        this.opponentsLegalMoves = opponentsLegalMoves;
        this.hasCastled = hasCastled;
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(myLegalMoves, getCastlingMoves(opponentsLegalMoves)));
        this.canCastleKingSide = theKing.canCastleKingSide();
        this.canCastleQueenSide = theKing.canCastleQueenSide();
    }

    /**
     * Finds if this player can castle and if yes adds the castling moves to the returned Collection
     *
     * @param opponentsLegalMoves
     * @return Collection of castling moves, if none return an empty Collection
     */
    protected abstract Collection<Move> getCastlingMoves(Collection<Move> opponentsLegalMoves);

    /**
     * @return Collection of live pieces of the player's PlayingSide
     */
    public abstract Collection<Piece> getLivePieces();
    public abstract PlayingSide getPlayingSide();
    public abstract Player getOpponent();

    /**
     * Check if the @param board includes a King of this players PlayingSide.
     * Chess cannot be played without a King.
     *
     * @param board
     * @return King if there is one alive with the same PlayingSide, else RuntimeException
     */
    private King checkForKing(Board board) {
        for (final Piece piece : getLivePieces()) {
            if (piece.isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("A game without a King cannot be played!");
    }

    public boolean canCastleKingSide() {
        return canCastleKingSide;
    }

    public boolean canCastleQueenSide() {
        return canCastleQueenSide;
    }

    public Collection<Move> getLegalMoves() {
        return legalMoves;
    }

    /**
     * Sets new legal moves to where they belong.
     *
     * @param myLegalMoves
     * @param opponentsLegalMoves
     */
    public void setAllLegalMoves(Collection<Move> myLegalMoves, Collection<Move> opponentsLegalMoves) {
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(myLegalMoves, getCastlingMoves(opponentsLegalMoves)));
        this.opponentsLegalMoves = opponentsLegalMoves;
    }

    /**
     * Checks if the @param move is a legal move for this Player
     *
     * @param move 
     * @return true if the move is legal, else false
     */
    public boolean isLegalMove(final Move move) {
        return this.legalMoves.contains(move);
    }

    /**
     * Checks if this Player's King is in check
     * 
     * @return true if the King is in check, else false
     */
    public boolean isInCheck() {
        final int kingPosition = this.theKing.getPiecePosition();
        for (Move move : this.opponentsLegalMoves) {
            if (kingPosition == move.getFinalCoordinate()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this Player's King is in checkmate
     *
     * @return true if the King is in checkmate, else false
     */
    public boolean isInCheckMate() {
        //TODO and another piece cannot save it
        return isInCheck() && !hasEscapeMoves();
    }

    /**
     * Checks if this Player's King is in stalemate
     *
     * @return true if the King is in stalemate, else false
     */
    public boolean isInStaleMate() {
        return !isInCheck() && this.legalMoves.isEmpty();
    }

    /**
     * Checks if this Player can escape from a check.
     *
     * @return true if this Player can escape, else false
     */
    protected boolean hasEscapeMoves() {
        for (final Move move : this.legalMoves) {
            final MoveExecution moveExecution = makeMove(move);
            if (moveExecution.getMoveState().isFinished()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds all attacks on the Tile at index @param position.
     *
     * @param position
     * @param moves
     * @return Collection of moves which attack on the Tile
     */
    protected static Collection<Move> findAttacksOnTile (final int position, final Collection<Move> moves) {
        final List<Move> attacks = new ArrayList<>();
        for (final Move move : moves) {
            if (position == move.getFinalCoordinate()) {
                attacks.add(move);
            }
        }
        return attacks;
    }

    public boolean hasCastled() {
        return hasCastled;
    }

    /**
     * Creates an instance of MoveExecution representing the execution of the @param move.
     *
     * @param move
     * @return MoveExecution representing the @param move
     */
    public MoveExecution makeMove(final Move move) {

        if (!isLegalMove(move)) {
            return new MoveExecution(this.board, this.board, move, MoveState.ILLEGAL);
        }

        final Board afterBoard = move.execute();
        if (afterBoard.getCurrentPlayer().getOpponent().isInCheck()) {
            return new MoveExecution(this.board, afterBoard, move, MoveState.OPPONENT_IN_CHECK);
        }

        if (move instanceof KingSideCastlingMove || move instanceof QueenSideCastlingMove) {
            System.out.println("CASTLING MOVE");
            afterBoard.getCurrentPlayer().hasCastled = true;
        }

        afterBoard.getCurrentPlayer().canCastleKingSide= false;
        afterBoard.getCurrentPlayer().canCastleQueenSide = false;
        return new MoveExecution(this.board, afterBoard, move, MoveState.FINISHED);
    }

    /**
     * If the Player is a computer, this method helps algorithmically choose a move.
     *
     * @return chosen move
     */
    public Move chooseAMove() {
        if (this.legalMoves.isEmpty()) {
            LOGGER.warning("Calling choseAMove on an empty Collection legalMoves");
            return null;
        }

        Move choice = Utils.getPromotionMove(this.legalMoves);
        if (choice == null) {
            choice = Utils.getCastlingMove(this.legalMoves);
        }
        if (choice == null) {
            choice = Utils.getOffensiveMove(this.legalMoves);
        }
        if (choice == null) {
            choice = (Move) this.legalMoves.toArray()[0];
        }
        return choice;
    }

}
