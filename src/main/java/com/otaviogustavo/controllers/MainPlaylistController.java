package com.otaviogustavo.controllers;

import com.otaviogustavo.GerenciadorEstruturas;
import com.otaviogustavo.Musica;
import com.otaviogustavo.PlayList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class MainPlaylistController {

    private MainController mainController;
    private GerenciadorEstruturas gerenciadorEstruturas;

    public void setGerenciador(GerenciadorEstruturas gerenciadorEstruturas) {
        this.gerenciadorEstruturas = gerenciadorEstruturas;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML private TextField txtTitle;
    @FXML private TextArea txtDescription;
    @FXML private Button btnCancel;
    @FXML private Button btnCreate;

    @FXML
    private void handleCancel() {
        closeStage();
    }

    @FXML
    private void handleCreate() {
        if (txtTitle.getText() == null || txtTitle.getText().trim().isEmpty()) {
            //emitir um alerta
            return;
        }

        gerenciadorEstruturas.criaPlaylistVazia(new PlayList(String.valueOf(txtTitle), String.valueOf(txtDescription)));

        closeStage();

        Map<PlayList, List<Musica>> teste= gerenciadorEstruturas.getPlaylists();

    }

    private void closeStage() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

}
