package com.otaviogustavo;

import com.otaviogustavo.controllers.MainController;
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

    private static final GerenciadorEstruturas gerenciadorEstruturas = new GerenciadorEstruturas();

    @Override
    public void start(Stage stage) throws IOException {

        Parent root = loadFXMLAndInject("main");

        scene = new Scene(root, 1000, 600);
        loadStyle("main");
        stage.setTitle("Baroness Player");
        stage.getIcons().add(
                new Image(getClass().getResourceAsStream("/com/otaviogustavo/images/icons/baroness.png")));
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        // Se no futuro você trocar de tela usando setRoot, ele também injeta o gerenciador
        scene.setRoot(loadFXMLAndInject(fxml));
        loadStyle(fxml);
    }

    private static void loadStyle(String fxml) {
        URL styleSheet = App.class.getResource("css/" + fxml + ".css");

        if (styleSheet != null) {
            scene.getStylesheets().add(styleSheet.toExternalForm());
        }
    }

    // deixei caso precise carregar algo sem precisar injetar o GerenciadorEstruturas
    private static Parent loadFXML(String fxml) throws IOException {
        URL fxmlLocation = App.class.getResource("views/" + fxml + ".fxml");
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found: views/" + fxml + ".fxml");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        return fxmlLoader.load();
    }

    //Carrega o FXML, descobre quem é o controller e injeta o gerenciador
    private static Parent loadFXMLAndInject(String fxml) throws IOException {
        URL fxmlLocation = App.class.getResource("views/" + fxml + ".fxml");
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found: views/" + fxml + ".fxml");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Parent root = fxmlLoader.load();

        Object controller = fxmlLoader.getController();

        // 1. ENTREGA O GERENCIADOR PARA O MAIN CONTROLLER!
        if (controller instanceof MainController) {
            ((MainController) controller).setGerenciador(gerenciadorEstruturas);
        }

        return root;
    }

    public static void main(String[] args) {
        launch();
    }
}