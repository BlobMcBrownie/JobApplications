package cz.chess.engine.view_controller.ingame;

import cz.chess.engine.model.board.PGN;
import cz.chess.engine.view_controller.GraphicsUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static cz.chess.engine.view_controller.App.chessBoard;

/**
 * Creates the scene for viewing the Game Record
 * Can be accessed from GameView
 *
 * @author Vojtěch Sýkora
 */
public class GameRecord {

    /**
     * Creates the GameRecord Scene
     *
     * @return GameRecord Scene
     */
    public static Scene create() {
        BorderPane layout = new BorderPane();

        VBox center = new VBox();
        center.setAlignment(Pos.CENTER);
        center.setSpacing(20);
            Label label = new Label("GAME RECORD");
            label.setFont(Font.font("Times New Roman", FontWeight.BOLD, 30));
            TextArea record = new TextArea(PGN.createGameRecord(chessBoard.getMoveLog()));
            record.setEditable(false);
            record.setWrapText(true);
        center.getChildren().addAll(label, record);
        layout.setCenter(center);

        HBox top = GraphicsUtils.createBackButtonOnRight();
        layout.setTop(top);

        return new Scene(layout, 1280, 720);
    }
}
