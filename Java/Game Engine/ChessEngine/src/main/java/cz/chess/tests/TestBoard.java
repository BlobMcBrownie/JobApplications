package cz.chess.tests;

import com.google.common.collect.Iterables;
import cz.chess.engine.model.PlayingSide;
import cz.chess.engine.model.board.Board;
import cz.chess.engine.model.board.Board.Builder;
import cz.chess.engine.model.board.Move;
import cz.chess.engine.model.board.Tile;
import cz.chess.engine.model.board.Utils;
import cz.chess.engine.model.pieces.*;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * UnitTest
 * Tests the whole board package at once
 * From Board to Move to Utils
 *
 * @author Vojtěch Sýkora
 */
public class TestBoard {

    /**
     * Tests if the board toString is correct
     */
    @Test
    public void testToString() {
        final Board board = Board.createStartingBoard();
        final String startingBoard =
                "  r  n  b  q  k  b  n  r\n" +
                "  p  p  p  p  p  p  p  p\n" +
                "  -  -  -  -  -  -  -  -\n" +
                "  -  -  -  -  -  -  -  -\n" +
                "  -  -  -  -  -  -  -  -\n" +
                "  -  -  -  -  -  -  -  -\n" +
                "  P  P  P  P  P  P  P  P\n" +
                "  R  N  B  Q  K  B  N  R\n";
        assertEquals(board.toString(), startingBoard);
    }

    /**
     * Creates a startingBoard and tests everything that is set up on the board
     */
    @Test
    public void testStartingBoard() {
        final Board board = Board.createStartingBoard();

        assertFalse(board.getCurrentPlayer().isInCheck());
        assertFalse(board.getCurrentPlayer().isInCheckMate());
        assertFalse(board.getCurrentPlayer().hasCastled());
        assertTrue(board.getCurrentPlayer().canCastleKingSide());
        assertTrue(board.getCurrentPlayer().canCastleQueenSide());
        assertEquals(board.getCurrentPlayer(), board.getWhitePlayer());
        assertEquals(board.getCurrentPlayer().getLegalMoves().size(), 20);

        assertFalse(board.getCurrentPlayer().getOpponent().isInCheck());
        assertFalse(board.getCurrentPlayer().getOpponent().isInCheckMate());
        assertFalse(board.getCurrentPlayer().getOpponent().hasCastled());
        assertTrue(board.getCurrentPlayer().getOpponent().canCastleKingSide());
        assertTrue(board.getCurrentPlayer().getOpponent().canCastleQueenSide());
        assertEquals(board.getCurrentPlayer().getOpponent(), board.getBlackPlayer());
        assertEquals(board.getCurrentPlayer().getOpponent().getLegalMoves().size(), 20);

        assertTrue(board.getWhitePlayer().toString().equals("WhitePlayer"));
        assertTrue(board.getBlackPlayer().toString().equals("BlackPlayer"));

        final Collection<Piece> allPieces = board.getWhitePieces();
        allPieces.addAll(board.getBlackPieces());
        assertEquals(allPieces.size(), 32);
        assertFalse(allPieces.isEmpty());
        assertNotEquals(allPieces, null);

        final Iterable<Move> allLegalMoves = board.getAllLegalMoves();
        assertEquals(Iterables.size(allLegalMoves), 40);
        assertNotEquals(allLegalMoves, null);
        for(final Move move : allLegalMoves) {
            assertFalse(move.isOffense());
            assertFalse(move.isCastlingMove());
        }

        assertFalse(Utils.gameEnded(board));
        assertFalse(Utils.someoneIsInCheck(board));
        assertEquals(board.getEnPassantPawn(), null);
        assertEquals(board.getPiece(16), null);
        assertEquals(board.getPiece(28), null);
        assertEquals(board.getPiece(45), null);
    }

