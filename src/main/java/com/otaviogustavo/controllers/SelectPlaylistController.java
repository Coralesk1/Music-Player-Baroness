package com.otaviogustavo.controllers;

import com.otaviogustavo.PlayList;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.util.List;

public class SelectPlaylistController {

    @FXML private ListView<PlayList> lstPlaylists;
    @FXML private Button btnCancel;
    @FXML private Button btnSelect;

    private PlayList selectedPlaylist;
    private boolean confirmed = false;

    @FXML
    public void initialize() {
        lstPlaylists.getStyleClass().add("playlist-list-view");

        // Define como cada célula da lista deve ser renderizada (apenas mostrando o nome da playlist)
        lstPlaylists.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(PlayList item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNome());
                }
            }
        });

        // Habilita/Desabilita o botão Adicionar com base na seleção
        btnSelect.disableProperty().bind(lstPlaylists.getSelectionModel().selectedItemProperty().isNull());
    }

    public void setPlaylists(List<PlayList> playlists) {
        lstPlaylists.setItems(FXCollections.observableArrayList(playlists));
    }

    @FXML
    private void handleCancel() {
        closeStage();
    }

    @FXML
    private void handleSelect() {
        selectedPlaylist = lstPlaylists.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            confirmed = true;
            closeStage();
        }
    }

    private void closeStage() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public PlayList getSelectedPlaylist() {
        return selectedPlaylist;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
