package com.otaviogustavo.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreatePlaylistController {

    @FXML private TextField txtTitle;
    @FXML private TextArea txtDescription;
    @FXML private Button btnCancel;
    @FXML private Button btnCreate;

    private boolean created = false;
    private String title;
    private String description;

    @FXML
    private void handleCancel() {
        closeStage();
    }

    @FXML
    private void handleCreate() {
        if (txtTitle.getText() == null || txtTitle.getText().trim().isEmpty()) {
            // Poderia adicionar uma validação visual aqui
            return;
        }
        
        this.title = txtTitle.getText();
        this.description = txtDescription.getText();
        this.created = true;
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public boolean isCreated() {
        return created;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
