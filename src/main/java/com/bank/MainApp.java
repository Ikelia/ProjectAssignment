package com.bank;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * JavaFX Application entry point.
 *
 * Loads main.fxml, applies the stylesheet, and shows the primary stage.
 * All backend initialisation (data loading) is handled by MainController.initialize().
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        URL fxmlUrl = getClass().getResource("/com/bank/main.fxml");
        if (fxmlUrl == null) {
            throw new IllegalStateException("Cannot find main.fxml — check resources path.");
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load(), 1100, 700);

        // Apply stylesheet
        URL cssUrl = getClass().getResource("/com/bank/styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        primaryStage.setTitle("National Bank");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
