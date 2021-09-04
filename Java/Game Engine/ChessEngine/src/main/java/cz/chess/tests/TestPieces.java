package cz.chess.tests;

import cz.chess.engine.model.PlayingSide;
import cz.chess.engine.model.board.*;
import cz.chess.engine.model.board.Board.Builder;
import cz.chess.engine.model.pieces.*;
import org.junit.Test;

import java.util.Collection;

import static cz.chess.engine.model.board.Move.MoveCreator.createMove;
import static cz.chess.engine.model.board.Utils.getCoordinateFromAlphaNotation;
import static org.junit.Assert.*;

/**
 * UnitTest
 * Tests the whole pieces package at once
 *
 * @author Vojtěch Sýkora
 */
public class TestPieces {

    /**
     * Tests the column exceptions which occur due to the 0-63 tile indexing
     */
    @Test
    public void testLegalMoveExceptions() {
        final Builder builder = new Builder();
        builder.setPiece(new King(0, PlayingSide.WHITE, false, false));
        builder.setPiece(new Bishop(16, PlayingSide.WHITE));
        builder.setPiece(new Knight(8, PlayingSide.WHITE));

        builder.setPiece(new King(63, PlayingSide.BLACK, false, false));
        builder.setPiece(new Rook(47, PlayingSide.BLACK));
        builder.setPiece(new Queen(57, PlayingSide.BLACK));
        builder.setWhoPlaysNext(PlayingSide.WHITE);
        final Board board = builder.build();

        final King king = new King(0, PlayingSide.WHITE, false, false);
        assertEquals(king.getLegalMoves(board).size(), 2);

        final Bishop bishop = new Bishop(16, PlayingSide.WHITE);
        assertEquals(bishop.getLegalMoves(board).size(), 7);

        final Knight knight = new Knight(8, PlayingSide.WHITE);
        assertEquals(knight.getLegalMoves(board).size(), 3);

        final King king2 = new King(63, PlayingSide.BLACK, false, false);
        assertEquals(king2.getLegalMoves(board).size(), 3);

        final Rook rook = new Rook(47, PlayingSide.BLACK);
        assertEquals(rook.getLegalMoves(board).size(), 13);

        final Queen queen = new Queen(57, PlayingSide.BLACK);
        assertEquals(queen.getLegalMoves(board).size(), 20);
    }

    /**
     * Tests if EnPassant is implemented correctly
     */
    @Test
    public void testEnPassant() {
        final Board.Builder builder = new Board.Builder();
        // Black Layout
        builder.setPiece(new King(60, PlayingSide.BLACK,  false, false));
        builder.setPiece(new Pawn(51, PlayingSide.BLACK));
        // White Layout
        builder.setPiece(new Pawn(12, PlayingSide.WHITE));
        builder.setPiece(new King(4, PlayingSide.WHITE, false, false));
        // Set the current player
        builder.setWhoPlaysNext(PlayingSide.WHITE);
        final Board board = builder.build();

        final Move m1 = createMove(board,
                getCoordinateFromAlphaNotation("e2"),
                getCoordinateFromAlphaNotation("e4"));
        final MoveExecution t1 = board.getCurrentPlayer().makeMove(m1);
        assertTrue(t1.getMoveState().isFinished());

        final Move m2 = createMove(t1.getAfterBoard(),
                getCoordinateFromAlphaNotation("e8"),
                getCoordinateFromAlphaNotation("f8"));
        final MoveExecution t2 = t1.getAfterBoard().getCurrentPlayer().makeMove(m2);
        assertTrue(t2.getMoveState().isFinished());

        final Move m3 = createMove(t2.getAfterBoard(),
                getCoordinateFromAlphaNotation("e4"),
                getCoordinateFromAlphaNotation("e5"));
        final MoveExecution t3 = t2.getAfterBoard().getCurrentPlayer().makeMove(m3);
        assertTrue(t3.getMoveState().isFinished());

        final Move m4 = createMove(t3.getAfterBoard(),
                getCoordinateFromAlphaNotation("d7"),
                getCoordinateFromAlphaNotation("d5"));
        final MoveExecution t4 = t3.getAfterBoard().getCurrentPlayer().makeMove(m4);
        assertTrue(t4.getMoveState().isFinished());

        final Board lastBoard = reloadBoard(t4.getAfterBoard());
        final Move enPassantMove = createMove(lastBoard,
                getCoordinateFromAlphaNotation("e5"),
                getCoordinateFromAlphaNotation("d6"));
        assertTrue(lastBoard.getCurrentPlayer().getLegalMoves().contains(enPassantMove));
        assertNotNull(lastBoard.getEnPassantPawn());

        final MoveExecution enPassant = lastBoard.getCurrentPlayer().makeMove(enPassantMove);
        assertTrue(enPassant.getMoveState().isFinished());
    }

