package cz.chess.engine.view_controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static cz.chess.engine.view_controller.App.mainMenu;
import static cz.chess.engine.view_controller.App.window;

/**
 * Utilities class for the GUI part of this project
 *
 * @author Vojtěch Sýkora
 */
public class GraphicsUtils {

    /**
     * Creates a button that takes the user back to the MainMenu Scene
     *
     * @return back Button
     */
    public static Button createBackButton () {
        Button backButton = new Button("Back");
        backButton.setPrefSize(80,30);
        backButton.setPadding(new Insets(10,10,10,10));
        backButton.setFont(Font.font("Times New Roman", FontWeight.BOLD, 18));
        backButton.setOnAction(e -> {
            System.out.println("Going back to MAIN MENU");
            window.setScene(mainMenu);
        });
        return backButton;
    }

    /**
     * Additionally to creating the back button,
     * this method makes it positioned on the right
     *
     * @return Hbox containing the back button
     */
    public static HBox createBackButtonOnRight() {
        Button backButton = createBackButton();

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().add(backButton);
        return hBox;
    }

    /**
     * Creates the GUI timer seen during playing in GameView
     *
     * @return timer Label
     */
    public static Label createTimerLabel() {
        Label label = new Label("15:00");
        label.setFont(Font.font("Times New Roman", FontWeight.BOLD, 25));
        label.setTextFill(Color.WHITE);
        return label;
    }
}
