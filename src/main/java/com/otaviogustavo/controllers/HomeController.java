package com.otaviogustavo.controllers;

import java.io.IOException;

import com.otaviogustavo.App;

import javafx.fxml.FXML;

public class HomeController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
