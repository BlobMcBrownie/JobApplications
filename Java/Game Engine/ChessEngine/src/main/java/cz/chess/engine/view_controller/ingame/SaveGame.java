package cz.chess.engine.view_controller.ingame;

import cz.chess.engine.model.board.FEN;
import cz.chess.engine.model.board.PGN;
import cz.chess.engine.view_controller.GraphicsUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import static cz.chess.engine.view_controller.App.chessBoard;

/**
 * Scene where player can save the game using FEN or PGN
 *
 * @author Vojtěch Sýkora
 */
public class SaveGame {
    /**
     * Creates the Scene
     *
     * @return SaveGame Scene
     */
    public static Scene create() {
        BorderPane layout = new BorderPane();

        HBox center = new HBox();
        center.setAlignment(Pos.CENTER);
        center.setSpacing(100.0);
        // PGN
        TextArea pgnLabel = new TextArea("PGN NOTATION\n\n" + PGN.createPGNString(chessBoard.getBoard(), chessBoard.getMoveLog()));
        pgnLabel.setEditable(false);
        pgnLabel.setWrapText(true);
        layout.setCenter(pgnLabel);

        // FEN
        TextArea fenLabel = new TextArea("FEN NOTATION\n\n" + FEN.createFENFromBoard(chessBoard.getBoard()));
        fenLabel.setEditable(false);
        fenLabel.setWrapText(true);

        center.getChildren().addAll(pgnLabel, fenLabel);
        layout.setCenter(center);

        // BACK BUTTON
        HBox top = GraphicsUtils.createBackButtonOnRight();

        layout.setTop(top);

        return new Scene(layout, 1280, 720);
    }
}
