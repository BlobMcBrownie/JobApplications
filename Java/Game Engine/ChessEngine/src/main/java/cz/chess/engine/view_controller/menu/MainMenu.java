package cz.chess.engine.view_controller.menu;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static cz.chess.engine.view_controller.App.*;

/**
 * Main Menu is the first scene a user sees when opening the app
 * It has the game title and a menu of 4 buttons which send the user to other Scenes
 *
 * @author Vojtěch Sýkora
 */
public class MainMenu {

    private MainMenu() {
        throw new RuntimeException("MainMenu cant be instantiated");
    }

    /**
     * Creates Main Menu Scene with all its elements and functions
     *
     * @return MainMenu Scene
     */
    public static Scene create() {
        Button normalGame = createMenuButton("NORMAL GAME", 0, 0);
        normalGame.setOnAction(e -> {
            System.out.println("User chose NORMAL GAME");
            window.setScene(gameView);
        });

        Button customGame = createMenuButton("CUSTOM GAME", 0, 1);
        customGame.setOnAction(e -> {
            System.out.println("User chose CUSTOM GAME");
            window.setScene(customScene);
        });

        Button loadGame = createMenuButton("LOAD GAME", 1, 0);
        loadGame.setOnAction(e -> {
            System.out.println("User chose LOAD GAME");
            window.setScene(loadScene);
        });

        Button about = createMenuButton("USER MANUAL", 1, 1);
        about.setOnAction(e -> {
            System.out.println("User chose USER MANUAL");
            window.setScene(userManualScene);
        });

        GridPane grid = createGrid();
        grid.getChildren().addAll(normalGame, customGame, loadGame, about);

        BorderPane root = new BorderPane(grid);
        Label heading = createHeading();
        HBox top = new HBox(heading);
        top.setAlignment(Pos.BOTTOM_CENTER);
        root.setTop(top);

        StackPane background = createBackgroundPane(root);
        return new Scene(background, 1280, 720);
    }

    private static GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10,10,10,10));
        grid.setVgap(10);
        grid.setHgap(10);
        return grid;
    }

    private static Label createHeading() {
        Label heading = new Label("KING'S GAMBIT");
        heading.setTextFill(Color.WHITE);
        heading.setFont(Font.font("Times New Roman", FontWeight.BOLD, 80));
        heading.setAlignment(Pos.CENTER);
        heading.setPadding(new Insets(50, 0, 0, 0));
        return heading;
    }

    private static Button createMenuButton(String text, int column, int row) {
        Button button = new Button(text);
        button.setPrefSize(200, 80);
        button.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
        GridPane.setConstraints(button, column, row);
        return button;
    }

    private static StackPane createBackgroundPane(BorderPane layout) {
        ImageView backgroundImage = new ImageView(new Image("start_background.jpg"));
        backgroundImage.setFitHeight(720);
        backgroundImage.setFitWidth(1280);

        StackPane background = new StackPane(backgroundImage, layout);
        return background;
    }
}
