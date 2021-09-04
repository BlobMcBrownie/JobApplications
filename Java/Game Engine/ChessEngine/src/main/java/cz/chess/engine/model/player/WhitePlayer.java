package cz.chess.engine.model.player;

import cz.chess.engine.model.PlayingSide;
import cz.chess.engine.model.board.Board;
import cz.chess.engine.model.board.Move;
import cz.chess.engine.model.board.Move.KingSideCastlingMove;
import cz.chess.engine.model.board.Move.QueenSideCastlingMove;
import cz.chess.engine.model.board.Tile;
import cz.chess.engine.model.pieces.Piece;
import cz.chess.engine.model.pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Representation of the Player playing with White pieces
 *
 * @author Vojtěch Sýkora
 */
public class WhitePlayer extends Player {

    public WhitePlayer(final Board board, final Collection<Move> whiteLegalMoves, final Collection<Move> blackLegalMoves) {
        super(board, whiteLegalMoves, blackLegalMoves);
    }

    /**
     * Finds if this player can castle and if yes adds the castling moves to the returned Collection
     *
     * @param opponentsLegalMoves Collection of moves
     * @return Collection of castling moves, if none return an empty Collection
     */
    @Override
    protected Collection<Move> getCastlingMoves(Collection<Move> opponentsLegalMoves) {

        final List<Move> castlingMoves = new ArrayList<>();
        Tile rookTile;
        
        //Neither the king nor the chosen rook has previously moved.
        //The king is not currently in check.
        if (this.theKing.isFirstMove() && !this.isInCheck()) {

            // King side castling
            rookTile = this.board.getTile(7);
            if (rookTile.isFull()
                && rookTile.getPiece().isRook()
                && rookTile.getPiece().isFirstMove()
            ) {
                //There are no pieces between the king and the chosen rook.
                if (!this.board.getTile(5).isFull()
                    && !this.board.getTile(6).isFull()
                ) {
                    //The king does not pass through a square that is attacked by an enemy piece.
                    //The king does not end up in check. (True of any legal move.)
                    if (Player.findAttacksOnTile(5, opponentsLegalMoves).isEmpty()
                        && Player.findAttacksOnTile(6, opponentsLegalMoves).isEmpty()
                    ) {
                        castlingMoves.add(new KingSideCastlingMove(
                                this.board,
                                this.theKing,
                                6,
                                (Rook) rookTile.getPiece(),
                                5
                        ));
                        this.canCastleKingSide = true;
                    }
                }
            }

            // Queen side castling
            rookTile = this.board.getTile(0);
            if (rookTile.isFull()
                && rookTile.getPiece().isRook()
                && rookTile.getPiece().isFirstMove()
            ) {
                //There are no pieces between the king and the chosen rook.
                if (!this.board.getTile(1).isFull()
                    && !this.board.getTile(2).isFull()
                    && !this.board.getTile(3).isFull()
                ) {
                    //The king does not pass through a square that is attacked by an enemy piece.
                    //The king does not end up in check. (True of any legal move.)
                    if (Player.findAttacksOnTile(2, opponentsLegalMoves).isEmpty()
                        && Player.findAttacksOnTile(3, opponentsLegalMoves).isEmpty()
                    ) {
                        castlingMoves.add(new QueenSideCastlingMove(
                                this.board,
                                this.theKing,
                                2,
                                (Rook) rookTile.getPiece(),
                                3
                        ));
                        this.canCastleKingSide = true;
                    }
                }
            }
        }
        return castlingMoves;
    }

    @Override
    public String toString() {
        return "WhitePlayer";
    }

    @Override
    public Collection<Piece> getLivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public PlayingSide getPlayingSide() {
        return PlayingSide.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.getBlackPlayer();
    }
}
