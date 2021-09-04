package cz.chess.engine.model.board;

/**
 * Representation of a Move being executed.
 * Moving from one Board to another Board.
 *
 * @author Vojtěch Sýkora
 */
public class MoveExecution {

    private final Board beforeBoard;
    private final Board afterBoard;
    private final Move move;
    private final MoveState moveState;

    public MoveExecution(Board beforeBoard, final Board afterBoard, final Move move, final MoveState moveState) {
        this.beforeBoard = beforeBoard;
        this.afterBoard = afterBoard;
        this.move = move;
        this.moveState = moveState;
    }

    public MoveState getMoveState() {
        return moveState;
    }

    public Board getAfterBoard() {
        return afterBoard;
    }

    public Move getMove() {
        return move;
    }
}
