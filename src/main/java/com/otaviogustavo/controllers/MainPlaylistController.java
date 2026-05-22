package com.otaviogustavo.controllers;

import com.otaviogustavo.GerenciadorEstruturas;
import com.otaviogustavo.Musica;
import com.otaviogustavo.PlayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

public class MainPlaylistController {

    private MainController mainController;
    private GerenciadorEstruturas gerenciadorEstruturas;

    // --- Campos do Create Playlist Dialog ---
    private String titulo;
    private String descricao;
    private Boolean estaCriado = false;

    @FXML private TextField txtTitle;
    @FXML private TextArea txtDescription;
    @FXML private Button btnCancel;
    @FXML private Button btnCreate;

    // --- Campos do Playlist View ---
    private PlayList playlist;

    @FXML private Label lblPlaylistNome;
    @FXML private Label lblPlaylistDescricao;
    @FXML private Label lblPlaylistData;

    @FXML private TableView<Musica> tabelaMusicas;
    @FXML private TableColumn<Musica, Void> colPlay;
    @FXML private TableColumn<Musica, String> colTitulo;
    @FXML private TableColumn<Musica, String> colArtista;
    @FXML private TableColumn<Musica, String> colDuracao;

    private ObservableList<Musica> listaMusicas = FXCollections.observableArrayList();

    public void definirGerenciador(GerenciadorEstruturas gerenciadorEstruturas) {
        this.gerenciadorEstruturas = gerenciadorEstruturas;
    }

    public void definirMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        if (tabelaMusicas != null) {
            // Configura as colunas para buscar as propriedades do objeto Musica
            colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
            colArtista.setCellValueFactory(new PropertyValueFactory<>("artista"));
            colDuracao.setCellValueFactory(new PropertyValueFactory<>("duracao"));

            // Configura a coluna que contém o botão de reproduzir
            configurarColunaTocar();

            tabelaMusicas.setItems(listaMusicas);
        }
    }

    @FXML
    private void lidarComCancelar() {
        fecharJanela();
    }

    @FXML
    private void lidarComCriar() {
        if (txtTitle.getText() == null || txtTitle.getText().trim().isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);

            alerta.setTitle("Campo Obrigatório");
            alerta.setHeaderText("Título Inválido");
            alerta.setContentText("Por favor, preencha o campo de título antes de continuar.");

            alerta.showAndWait();

            return;
        }

        titulo = txtTitle.getText().trim();
        descricao = txtDescription.getText().trim();
        estaCriado = true;

        fecharJanela();
    }

    private void fecharJanela() {
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

    public Boolean estaCriado() {
        return estaCriado;
    }

    public void setEstaCriado(Boolean estaCriado) {
        this.estaCriado = estaCriado;
    }

    public void definirPlaylist(PlayList playlist, GerenciadorEstruturas gerenciador, MainController mainController) {
        this.playlist = playlist;
        this.gerenciadorEstruturas = gerenciador;
        this.mainController = mainController;

        if (playlist != null) {
            if (lblPlaylistNome != null) lblPlaylistNome.setText(playlist.getNome());
            if (lblPlaylistDescricao != null) lblPlaylistDescricao.setText(playlist.getDescricao());
            if (lblPlaylistData != null) lblPlaylistData.setText("Criada em: " + playlist.getDtCriacao());

            carregarMusicas();
        }
    }

    private void carregarMusicas() {
        if (gerenciadorEstruturas != null && playlist != null) {
            List<Musica> musicas = gerenciadorEstruturas.getPlaylists().get(playlist);
            if (musicas != null) {
                listaMusicas.setAll(musicas);
            } else {
                listaMusicas.clear();
            }
        }
    }

    private void configurarColunaTocar() {
        colPlay.setCellFactory(column -> new TableCell<>() {
            private final Button btnTocarLocal = new Button();
            private final FontIcon iconePlay = new FontIcon("ion4-ios-play");

            {
                btnTocarLocal.getStyleClass().add("button-player");
                iconePlay.setIconSize(24);
                btnTocarLocal.setGraphic(iconePlay);
                btnTocarLocal.setOnAction(event -> {
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
                    setGraphic(btnTocarLocal);

                    // Sincroniza o ícone baseado no estado do MainController
                    if (mainController != null) {
                        atualizarIcone(musicaDaLinha);

                        // Listeners para reagir a mudanças no player global
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
                        mainController.tocandoProperty().get()) {
                    iconePlay.setIconLiteral("ion4-ios-pause");
                } else {
                    iconePlay.setIconLiteral("ion4-ios-play");
                }
            }
        });
    }

    public void tocarMusica(Musica musica) {
        if (mainController != null) {
            mainController.tocarMusica(musica);
        }
    }
}