    /**
     * Tests if getTile returns the correct Tile
     * with all functions of Tile working correctly
     */
    @Test
    public void getTile() {
        final Board board = Board.createStartingBoard();
        final Tile tile0 = board.getTile(0);
        assertEquals(tile0.getTileCoordinate(), 0);
        assertEquals(tile0.getPiece(), new Rook(0, PlayingSide.WHITE));
        assertTrue(tile0.isFull());
        assertTrue(tile0.isEdge());
        assertTrue(tile0.isCorner());

        final Tile tile27 = board.getTile(27);
        assertEquals(tile27.getTileCoordinate(), 27);
        assertEquals(tile27.getPiece(), null);
        assertFalse(tile27.isFull());
        assertFalse(tile27.isEdge());
        assertFalse(tile27.isCorner());
    }

    /**
     * Tests if trying to create a board without kings throws an exception
     */
    @Test(expected=RuntimeException.class)
    public void testBoardWithoutKings() {
        final Builder builder = new Builder();
        // WHITE starting layout (at the bottom)
        builder.setPiece(new Rook(0, PlayingSide.WHITE));
        builder.setPiece(new Knight(1, PlayingSide.WHITE));
        builder.setPiece(new Bishop(2, PlayingSide.WHITE));
        builder.setPiece(new Queen(3, PlayingSide.WHITE));
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

        builder.setPiece(new Bishop(61, PlayingSide.BLACK));
        builder.setPiece(new Knight(62, PlayingSide.BLACK));
        builder.setPiece(new Rook(63, PlayingSide.BLACK));

        builder.setWhoPlaysNext(PlayingSide.WHITE);
        builder.build();
    }

    /**
     * tests if the current Player has everything setup correctly
     */
    @Test
    public void getCurrentPlayer() {
        final Board board = Board.createStartingBoard();
        assertEquals(board.getCurrentPlayer(), board.getWhitePlayer());
        assertEquals(board.getCurrentPlayer().getLegalMoves().size(), 20);
        assertFalse(board.getCurrentPlayer().isInCheck());
        assertFalse(board.getCurrentPlayer().isInCheckMate());
        assertFalse(board.getCurrentPlayer().hasCastled());
        assertTrue(board.getCurrentPlayer().canCastleKingSide());
        assertTrue(board.getCurrentPlayer().canCastleQueenSide());
    }

    /**
     * Tests setting up a custom board
     * and if all the variables in it are set correctly.
     */
    @Test
    public void testCustomBoard() {
        final Builder builder = new Builder();

        builder.setWhoPlaysNext(PlayingSide.BLACK);

        // WHITE
        builder.setPiece(new King(6,
                PlayingSide.WHITE,
                false,
                false,
                false,
                true));
        builder.setPiece(new Pawn(15, PlayingSide.WHITE));
        builder.setPiece(new Knight(13, PlayingSide.WHITE));

        // BLACK
        builder.setPiece(new King(62,
                PlayingSide.BLACK,
                false,
                false,
                false,
                true));
        builder.setPiece(new Pawn(53, PlayingSide.BLACK));
        builder.setPiece(new Knight(55, PlayingSide.BLACK));

        final Board board = builder.build();

        assertFalse(board.getCurrentPlayer().isInCheck());
        assertFalse(board.getCurrentPlayer().isInCheckMate());
        assertFalse(board.getCurrentPlayer().isInStaleMate());

        assertFalse(board.getCurrentPlayer().getOpponent().isInCheck());
        assertFalse(board.getCurrentPlayer().getOpponent().isInCheckMate());
        assertFalse(board.getCurrentPlayer().getOpponent().isInStaleMate());

        assertEquals(board.getCurrentPlayer(), board.getBlackPlayer());
        assertEquals(board.getCurrentPlayer().getOpponent(), board.getWhitePlayer());

        assertEquals(board.getWhitePlayer().getLegalMoves().size(), 11);
        assertEquals(board.getBlackPlayer().getLegalMoves().size(), 8);
        assertNull(board.getTile(7).getPiece());
        assertNull(board.getTile(63).getPiece());
        assertNull(board.getEnPassantPawn());
        assertEquals(board.getGameTiles().size(), 64);
    }

    /**
     * Tests setting the currentPlayer manually
     */
    @Test
    public void setCurrentPlayer() {
        final Board board = Board.createStartingBoard();
        assertEquals(board.getCurrentPlayer(), board.getWhitePlayer());

        board.setCurrentPlayer("BLACK");
        assertEquals(board.getCurrentPlayer(), board.getBlackPlayer());
    }

}