package com.otaviogustavo.controllers;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.otaviogustavo.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class MainLibraryController {

    private MainController mainController;
    private GerenciadorEstruturas gerenciadorEstruturas;
    private Set<Musica> musicasPendentesExclusao = new HashSet<>();

    public void definirGerenciador(GerenciadorEstruturas gerenciadorEstruturas) {
        this.gerenciadorEstruturas = gerenciadorEstruturas;
        popularFiltros();
        atualizarTabelaBiblioteca();
    }

    public void definirMainController(MainController mainController) {
        this.mainController = mainController;
    }


    @FXML
    private Button btnOpenFolder;

    @FXML
    private TextField txtBusca;

    @FXML
    private ComboBox<String> comboGenre;
    @FXML
    private ComboBox<String> comboArtist;
    @FXML
    private ComboBox<String> comboAlbum;

    @FXML
    private ComboBox<String> comboOrdenacao;

    @FXML
    private TableView<Musica> tabelaMusicas;
    @FXML
    private TableColumn<Musica, Void> colAdd;
    @FXML
    private TableColumn<Musica, Void> colPlay;
    @FXML
    private TableColumn<Musica, String> colTitulo;
    @FXML
    private TableColumn<Musica, String> colArtista;
    @FXML
    private TableColumn<Musica, String> colAlbum;
    @FXML
    private TableColumn<Musica, String> colDuracao;
    @FXML
    private TableColumn<Musica, Void> colExcluir;

    private ObservableList<Musica> listaMusicas = FXCollections.observableArrayList();

    public ObservableList<Musica> getListaMusicas() {
        return listaMusicas;
    }

    @FXML
    public void initialize() {

        // Configura como cada coluna vai buscar o dado no objeto Musica
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colArtista.setCellValueFactory(new PropertyValueFactory<>("artista"));
        colAlbum.setCellValueFactory(new PropertyValueFactory<>("album"));
        colDuracao.setCellValueFactory(new PropertyValueFactory<>("duracao"));

        configurarColunaAdd();
        configurarColunaPlay();
        configurarColunaExcluir();

        // Adiciona listeners para todos os campos de busca e filtros
        txtBusca.textProperty().addListener((observable, oldValue, newValue) -> atualizarListaComFiltros());
        
        if (comboGenre != null) comboGenre.setOnAction(event -> atualizarListaComFiltros());
        if (comboArtist != null) comboArtist.setOnAction(event -> atualizarListaComFiltros());
        if (comboAlbum != null) comboAlbum.setOnAction(event -> atualizarListaComFiltros());

        // Configura o ComboBox de ordenação
        if (comboOrdenacao != null) {
            comboOrdenacao.setItems(FXCollections.observableArrayList(
                    "Ordem padrão",
                    "Ordenar por nome",
                    "Ordenar por artista"
            ));
            comboOrdenacao.setValue("Ordem padrão");
            comboOrdenacao.setOnAction(event -> atualizarListaComFiltros());
        }

        // Define a lista na tabela
        tabelaMusicas.setItems(listaMusicas);

        tabelaMusicas.getSelectionModel().selectedItemProperty().addListener((obs, antigoValor, novoValor) -> {
            if (novoValor != null) {
                System.out.println("--- Música Selecionada ---");
                System.out.println("Título: " + novoValor.getTitulo());
                System.out.println("Artista: " + novoValor.getArtista());
            }
        });
    }

    private void popularFiltros() {
        if (gerenciadorEstruturas == null || gerenciadorEstruturas.getBibliotecaGeral() == null) return;

        Set<Musica> biblioteca = gerenciadorEstruturas.getBibliotecaGeral();

        Set<String> generos = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        Set<String> artistas = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        Set<String> albuns = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        generos.add("Todos os gêneros");
        artistas.add("Todos os artistas");
        albuns.add("Todos os álbuns");

        for (Musica m : biblioteca) {
            if (m.getGenero() != null && !m.getGenero().isBlank()) generos.add(m.getGenero());
            if (m.getArtista() != null && !m.getArtista().isBlank()) artistas.add(m.getArtista());
            if (m.getAlbum() != null && !m.getAlbum().isBlank()) albuns.add(m.getAlbum());
        }

        comboGenre.setItems(FXCollections.observableArrayList(new ArrayList<>(generos)));
        comboArtist.setItems(FXCollections.observableArrayList(new ArrayList<>(artistas)));
        comboAlbum.setItems(FXCollections.observableArrayList(new ArrayList<>(albuns)));

        comboGenre.setValue("Todos os gêneros");
        comboArtist.setValue("Todos os artistas");
        comboAlbum.setValue("Todos os álbuns");
    }

    private void atualizarListaComFiltros() {
        if (gerenciadorEstruturas == null) return;

        // 1. Coleta os valores dos filtros
        String termo = txtBusca.getText();
        String genero = (comboGenre != null) ? comboGenre.getValue() : null;
        String artista = (comboArtist != null) ? comboArtist.getValue() : null;
        String album = (comboAlbum != null) ? comboAlbum.getValue() : null;

        List<Musica> resultado = gerenciadorEstruturas.buscarMusicaNaBiblioteca(termo, genero, artista, album);

        // 3. Aplica a ordenação
        String opcao = comboOrdenacao.getValue();
        if (opcao != null && !opcao.equals("Ordem padrão")) {
            gerenciadorEstruturas.ordenarLista(resultado, opcao);
        }

        // 4. Atualiza a lista observável da tabela
        listaMusicas.setAll(resultado);
    }

    @FXML
    public void abrirJanelaArquivos(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Selecionar Pasta");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            File[] pastaArquivos = selectedDirectory.listFiles();
            if (pastaArquivos != null) {
                for (File file : pastaArquivos) {
                    if (file.isFile() && (file.getName().endsWith(".mp3") || file.getName().endsWith(".wav"))) {
                        Musica musica = lerMetadados(file);
                        if (musica != null) {
                            gerenciadorEstruturas.adicionarMusicaBiblioteca(musica);
                        }
                    }
                }
                popularFiltros(); // Atualiza os filtros com novos gêneros/artistas/albuns
                atualizarTabelaBiblioteca();
                salvarDadosNoJson();
            }
        }
    }

    public Musica lerMetadados(File arquivoMp3) {
        String titulo = arquivoMp3.getName();
        String artista = "Artista Desconhecido";
        String album = "Álbum Desconhecido";
        String genero = "Gênero Desconhecido";
        String duracao = "00:00";
        String caminho = arquivoMp3.getAbsolutePath();
        byte[] capa = null;

        try {
            Mp3File mp3File = new Mp3File(arquivoMp3);
            long segundosTotais = mp3File.getLengthInSeconds();
            long minutos = segundosTotais / 60;
            long segundos = segundosTotais % 60;
            duracao = String.format("%02d:%02d", minutos, segundos);

            if (mp3File.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                if (id3v2Tag.getTitle() != null && !id3v2Tag.getTitle().isBlank()) titulo = id3v2Tag.getTitle();
                if (id3v2Tag.getArtist() != null && !id3v2Tag.getArtist().isBlank()) artista = id3v2Tag.getArtist();
                if (id3v2Tag.getAlbum() != null && !id3v2Tag.getAlbum().isBlank()) album = id3v2Tag.getAlbum();
                if (id3v2Tag.getGenreDescription() != null && !id3v2Tag.getGenreDescription().isBlank()) genero = id3v2Tag.getGenreDescription();
            }
        } catch (Exception e) {
            System.err.println("Erro ao ler metadados: " + arquivoMp3.getName());
        }

        return new Musica(titulo, artista, album, genero, duracao, caminho, capa);
    }

    private void atualizarTabelaBiblioteca() {
        if (gerenciadorEstruturas != null) {
            atualizarListaComFiltros();
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
                            if (getTableRow() != null && getTableRow().getItem() != null) atualizarIcone(getTableRow().getItem());
                        });
                        mainController.musicaAtualProperty().addListener((obs, antiga, nova) -> {
                            if (getTableRow() != null && getTableRow().getItem() != null) atualizarIcone(getTableRow().getItem());
                        });
                    }
                }
            }
            private void atualizarIcone(Musica musicaDaLinha) {
                if (mainController.musicaAtualProperty().get() != null &&
                        mainController.musicaAtualProperty().get().equals(musicaDaLinha) &&
                        mainController.tocandoProperty().get() &&
                        "BIBLIOTECA".equals(mainController.getContextoAtivo())) {
                    iconPlay.setIconLiteral("ion4-ios-pause");
                } else {
                    iconPlay.setIconLiteral("ion4-ios-play");
                }
            }
        });
    }

    public void tocarMusica(Musica musica) {
        if (mainController != null) {
            mainController.tocarMusica(musica, new java.util.ArrayList<>(listaMusicas), "BIBLIOTECA");
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
            stage.initOwner(tabelaMusicas.getScene().getWindow());
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
            e.printStackTrace();
        }
    }

    public void adicionarMusicaEstruturaEJson(PlayList playlist, Musica musica) {
        if (gerenciadorEstruturas != null) {
            gerenciadorEstruturas.adicionarMusicaPLaylist(playlist, musica);
            salvarDadosNoJson();
        }
    }

    private void configurarColunaExcluir() {
        if (colExcluir == null) return;
        colExcluir.setCellFactory(column -> new TableCell<>() {
            private final Button btnExcluir = new Button();
            private final FontIcon iconeLixeira = new FontIcon("ion4-ios-trash");
            {
                btnExcluir.getStyleClass().add("button-delete-playlist");
                iconeLixeira.setIconSize(20);
                btnExcluir.setGraphic(iconeLixeira);
                btnExcluir.setOnAction(event -> {
                    Musica musica = getTableView().getItems().get(getIndex());
                    excluirMusicaDaBiblioteca(musica);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(btnExcluir);
                }
            }
        });
    }

    private void excluirMusicaDaBiblioteca(Musica musica) {
        if (mainController != null && mainController.musicaAtualProperty().get() != null &&
                mainController.musicaAtualProperty().get().equals(musica)) {
            if (!musicasPendentesExclusao.contains(musica)) {
                musicasPendentesExclusao.add(musica);
                ChangeListener<Musica> listener = new ChangeListener<Musica>() {
                    @Override
                    public void changed(ObservableValue<? extends Musica> observable, Musica oldMusic, Musica newMusic) {
                        if (oldMusic != null && oldMusic.equals(musica) && !musica.equals(newMusic)) {
                            removerMusicaEfetivamente(musica);
                            musicasPendentesExclusao.remove(musica);
                            mainController.musicaAtualProperty().removeListener(this);
                        }
                    }
                };
                mainController.musicaAtualProperty().addListener(listener);
            }
        } else {
            removerMusicaEfetivamente(musica);
        }
    }

    private void removerMusicaEfetivamente(Musica musica) {
        if (gerenciadorEstruturas != null) {
            gerenciadorEstruturas.removerMusicaBiblioteca(musica);
            popularFiltros(); // atualiza os combo de filtro apos excliur uma musica .
            atualizarListaComFiltros();
            if (mainController != null) {
                mainController.removerDaFilaEContexto(musica);
            }
            salvarDadosNoJson();
        }
    }

    private void salvarDadosNoJson() {
        if (mainController != null) {
            mainController.salvarDadosNoJson();
        } else {
            try {
                Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
                try (FileWriter writer = new FileWriter("BibliotecaEPlaylists.json")) {
                    gson.toJson(gerenciadorEstruturas, writer);
                }
            } catch (IOException e) {
                System.err.println("Erro ao salvar dados no json: " + e.getMessage());
            }
        }
    }
}