    /**
     * Tests all the small methods in Pieces
     */
    @Test
    public void testSmallMethods() {
        final Rook rook = new Rook(47, PlayingSide.BLACK);
        final King king = new King(0, PlayingSide.WHITE, false, false);
        final Queen queen = new Queen(57, PlayingSide.BLACK);

        assertFalse(rook.isKing());
        assertTrue(king.isKing());
        assertFalse(queen.isKing());

        assertTrue(rook.isRook());
        assertFalse(king.isRook());
        assertFalse(queen.isRook());

        assertEquals(rook.toString(), "R");
        assertEquals(rook.asWord(), "rook");

        assertEquals(king.toString(), "K");
        assertEquals(king.asWord(), "king");

        assertEquals(queen.toString(), "Q");
        assertEquals(queen.asWord(), "queen");

        assertEquals(rook.getPiecePosition(), 47);
        assertEquals(rook.getPieceType(), Piece.PieceType.ROOK);
        assertEquals(rook.getPlayingSide(), PlayingSide.BLACK);
        assertTrue(rook.isFirstMove());

        assertEquals(king.getPiecePosition(), 0);
        assertEquals(king.getPieceType(), Piece.PieceType.KING);
        assertEquals(king.getPlayingSide(), PlayingSide.WHITE);
        assertTrue(king.isFirstMove());

        assertEquals(queen.getPiecePosition(), 57);
        assertEquals(queen.getPieceType(), Piece.PieceType.QUEEN);
        assertEquals(queen.getPlayingSide(), PlayingSide.BLACK);
        assertTrue(queen.isFirstMove());
    }

