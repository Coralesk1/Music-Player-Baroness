package com.otaviogustavo.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import java.io.FileReader;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;


public class MainLibraryController {

    private MainController mainController;
    private GerenciadorEstruturas gerenciadorEstruturas;
    private Set<Musica> musicasPendentesExclusao = new HashSet<>();

    public void definirGerenciador(GerenciadorEstruturas gerenciadorEstruturas) {
        this.gerenciadorEstruturas = gerenciadorEstruturas;
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

        // Adiciona um listener para o campo de busca que usa a lógica da estrutura
        txtBusca.textProperty().addListener((observable, oldValue, newValue) -> {
            atualizarListaComBuscaEOrdenacao();
        });

        // Configura o ComboBox de ordenação
        if (comboOrdenacao != null) {
            comboOrdenacao.setItems(FXCollections.observableArrayList(
                    "Ordem padrão",
                    "Ordenar por nome",
                    "Ordenar por artista"
            ));
            comboOrdenacao.setValue("Ordem padrão");
            comboOrdenacao.setOnAction(event -> atualizarListaComBuscaEOrdenacao());
        }

        // Define a lista na tabela
        tabelaMusicas.setItems(listaMusicas);

        tabelaMusicas.getSelectionModel().selectedItemProperty().addListener((obs, antigoValor, novoValor) -> {
            if (novoValor != null) {
                // 'novoValor' é o objeto Musica da linha clicada
                System.out.println("--- Música Selecionada ---");
                System.out.println("Título: " + novoValor.getTitulo());
                System.out.println("Artista: " + novoValor.getArtista());
                System.out.println("Álbum: " + novoValor.getAlbum());
                System.out.println("Duração: " + novoValor.getDuracao());

            }
        });
    }

    private void atualizarListaComBuscaEOrdenacao() {
        if (gerenciadorEstruturas == null) return;

        // 1. Busca na estrutura
        String termo = txtBusca.getText();
        List<Musica> resultado = gerenciadorEstruturas.buscarMusicaNaBiblioteca(termo);

        // 2. Aplica a ordenação se necessário
        String opcao = comboOrdenacao.getValue();
        if (opcao != null && !opcao.equals("Ordem padrão")) {
            gerenciadorEstruturas.ordenarLista(resultado, opcao);
        }

        // 3. Atualiza a lista observável
        listaMusicas.setAll(resultado);
    }

    @FXML
    public void abrirJanelaArquivos(ActionEvent actionEvent) {

        DirectoryChooser directoryChooser = new DirectoryChooser();

        directoryChooser.setTitle("Selecionar Pasta");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // 3. Obtém a "Stage" (janela) atual para que a nova janela seja aberta sobre ela
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        // 4. Abre a janela e captura a pasta selecionada
        File selectedDirectory = directoryChooser.showDialog(stage);

        // 5. Verifica se o usuário selecionou algo ou cancelou
        if (selectedDirectory != null) {
            System.out.println("Pasta selecionada: " + selectedDirectory.getAbsolutePath());

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
                atualizarTabelaBiblioteca();
                salvarDadosNoJson();
            }

        } else {
            System.out.println("Seleção cancelada.");
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
            // Carrega o arquivo MP3
            Mp3File mp3File = new Mp3File(arquivoMp3);

            // Calcula a duração em minutos e segundos
            long segundosTotais = mp3File.getLengthInSeconds();
            long minutos = segundosTotais / 60;
            long segundos = segundosTotais % 60;
            duracao = String.format("%02d:%02d", minutos, segundos);

            // Tenta ler tags ID3v2 (mais modernas e detalhadas)
            if (mp3File.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3File.getId3v2Tag();

                if (id3v2Tag.getTitle() != null && !id3v2Tag.getTitle().isBlank()) {
                    titulo = id3v2Tag.getTitle();
                }
                if (id3v2Tag.getArtist() != null && !id3v2Tag.getArtist().isBlank()) {
                    artista = id3v2Tag.getArtist();
                }
                if (id3v2Tag.getAlbum() != null && !id3v2Tag.getAlbum().isBlank()) {
                    album = id3v2Tag.getAlbum();
                }
                if (id3v2Tag.getGenreDescription() != null && !id3v2Tag.getGenreDescription().isBlank()) {
                    genero = id3v2Tag.getGenreDescription();
                }
            }

            // Fallback para ID3v1: Se campos importantes ainda forem os padrões, tenta buscar na tag antiga
            if (mp3File.hasId3v1Tag()) {
                var id3v1Tag = mp3File.getId3v1Tag();

                if (titulo.equals(arquivoMp3.getName()) && id3v1Tag.getTitle() != null && !id3v1Tag.getTitle().isBlank()) {
                    titulo = id3v1Tag.getTitle();
                }
                if (artista.equals("Artista Desconhecido") && id3v1Tag.getArtist() != null && !id3v1Tag.getArtist().isBlank()) {
                    artista = id3v1Tag.getArtist();
                }
                if (album.equals("Álbum Desconhecido") && id3v1Tag.getAlbum() != null && !id3v1Tag.getAlbum().isBlank()) {
                    album = id3v1Tag.getAlbum();
                }
                if (genero.equals("Gênero Desconhecido") && id3v1Tag.getGenreDescription() != null && !id3v1Tag.getGenreDescription().isBlank()) {
                    genero = id3v1Tag.getGenreDescription();
                }
            }

        } catch (Exception e) {
            System.err.println("Erro ao ler metadados do arquivo: " + arquivoMp3.getName());
            e.printStackTrace();
        }

        return new Musica(titulo, artista, album, genero, duracao, caminho, capa);
    }

    private void atualizarTabelaBiblioteca() {
        if (gerenciadorEstruturas != null) {
            listaMusicas.setAll(gerenciadorEstruturas.getBibliotecaGeral());
        }
    }

    // metodo para criar um botão de play para cada linha da tabela de musica da biblioteca
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

    // método para criar um botão de adicionar à playlist para cada linha da tabela de música da biblioteca
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

            // Passa as playlists existentes do gerenciador de estruturas
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
            System.err.println("Erro ao abrir diálogo de seleção de playlist: " + e.getMessage());
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

            // Se a música está tocando e não está na lista de pendentes, adiciona
            if (!musicasPendentesExclusao.contains(musica)) {
                musicasPendentesExclusao.add(musica);

                ChangeListener<Musica> listener = new ChangeListener<Musica>() {
                    @Override
                    public void changed(ObservableValue<? extends Musica> observable, Musica oldMusic, Musica newMusic) {
                        // Quando mudar a música, removemos efetivamente a que estava pendente
                        if (oldMusic != null && oldMusic.equals(musica) && !musica.equals(newMusic)) {
                            removerMusicaEfetivamente(musica);
                            musicasPendentesExclusao.remove(musica);
                            mainController.musicaAtualProperty().removeListener(this);
                        }
                    }
                };
                mainController.musicaAtualProperty().addListener(listener);
                System.out.println("Exclusão adiada. A música será removida quando a reprodução mudar ou terminar: " + musica.getTitulo());
            }
        } else {
            // Se não for a música atual, remove imediatamente
            removerMusicaEfetivamente(musica);
        }
    }

    private void removerMusicaEfetivamente(Musica musica) {
        if (gerenciadorEstruturas != null) {
            gerenciadorEstruturas.removerMusicaBiblioteca(musica);
            listaMusicas.remove(musica);
            salvarDadosNoJson();
        }
    }

    private void salvarDadosNoJson() {
        if (mainController != null) {
            mainController.salvarDadosNoJson();
        } else {
            // Fallback caso o mainController não esteja setado
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
