package cz.chess.engine.view_controller.boxes;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static cz.chess.engine.view_controller.App.mainMenu;
import static cz.chess.engine.view_controller.App.window;

/**
 * Pop-up GUI box for informing the user that the game ended
 *
 * @author Vojtěch Sýkora
 */
public class GameEndBox {

    /**
     * Displays the score and which mate ended the game
     *
     * @param mate
     * @param score
     */
    public static void display(final String mate, final String score) {
        Stage stage = new Stage();

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Game Ended");
        stage.setMinWidth(400);
        stage.setMinHeight(250);

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("GAME ENDED ... here is the score\n");
        stringBuffer.append("Game ended in a " + mate + "\n");
        stringBuffer.append("Final Score is ... White " + score + " Black");

        Label label = new Label(stringBuffer.toString());
        label.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
        label.setTextFill(Color.WHITE);

        Button backToMainMenu = new Button("Back to Main Menu");
        backToMainMenu.setAlignment(Pos.CENTER);
        backToMainMenu.setOnAction(e -> {
            stage.close();
            window.setScene(mainMenu);
        });


        VBox layout = new VBox(20);
        layout.getChildren().addAll(label, backToMainMenu);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2F3437;");

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.showAndWait();
    }
    

}
