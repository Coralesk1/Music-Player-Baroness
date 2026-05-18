package com.otaviogustavo.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.otaviogustavo.GerenciadorEstruturas;
import com.otaviogustavo.LibraryFilePaths;
import com.otaviogustavo.Musica;
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


public class MainLibraryController {

    private MainController mainController;
    private GerenciadorEstruturas gerenciadorEstruturas;

    public void setGerenciador(GerenciadorEstruturas gerenciadorEstruturas) {
        this.gerenciadorEstruturas = gerenciadorEstruturas;

        carregarListFileLibraryJson();
        atualizarTabela();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private Button btnOpenFolder;

    @FXML private TableView<Musica> tabelaMusicas;
    @FXML private TableColumn<Musica, Void> colPlay;
    @FXML private TableColumn<Musica, String> colTitulo;
    @FXML private TableColumn<Musica, String> colArtista;
    @FXML private TableColumn<Musica, String> colAlbum;
    @FXML private TableColumn<Musica, String> colDuracao;

    private ObservableList<Musica> listaMusicas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        // Configura como cada coluna vai buscar o dado no objeto Musica
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colArtista.setCellValueFactory(new PropertyValueFactory<>("artista"));
        colDuracao.setCellValueFactory(new PropertyValueFactory<>("duracao"));

        configurarColunaPlay();

        tabelaMusicas.setItems(listaMusicas);

        tabelaMusicas.getSelectionModel().selectedItemProperty().addListener((obs, antigoValor, novoValor) -> {
            if (novoValor != null) {
                // 'novoValor' é o objeto Musica da linha clicada
                System.out.println("--- Música Selecionada ---");
                System.out.println("Título: " + novoValor.getTitulo());
                System.out.println("Artista: " + novoValor.getArtista());
                System.out.println("Duração: " + novoValor.getDuracao());

            }
        });
    }

    @FXML
    public void abreJanelaArquivos(ActionEvent actionEvent){

        DirectoryChooser directoryChooser = new DirectoryChooser();
        List<String> listaCaminhos = new ArrayList<>();
        LibraryFilePaths libraryData = new LibraryFilePaths();


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
                    if (file.isFile() && (file.getName().endsWith(".mp3") || file.getName().endsWith(".wav"))){

                        Musica musica = lerMetadados(file);

                        if (musica != null){
                            gerenciadorEstruturas.adicionarMusicaBiblioteca(musica);
                            listaCaminhos.add(file.getAbsolutePath());
                        }
                    }
                }
                atualizarTabela();

                if (listaCaminhos != null){
                    libraryData.setListaCaminhos(listaCaminhos);

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();

                    // Grava o arquivo de configuração na raiz do projeto
                    try (FileWriter writer = new FileWriter("LibraryFilePath.json")) {
                        gson.toJson(libraryData, writer);
                        System.out.println("Biblioteca salva com sucesso em LibraryFilePath.json!");
                    } catch (IOException e) {
                        System.err.println("Erro ao salvar os caminhos no arquivo JSON:" + e.getMessage());
                        e.printStackTrace();
                    }

                }

            }

        } else {
            System.out.println("Seleção cancelada.");
        }

    }

    public Musica lerMetadados(File arquivoMp3) {

        String titulo = arquivoMp3.getName();
        String artista = "Artista Desconhecido";
        String duracao = "00:00";
        String caminho = arquivoMp3.getAbsolutePath();

        try {
            // Carrega o arquivo MP3
            Mp3File mp3File = new Mp3File(arquivoMp3);

            // Calcula a duração em minutos e segundos
            long segundosTotais = mp3File.getLengthInSeconds();
            long minutos = segundosTotais / 60;
            long segundos = segundosTotais % 60;
            duracao = String.format("%02d:%02d", minutos, segundos);

            // Verifica se o arquivo possui tags ID3v2 (as mais comuns e modernas)
            if (mp3File.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3File.getId3v2Tag();

                // Só substitui se o metadado não estiver vazio no arquivo
                if (id3v2Tag.getTitle() != null && !id3v2Tag.getTitle().isBlank()) {
                    titulo = id3v2Tag.getTitle();
                }
                if (id3v2Tag.getArtist() != null && !id3v2Tag.getArtist().isBlank()) {
                    artista = id3v2Tag.getArtist();
                }
            } else if (mp3File.hasId3v1Tag()) { // Se não tiver ID3v2, tenta ler o formato antigo ID3v1

                var id3v1Tag = mp3File.getId3v1Tag();
                if (id3v1Tag.getTitle() != null && !id3v1Tag.getTitle().isBlank()) titulo = id3v1Tag.getTitle();
                if (id3v1Tag.getArtist() != null && !id3v1Tag.getArtist().isBlank()) artista = id3v1Tag.getArtist();
            }

        } catch (Exception e) {
            System.err.println("Erro ao ler metadados do arquivo: " + arquivoMp3.getName());
            e.printStackTrace();
        }

        return new Musica(titulo, artista, duracao, caminho);
    }

    private void carregarListFileLibraryJson() {
        File arquivoJson = new File("LibraryFilePath.json");

        if (!arquivoJson.exists()) {
            return;
        }

        Gson gson = new Gson();

        try (FileReader reader = new FileReader(arquivoJson)) {

            // Passa os dados do json para o objeto LibraryData
            LibraryFilePaths dados = gson.fromJson(reader, LibraryFilePaths.class);

            if (dados != null && dados.getListaCaminhos() != null) {

                for (String caminho : dados.getListaCaminhos()) {
                    File arquivo = new File(caminho);

                    // verifica se o arquivo existe
                    if (arquivo.exists() && arquivo.isFile()) {
                        Musica musica = lerMetadados(arquivo);
                        if (gerenciadorEstruturas != null) {
                            gerenciadorEstruturas.adicionarMusicaBiblioteca(musica);
                        }
                    }
                }
                atualizarTabela();
                System.out.println("Biblioteca carregada do JSON!");
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo JSON: " + arquivoJson);
            e.printStackTrace();
        }
    }

    private void atualizarTabela() {
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
                    mainController.tocandoProperty().get()) {
                    iconPlay.setIconLiteral("ion4-ios-pause");
                } else {
                    iconPlay.setIconLiteral("ion4-ios-play");
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
