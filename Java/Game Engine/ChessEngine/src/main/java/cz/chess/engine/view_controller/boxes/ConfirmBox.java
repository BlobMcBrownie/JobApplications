package cz.chess.engine.view_controller.boxes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Pop-up GUI box for confirmation (YES/NO)
 *
 * @author Vojtěch Sýkora
 */
public class ConfirmBox {

    static boolean answer;

    /**
     * Displays the @param message and waits for user to click either YES or NO
     *
     * @param title
     * @param message
     * @return boolean corresponding to the yes/no answer
     */
    public static boolean display(String title, String message) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        window.setMinHeight(150);

        // TOP
        HBox topMenu = new HBox();
        topMenu.setAlignment(Pos.CENTER);
        Label label = new Label(message);
        label.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
        label.setTextFill(Color.WHITE);
        label.setPadding(new Insets(20,10,10,10));
        topMenu.getChildren().addAll(label);

        // CENTER
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10,10,10,10));
        gridPane.setHgap(10);

        // YES
        Button yesButton = new Button("YES");
        yesButton.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
        yesButton.setPrefSize(70, 40);
        yesButton.setOnAction(e -> {
            answer = true;
            window.close();
        });
        GridPane.setConstraints(yesButton, 0,0);

        // NO
        Button noButton = new Button("NO");
        noButton.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
        noButton.setPrefSize(70, 40);        noButton.setOnAction(e -> {
            answer = false;
            window.close();
        });
        GridPane.setConstraints(noButton, 1,0);
        gridPane.getChildren().addAll(yesButton, noButton);

        BorderPane layout = new BorderPane();
        layout.setTop(topMenu);
        layout.setCenter(gridPane);
        layout.setStyle("-fx-background-color: #2F3437;");

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        return answer;
    }


}
