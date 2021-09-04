package cz.chess.engine.view_controller.boxes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Pop-up GUI box for information alerts
 *
 * @author Vojtěch Sýkora
 */
public class AlertBox {

    /**
     * Displays the @param message in a small modal GUI window.
     *
     * @param title
     * @param message
     */
    public static void display(String title, String message) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        window.setMinHeight(150);

        // TOP
        HBox layout = new HBox();
        layout.setAlignment(Pos.CENTER);
        Label label = new Label(message);
        label.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
        label.setTextFill(Color.WHITE);
        label.setPadding(new Insets(5,5,5,5));
        layout.getChildren().add(label);

        layout.setStyle("-fx-background-color: #2F3437;");

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

}
