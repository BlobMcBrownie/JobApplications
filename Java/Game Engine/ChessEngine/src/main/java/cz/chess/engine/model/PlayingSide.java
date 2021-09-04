package cz.chess.engine.model;

import cz.chess.engine.model.board.Utils;
import cz.chess.engine.model.player.BlackPlayer;
import cz.chess.engine.model.player.Player;
import cz.chess.engine.model.player.WhitePlayer;

/**
 * Enum for representing the two sides that play chess, White and Black
 *
 * @author Vojtěch Sýkora
 */
public enum PlayingSide {
    WHITE {
        @Override
        public int getDirection() {
            return 1;
        }

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public Player playerVersion(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return whitePlayer;
        }

        @Override
        public boolean isPawnPromotionTile(int index) {
            return Utils.isNthRow(index, 7);
        }

        @Override
        public String toString() {
            return "WHITE";
        }
    },
    BLACK {
        @Override
        public int getDirection() {
            return -1;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public Player playerVersion(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return blackPlayer;
        }

        @Override
        public boolean isPawnPromotionTile(int index) {
            return Utils.isNthRow(index, 0);
        }

        @Override
        public String toString() {
            return "BLACK";
        }
    };

    /**
     * Both players sit on opposite sides of the board which needs to be accounted for.
     *
     * @return 1 if WHITE, -1 if BLACK
     */
    public abstract int getDirection();

    public abstract boolean isWhite();
    public abstract boolean isBlack();

    /**
     * @param whitePlayer instance of WhitePlayer
     * @param blackPlayer instance of BlackPlayer
     * @return the player which has the same color as the PlayingSide which called this method
     */
    public abstract Player playerVersion(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer);

    /**
     * Checks if the Tile at @param index is in the row for promoting a Pawn.
     * 8th row for WHITE
     * 1st row for BLACK
     *
     * @param index int representing the position on a Board
     * @return true if the Tile is in the wanted row, else false
     */
    public abstract boolean isPawnPromotionTile(int index);
}
