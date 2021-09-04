package cz.chess.engine.view_controller;

import cz.chess.engine.model.board.Board;

/**
 * Class for testing the game without a GUI
 *
 * @author Vojtěch Sýkora
 */
public class GameController {
    public static void main(String[] args) {
        Board board = Board.createStartingBoard();
        System.out.println(board);

    }
}
