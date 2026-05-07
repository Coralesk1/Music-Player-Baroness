package com.otaviogustavo.controllers;

import java.io.IOException;
import java.net.URL;

import com.otaviogustavo.App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class MainController {

    @FXML
    private VBox contentArea;

    @FXML
    private ToggleButton btnHome;

    @FXML
    private ToggleButton btnLibrary;

    @FXML
    private ToggleButton btnPlay;

    @FXML
    private FontIcon iconPlay;

    @FXML
    private void handleMenuAction(ActionEvent event) {
        ToggleButton selectedButton = (ToggleButton) event.getSource();
        String fxml = "";

        if (selectedButton == btnHome) {
            fxml = "main_home";
        } else if (selectedButton == btnLibrary) {
            fxml = "main_library";
        }

        if (!fxml.isEmpty()) {
            loadView(fxml);
        }
    }

    @FXML
    private void handlePlayAction(ActionEvent event) {
        if (btnPlay.isSelected()) {
            iconPlay.setIconLiteral("ion4-ios-pause");
        } else {
            iconPlay.setIconLiteral("ion4-ios-play");
        }
    }

    private void loadView(String fxml) {
        try {
            URL fxmlLocation = App.class.getResource("/com/otaviogustavo/views/" + fxml + ".fxml");
            if (fxmlLocation == null) {
                System.err.println("FXML file not found: /com/otaviogustavo/views/" + fxml + ".fxml");
                return;
            }
            Parent root = FXMLLoader.load(fxmlLocation);
            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        loadView("main_home");
    }
}
