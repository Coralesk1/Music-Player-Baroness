package com.otaviogustavo.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.otaviogustavo.App;
import com.otaviogustavo.GerenciadorEstruturas;
import com.otaviogustavo.Musica;
import com.otaviogustavo.PlayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MainHomeController {

    private MainController mainController;
    private GerenciadorEstruturas gerenciadorEstruturas;

    @FXML private TableView<Musica> tabelaHistorico;
    @FXML private TableColumn<Musica, Void> colPlay;
    @FXML private TableColumn<Musica, String> colTitulo;
    @FXML private TableColumn<Musica, String> colArtista;
    @FXML private TableColumn<Musica, String> colAlbum;
    @FXML private TableColumn<Musica, String> colDuracao;
    @FXML private TableColumn<Musica, Void> colAdd;

    private ObservableList<Musica> listaHistorico = FXCollections.observableArrayList();

    public void definirGerenciador(GerenciadorEstruturas gerenciadorEstruturas) {
        this.gerenciadorEstruturas = gerenciadorEstruturas;
        atualizarTabelaHistorico();
    }

    public void definirMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colArtista.setCellValueFactory(new PropertyValueFactory<>("artista"));
        colAlbum.setCellValueFactory(new PropertyValueFactory<>("album"));
        colDuracao.setCellValueFactory(new PropertyValueFactory<>("duracao"));

        configurarColunaPlay();
        configurarColunaAdd();

        tabelaHistorico.setItems(listaHistorico);
    }

    public void atualizarTabelaHistorico() {
        if (gerenciadorEstruturas != null && gerenciadorEstruturas.getHistoricoReproducao() != null) {
            List<Musica> historicoInvertido = new ArrayList<>(gerenciadorEstruturas.getHistoricoReproducao());
            Collections.reverse(historicoInvertido);
            listaHistorico.setAll(historicoInvertido);
        }
    }

    private void configurarColunaPlay() {
        colPlay.setCellFactory(column -> new TableCell<>() {
            private final Button btnPlayLocal = new Button();
            private final FontIcon iconPlay = new FontIcon("ion4-ios-play");

            {
                btnPlayLocal.getStyleClass().add("button-player");
                iconPlay.setIconSize(24);
                btnPlayLocal.setGraphic(iconPlay);
                btnPlayLocal.setOnAction(event -> {
                    Musica musica = getTableView().getItems().get(getIndex());
                    tocarMusica(musica);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Musica musicaDaLinha = getTableRow().getItem();
                    setGraphic(btnPlayLocal);

                    if (mainController != null) {
                        atualizarIcone(musicaDaLinha);

                        mainController.tocandoProperty().addListener((obs, antigo, tocando) -> {
                            if (getTableRow() != null && getTableRow().getItem() != null) {
                                atualizarIcone(getTableRow().getItem());
                            }
                        });
                        mainController.musicaAtualProperty().addListener((obs, antiga, nova) -> {
                            if (getTableRow() != null && getTableRow().getItem() != null) {
                                atualizarIcone(getTableRow().getItem());
                            }
                        });
                    }
                }
            }

            private void atualizarIcone(Musica musicaDaLinha) {
                if (mainController.musicaAtualProperty().get() != null &&
                        mainController.musicaAtualProperty().get().equals(musicaDaLinha) &&
                        mainController.tocandoProperty().get() &&
                        "HISTORICO".equals(mainController.getContextoAtivo())) {
                    iconPlay.setIconLiteral("ion4-ios-pause");
                } else {
                    iconPlay.setIconLiteral("ion4-ios-play");
                }
            }
        });
    }

    public void tocarMusica(Musica musica) {
        if (mainController != null) {
            mainController.tocarMusica(musica, new ArrayList<>(listaHistorico), "HISTORICO");
        }
    }

    private void configurarColunaAdd() {
        colAdd.setCellFactory(column -> new TableCell<>() {
            private final Button btnAddLocal = new Button();
            private final FontIcon iconAdd = new FontIcon("ion4-ios-add");

            {
                btnAddLocal.getStyleClass().add("button-player");
                iconAdd.setIconSize(24);
                btnAddLocal.setGraphic(iconAdd);
                btnAddLocal.setOnAction(event -> {
                    Musica musica = getTableView().getItems().get(getIndex());
                    abrirDialogoAdicionarPlaylist(musica);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(btnAddLocal);
                }
            }
        });
    }

    private void abrirDialogoAdicionarPlaylist(Musica musica) {
        try {
            URL fxmlLocation = App.class.getResource("/com/otaviogustavo/views/select_playlist_dialog.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            SelectPlaylistController controller = loader.getController();

            List<PlayList> listaPlaylists = new ArrayList<>();
            if (gerenciadorEstruturas != null && gerenciadorEstruturas.getPlaylists() != null) {
                listaPlaylists.addAll(gerenciadorEstruturas.getPlaylists().keySet());
            }
            controller.setPlaylists(listaPlaylists);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initOwner(tabelaHistorico.getScene().getWindow());

            Scene scene = new Scene(root);
            scene.getStylesheets().add(App.class.getResource("/com/otaviogustavo/css/main.css").toExternalForm());
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            stage.showAndWait();

            if (controller.isConfirmed()) {
                PlayList playlistSelecionada = controller.getSelectedPlaylist();
                if (playlistSelecionada != null) {
                    adicionarMusicaEstruturaEJson(playlistSelecionada, musica);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao abrir diálogo de seleção de playlist: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void adicionarMusicaEstruturaEJson(PlayList playlist, Musica musica) {
        try {
            gerenciadorEstruturas.adicionarMusicaPLaylist(playlist, musica);
            Map<PlayList, List<Musica>> playlistMusica =  gerenciadorEstruturas.getPlaylists();

            if (playlistMusica != null){
                Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
                try (FileWriter writer = new FileWriter("PlayLists.json")) {
                    gson.toJson(gerenciadorEstruturas, writer);
                    System.out.println("Playlist com musica salva com sucesso em PlayLists.json!");
                } catch (IOException e) {
                    System.err.println("Erro ao salvar musica no json:" + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao salvar musica da plalist:" + e.getMessage());
            e.printStackTrace();
        }
    }
}