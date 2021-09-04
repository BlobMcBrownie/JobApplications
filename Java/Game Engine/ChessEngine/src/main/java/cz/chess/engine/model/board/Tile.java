package cz.chess.engine.model.board;

import cz.chess.engine.model.pieces.Piece;

import java.util.Arrays;

/**
 * Object representation of a single Tile on the chess board.
 * The Tile can be either empty or full (has a Piece on it).
 *
 * @author Vojtěch Sýkora
 */
public abstract class Tile {

    private int tileCoordinate;

    private Tile(int tileCoordinate) {
        this.tileCoordinate = tileCoordinate;
    }

    /**
     * @return true if there is a Piece on this tile, false if it is empty
     */
    public abstract boolean isFull();

    /**
     * @return Piece object on this Tile, null if there is no Piece
     */
    public abstract Piece getPiece();

    public int getTileCoordinate() {
        return tileCoordinate;
    }

    /**
     * @return true if this Tile is an edge Tile is an edge tile on the chess board
     */
    public boolean isEdge() {
        final int[] edgeCoordinates = {0,1,2,3,4,5,6,7,8,15,16,23,24,31,32,39,40,47,48,55,56,63};
        return Arrays.stream(edgeCoordinates).anyMatch(i -> i == tileCoordinate);
    }

    /**
     * @return true if this Tile is a corner Tile is an edge tile on the chess board
     */
    public boolean isCorner() {
        final int[] edgeCoordinates = {0,7,56,63};
        return Arrays.stream(edgeCoordinates).anyMatch(i -> i == tileCoordinate);
    }

    /**
     * Returns a new instance of either FullTile or EmptyTile depending on the @param piece
     *
     * @param coordinate
     * @param piece
     * @return new instance of one of the Tile subclasses
     */
    public static Tile createTile(final int coordinate, final Piece piece) {
        return piece == null ? new EmptyTile(coordinate) : new FullTile(coordinate, piece);
    }


    /**
     * Tile subclass representing an occupied Tile.
     */
    public static final class FullTile extends Tile {

        private Piece piece;

        public FullTile(int tileCoordinate, Piece piece) {
            super(tileCoordinate);
            this.piece = piece;
        }

        @Override
        public String toString() {
            return piece.getPlayingSide().isWhite() ?
                    piece.toString() : piece.toString().toLowerCase();
        }

        @Override
        public boolean isFull() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return this.piece;
        }
    }


    /**
     * Tile subclass representing an empty Tile
     */
    public static final class EmptyTile extends Tile {

        public EmptyTile(int tileCoordinate) {
            super(tileCoordinate);
        }

        @Override
        public String toString() {
            return "-";
        }

        @Override
        public boolean isFull() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }
    }


}
