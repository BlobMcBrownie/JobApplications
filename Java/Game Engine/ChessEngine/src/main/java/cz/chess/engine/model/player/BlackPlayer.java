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
 * Representation of the Player playing with Black pieces
 *
 * @author Vojtěch Sýkora
 */
public class BlackPlayer extends Player {

    public BlackPlayer(final Board board, final Collection<Move> blackLegalMoves, final Collection<Move> whiteLegalMoves) {
        super(board, blackLegalMoves, whiteLegalMoves);
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
        if (theKing.isFirstMove() && !this.isInCheck()) {
            // King side castling
            rookTile = this.board.getTile(63);
            if (rookTile.isFull()
                    && rookTile.getPiece().isRook()
                    && rookTile.getPiece().isFirstMove()
            ) {
                //There are no pieces between the king and the chosen rook.
                if (!this.board.getTile(61).isFull()
                        && !this.board.getTile(62).isFull()
                ) {
                    //The king does not pass through a square that is attacked by an enemy piece.
                    //The king does not end up in check. (True of any legal move.)
                    if (Player.findAttacksOnTile(61, opponentsLegalMoves).isEmpty()
                            && Player.findAttacksOnTile(62, opponentsLegalMoves).isEmpty()
                    ) {
                        castlingMoves.add(new KingSideCastlingMove(
                                this.board,
                                this.theKing,
                                62,
                                (Rook) rookTile.getPiece(),
                                61
                        ));
                        this.canCastleKingSide = true;
                    }
                }
            }

            // Queen side castling
            rookTile = this.board.getTile(56);
            if (rookTile.isFull()
                    && rookTile.getPiece().isRook()
                    && rookTile.getPiece().isFirstMove()
            ) {
                //There are no pieces between the king and the chosen rook.
                if (!this.board.getTile(57).isFull()
                        && !this.board.getTile(58).isFull()
                        && !this.board.getTile(59).isFull()
                ) {
                    //The king does not pass through a square that is attacked by an enemy piece.
                    //The king does not end up in check. (True of any legal move.)
                    if (Player.findAttacksOnTile(58, opponentsLegalMoves).isEmpty()
                            && Player.findAttacksOnTile(59, opponentsLegalMoves).isEmpty()
                    ) {
                        castlingMoves.add(new QueenSideCastlingMove(
                                this.board,
                                this.theKing,
                                58,
                                (Rook) rookTile.getPiece(),
                                59
                        ));
                        this.canCastleQueenSide = true;
                    }
                }
            }
        }
        return castlingMoves;
    }

    @Override
    public String toString() {
        return "BlackPlayer";
    }

    @Override
    public Collection<Piece> getLivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public PlayingSide getPlayingSide() {
        return PlayingSide.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.getWhitePlayer();
    }
}
