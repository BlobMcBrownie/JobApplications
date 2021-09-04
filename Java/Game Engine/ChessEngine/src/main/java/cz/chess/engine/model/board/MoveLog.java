package cz.chess.engine.model.board;

import java.util.ArrayList;
import java.util.List;


public class MoveLog {
    private final List<Move> moves;

    public MoveLog() {
        this.moves = new ArrayList<>();
    }

    public List<Move> getMoves() {
        return this.moves;
    }

    /**
     * Adds the @param move to the Log of moves
     *
     * @param move
     */
    public void addMove(final Move move) {
        this.moves.add(move);
    }

    /**
     * Removes the @param move from the Log of moves
     *
     * @param move
     * @return true if the move was inside the moves List
     */
    public boolean removeMove(final Move move) {
        return this.moves.remove(move);
    }

    /**
     * Removes the move from the @param index of the Log of moves
     *
     * @param index
     * @return removed Move if the move was inside the moves List, else null
     */
    public Move removeMove(final int index) {
        return this.moves.remove(index);
    }

    /**
     * @return how many moves are saved in the Log
     */
    public int size() {
        return this.moves.size();
    }

    public void clear() {
        this.moves.clear();
    }

    /**
     * Creates a PGN format string representation of the Log of moves
     *
     * @return String
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        int i = 1;
        for (Move move : moves) {
            sb.append(i + "." + move + " ");
            i++;
        }
        //TODO add result
        return sb.toString();
    }
}
