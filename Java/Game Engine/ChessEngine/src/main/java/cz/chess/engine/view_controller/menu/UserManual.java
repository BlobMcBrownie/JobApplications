package cz.chess.engine.view_controller.menu;

import cz.chess.engine.SystemInfo;
import cz.chess.engine.view_controller.GraphicsUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Scene for reading the manual and the rules of chess
 *
 * @author Vojtěch Sýkora
 */
public class UserManual {

    private static final String manualURL = "https://www.notion.so/The-User-Manual-b7131e720c7646429c626728468c4df1";
    private static WebView browser;

    /**
     * Creates the UserManual Scene and loads the website manual
     *
     * @return UserManual Scene
     */
    public static Scene create() {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #2F3437;");

        final Label label = createInfo();
        loadManualPage();

        VBox vBox = new VBox(label, browser);
        vBox.setAlignment(Pos.CENTER);
        layout.setCenter(vBox);

        Button backButton = GraphicsUtils.createBackButton();
        Button manualLink = createManualLinkButton();

        HBox top = new HBox();
        top.setAlignment(Pos.BOTTOM_RIGHT);
        top.setSpacing(430.0);
        top.getChildren().addAll(manualLink, backButton);
        layout.setTop(top);

        return new Scene(layout, 1280, 720);
    }

    private static Button createManualLinkButton() {
        Button button = new Button("Click here to copy the link to the manual");
        button.setPrefSize(250,30);
        button.setOnAction(e -> {
            final Clipboard cb = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(manualURL);
            cb.setContent(content);
        });

        return button;
    }

    private static void loadManualPage() {
        browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
        webEngine.load(manualURL);
    }

    private static Label createInfo() {
        String javaVersion = SystemInfo.javaVersion();
        String javafxVersion = SystemInfo.javafxVersion();
        Label label = new Label("Author ... Vojtěch Sýkora\t"
                + "javaVersion ... " + javaVersion
                + "\tjavaFXversion ... " + javafxVersion);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Times New Roman", FontWeight.BOLD, 13));
        return label;
    }
}
