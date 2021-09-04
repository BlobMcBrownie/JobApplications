package cz.chess.engine.model.board;

/**
 * Enum representing states of a move
 *
 * @author Vojtěch Sýkora
 */
public enum MoveState {
    FINISHED {
        @Override
        public boolean isFinished() {
            return true;
        }
    },
    ILLEGAL{
        @Override
        public boolean isFinished() {
            return false;
        }
    },
    OPPONENT_IN_CHECK {
        @Override
        public boolean isFinished() {
            return false;
        }
    };


    /**
     * Informs user about whether the move has been executed or not.
     *
     * @return true if the move is already finished
     */
    public abstract boolean isFinished();
}
