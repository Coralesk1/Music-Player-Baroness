package com.otaviogustavo.controllers;

import com.otaviogustavo.GerenciadorEstruturas;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class MainPlaylistController {

    private MainController mainController;
    private GerenciadorEstruturas gerenciadorEstruturas;

    public void setGerenciador(GerenciadorEstruturas gerenciadorEstruturas) {
        this.gerenciadorEstruturas = gerenciadorEstruturas;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private String titulo;
    private String descricao;
    private Boolean isCriado;

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

        titulo = txtTitle.getText().trim();
        descricao = txtDescription.getText().trim();
        isCriado = true;

        closeStage();

    }

    private void closeStage() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean isCriado() {
        return isCriado;
    }

    public void setIsCriado(Boolean crirado) {
        isCriado = crirado;
    }
}
