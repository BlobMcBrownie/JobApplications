package cz.chess.engine.view_controller.boxes;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Pop-up GUI box for choosing the Pawn Promotion piece
 *
 * @author Vojtěch Sýkora
 */
public class PawnPromotionChoiceBox {

    private static Stage window;
    private static String ret = "Q";

    /**
     * displays the window and returns the choice
     *
     * @return String corresponding to the piece toString()
     */
    public static String display() {
        window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("PawnPromotion Choice");
        window.setMinWidth(400);
        window.setMinHeight(250);

        Label label = new Label("Promote pawn to what?");
        label.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
        label.setTextFill(Color.WHITE);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(20.0);
        hBox.getChildren().addAll(createButton("Queen", "Q"),
                createButton("Knight", "N"),
                createButton("Rook", "R"),
                createButton("Bishop", "B")
                );

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, hBox);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2F3437;");

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        return ret;
    }

    private static Button createButton(String label, String representation) {
        Button button = new Button(label);
        button.setFont(Font.font("Times New Roman", FontWeight.BOLD, 16));
        button.setPrefSize(100, 40);
        button.setOnAction(e -> {
            ret = representation;
            window.close();
        });
        return button;
    }
    

}