    /**
     * Tests if the legal moves are calculated correctly
     */
    @Test
    public void testLegalMoves() {
        final Builder builder = new Builder();
        builder.setPiece(new King(6, PlayingSide.WHITE,false, false));
        builder.setPiece(new Queen(11, PlayingSide.WHITE));
        builder.setPiece(new King(63, PlayingSide.BLACK, false, false));
        builder.setWhoPlaysNext(PlayingSide.WHITE);
        final Board board = builder.build();

        final Collection<Move> whiteLegalMoves = board.getWhitePlayer().getLegalMoves();
        final Collection<Move> blackLegalMoves = board.getBlackPlayer().getLegalMoves();

        assertEquals(whiteLegalMoves.size(), 28);
        assertEquals(blackLegalMoves.size(), 3);

        assertTrue(whiteLegalMoves.contains(createMove(board, 6, 7)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 6, 15)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 6, 14)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 6, 13)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 6, 5)));

        assertTrue(whiteLegalMoves.contains(createMove(board, 11, 2)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 11, 3)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 11, 4)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 11, 12)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 11, 13)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 11, 14)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 11, 15)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 11, 8)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 11, 9)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 11, 10)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 11, 18)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 11, 25)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 11, 32)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 11, 20)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 11, 29)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 11, 38)));
        assertTrue(whiteLegalMoves.contains(createMove(board, 11, 47)));

        assertTrue(blackLegalMoves.contains(createMove(board, 63, 62)));
        assertTrue(blackLegalMoves.contains(createMove(board, 63, 54)));
        assertTrue(blackLegalMoves.contains(createMove(board, 63, 55)));
    }

    /**
     * Tests castling on the KingSide
     */
    @Test
    public void KingSideCastling() {
        final Board board = Board.createStartingBoard();

        final MoveExecution me1 = board.getCurrentPlayer().makeMove(createMove(board, 12, 28));
        assertTrue(me1.getMoveState().isFinished());

        final MoveExecution me2 = me1.getAfterBoard().getCurrentPlayer().makeMove(createMove(me1.getAfterBoard(), 50, 34));
        assertTrue(me2.getMoveState().isFinished());

        final MoveExecution me3 = me2.getAfterBoard().getCurrentPlayer().makeMove(createMove(me2.getAfterBoard(), 6, 23));
        assertTrue(me3.getMoveState().isFinished());

        final MoveExecution me4 = me3.getAfterBoard().getCurrentPlayer().makeMove(createMove(me3.getAfterBoard(), 59, 41));
        assertTrue(me4.getMoveState().isFinished());

        final MoveExecution me5 = me4.getAfterBoard().getCurrentPlayer().makeMove(createMove(me4.getAfterBoard(), 5, 26));
        assertTrue(me5.getMoveState().isFinished());

        final MoveExecution me6 = me5.getAfterBoard().getCurrentPlayer().makeMove(createMove(me5.getAfterBoard(), 62, 47));
        assertTrue(me6.getMoveState().isFinished());

        final Move castlingMove = createMove(me6.getAfterBoard(), 4, 6);
        assertTrue(me6.getAfterBoard().getCurrentPlayer().getLegalMoves().contains(castlingMove));

        final MoveExecution castling = me6.getAfterBoard().getCurrentPlayer().makeMove(castlingMove);
        assertTrue(castling.getMoveState().isFinished());
        assertTrue(castling.getAfterBoard().getBlackPlayer().hasCastled());
        assertFalse(castling.getAfterBoard().getBlackPlayer().canCastleKingSide());
        assertFalse(castling.getAfterBoard().getBlackPlayer().canCastleQueenSide());
    }

    /**
     * Tests that player cannot castle out of check
     */
    @Test
    public void testNoCastlingOutOfCheck() {
        final Board board = FEN.createBoardFromFEN("r3k2r/1pN1nppp/p3p3/3p4/8/8/PPPK1PPP/R6R b kq - 1 18");
        final Move illegalCastleMove = createMove(board, 60, 58);
        final MoveExecution moveExecution = board.getCurrentPlayer()
                .makeMove(illegalCastleMove);
        assertFalse(moveExecution.getMoveState().isFinished());
    }

    /**
     * Tests if CheckMate works
     */
    @Test
    public void testCheckMate() {
        final Board board = FEN.createBoardFromFEN("k7/8/8/8/1Q6/1Q6/8/3K4 w - - 0 1");
        assertFalse(board.getCurrentPlayer().isInCheckMate());
        final MoveExecution t1 = board.getCurrentPlayer()
                .makeMove(Move.MoveCreator.createMove(board,
                        Utils.getCoordinateFromAlphaNotation("b3"),
                        Utils.getCoordinateFromAlphaNotation("a3")));
        assertTrue(t1.getMoveState().isFinished());
        assertTrue(t1.getAfterBoard().getCurrentPlayer().isInCheckMate());
    }

    /**
     * Tests if StaleMate works
     */
    @Test
    public void testStaleMate() {
        final Board board = FEN.createBoardFromFEN("7k/5K2/6Q1/8/8/8/8/8 b - - 0 1");
        assertFalse(board.getCurrentPlayer().isInStaleMate());
        final MoveExecution t1 = board.getCurrentPlayer()
                .makeMove(Move.MoveCreator.createMove(board,
                        Utils.getCoordinateFromAlphaNotation("c5"),
                        Utils.getCoordinateFromAlphaNotation("c6")));
        assertFalse(t1.getMoveState().isFinished());
        assertFalse(t1.getAfterBoard().getCurrentPlayer().getOpponent().isInStaleMate());
    }

    private Board reloadBoard(Board afterBoard) {
        afterBoard.setEnPassantPawn((Pawn) afterBoard.getPiece(35));
        afterBoard.recalculateLegalMoves();
        return afterBoard;
    }
}
