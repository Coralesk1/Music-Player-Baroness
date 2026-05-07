package com.otaviogustavo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("main"), 1000, 600);
        loadStyle("main");
        stage.setTitle("Baroness Player");
        stage.getIcons().add(
                new Image(getClass().getResourceAsStream("/com/otaviogustavo/images/icons/baroness.png")));
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        loadStyle(fxml);
    }

    private static void loadStyle(String fxml) {
        URL styleSheet = App.class.getResource("css/" + fxml + ".css");

        if (styleSheet != null) {
            scene.getStylesheets().add(styleSheet.toExternalForm());
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        URL fxmlLocation = App.class.getResource("views/" + fxml + ".fxml");
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found: views/" + fxml + ".fxml");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}