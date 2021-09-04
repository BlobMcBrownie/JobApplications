package cz.chess.engine.model.board;

import com.google.common.collect.ImmutableList;
import cz.chess.engine.model.PlayingSide;
import cz.chess.engine.model.board.Move.*;
import cz.chess.engine.model.pieces.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Utilities class for the whole board package.
 *
 * @author Vojtěch Sýkora
 */
public class Utils {

    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;
    private static final List<String> ALPHA_NOTATION = createAlphaNotationList();

    /**
     * Creates a list of strings where each index corresponds to the file and rank chess board representation
     * The list is used for converting a position between the two coordination systems.
     *
     * @return List of strings
     */
    private static List<String> createAlphaNotationList() {
        return ImmutableList.copyOf(Arrays.asList(
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"
                ));
    }

    private Utils() {
        throw new RuntimeException("Utils class cannot be instantiated!");
    }

    /**
     * Checks if the Tile coordinate is on the board.
     *
     * @param coordinate
     * @return true if the coordinate is valid, else false
     */
    public static boolean isValidCoordinate(final int coordinate) {
        return (coordinate >= 0) && (coordinate < 64);
    }

    /**
     * Checks if the coordinate is in the Nth column.
     * Column numbers are indexed 0-7 instead of 1-8.
     *
     * @param coordinate
     * @param column
     * @return true if coordinate in column, else false
     */
    public static boolean isNthColumn(final int coordinate, final int column) {
        return (coordinate % 8 == column);
    }

    /**
     * Checks if the coordinate is in the Nth row.
     * Row numbers are indexed 0-7 instead of 1-8.
     *
     * @param coordinate
     * @param row
     * @return true if coordinate in row, else false
     */
    public static boolean isNthRow(final int coordinate, final int row) {
        return (coordinate >= row*8) && (coordinate <= 7 + row*8);
    }

    /**
     * Converts index to file+rank notation
     *
     * @param coordinate
     * @return String file+rank
     */
    public static String getAlphaNotationFromCoordinate(final int coordinate) {
        return ALPHA_NOTATION.get(coordinate);
    }

    /**
     * Converts file+rank to index notation
     *
     * @param position
     * @return int index
     */
    public static int getCoordinateFromAlphaNotation(final String position) {
         return ALPHA_NOTATION.indexOf(position);
    }

    /**
     * Checks if the game ended.
     * Checks for checkmate and stalemate
     *
     * @param board
     * @return true if game ended, else false
     */
    public static boolean gameEnded(final Board board) {
        return board.getCurrentPlayer().isInCheckMate() ||
                board.getCurrentPlayer().isInStaleMate();
    }

    /**
     * Checks if one of the players is in check.
     *
     * @param board
     * @return true if one of the players is in check, else false
     */
    public static boolean someoneIsInCheck(final Board board) {
        return board.getWhitePlayer().isInCheck() || board.getBlackPlayer().isInCheck();
    }

    /**
     * Tries to find any type of offensive move in the @param moves
     *
     * @param moves
     * @return move if it finds one, else null
     */
    public static Move getOffensiveMove(final Collection<Move> moves) {
        for (final Move move : moves) {
            if (move instanceof OffensiveMove) {
                return (OffensiveMove) move;
            } else if (move instanceof PawnOffensiveMove) {
                return (PawnOffensiveMove) move;
            } else if (move instanceof PawnEnPassantOffensiveMove) {
                return (PawnEnPassantOffensiveMove) move;
            }
        }
        return null;
    }

    /**
     * Tries to find any type of castling move in the @param moves
     *
     * @param moves
     * @return move if it finds one, else null
     */
    public static Move getCastlingMove(final Collection<Move> moves) {
        for (final Move move : moves) {
            if (move instanceof QueenSideCastlingMove) {
                return (QueenSideCastlingMove) move;
            } else if (move instanceof KingSideCastlingMove) {
                return (KingSideCastlingMove) move;
            }
        }
        return null;
    }

    /**
     * Tries to find a promotion move in the @param moves
     *
     * @param moves
     * @return move if it finds one, else null
     */
    public static Move getPromotionMove(final Collection<Move> moves) {
        for (final Move move : moves) {
            if (move instanceof PawnPromotionMove) {
                return (PawnPromotionMove) move;
            }
        }
        return null;
    }

    /**
     * Recalculates the index for FEN notation.
     * Input is starting on 0 from top left, output is starting from zero on bottom left.
     *
     * @param index
     * @return correct index
     */
    public static int getCorrectIndex(final int index) {
        final List<Integer> indexes = ImmutableList.copyOf(Arrays.asList(
                56, 57, 58, 59, 60, 61, 62, 63,
                48, 49, 50, 51, 52, 53, 54, 55,
                40, 41, 42, 43, 44, 45, 46, 47,
                32, 33, 34, 35, 36, 37, 38, 39,
                24, 25, 26, 27, 28, 29, 30, 31,
                16, 17, 18, 19, 20, 21, 22, 23,
                8, 9, 10, 11, 12, 13, 14, 15,
                0, 1, 2, 3, 4, 5, 6, 7
        ));
        return indexes.get(index);

    }

    /**
     * Returns a new Piece for the custom menu board.
     * Piece is selected based on the @param customSelectedPiece
     *
     * @param customSelectedPiece
     * @param index
     * @return new Piece of wanted PieceType
     */
    public static Piece returnNewCustomPiece(String customSelectedPiece, int index) {
        final String[] info = customSelectedPiece.split(" ");
        final PlayingSide playingSide = info[0].equals("WHITE") ? PlayingSide.WHITE : PlayingSide.BLACK;

        switch (info[1]) {
            case "R":
                return new Rook(index, playingSide);
            case "N":
                return new Knight(index, playingSide);
            case "B":
                return new Bishop(index, playingSide);
            case "Q":
                return new Queen(index, playingSide);
            case "K":
                return new King(index, playingSide, true, true);
            case "P":
                return new Pawn(index, playingSide);
            default:
                throw new RuntimeException("customSelectedPiece string not valid ... " + customSelectedPiece);
        }

    }
}
