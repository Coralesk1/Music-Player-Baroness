package com.otaviogustavo.controllers;

import com.otaviogustavo.Musica;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class MainLibraryController {

    @FXML
    private Button btnOpenFolder;

    @FXML private TableView<Musica> tabelaMusicas;
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

        tabelaMusicas.setItems(listaMusicas);


        tabelaMusicas.getSelectionModel().selectedItemProperty().addListener((obs, antigoValor, novoValor) -> {
            if (novoValor != null) {
                // 'novoValor' é o objeto Musica da linha clicada
                System.out.println("--- Música Selecionada ---");
                System.out.println("Título: " + novoValor.getTitulo());
                System.out.println("Artista: " + novoValor.getArtista());
                System.out.println("Duração: " + novoValor.getDuracao());

                // Se quiser passar para um novo construtor de outra classe:
                // AlgumaClasse obj = new AlgumaClasse(novoValor.getTitulo(), novoValor.getArtista());
            }
        });

        // Teste: Adicionando uma música manualmente
        listaMusicas.add(new Musica("Batom de Cereja", "Murilo Huff", "02:39"));


    }

    @FXML
    public void abreJanelaArquivos(ActionEvent actionEvent){

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Selecionar Pasta");

        // 2. Opcional: Define um diretório inicial (ex: pasta do usuário)
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // 3. Obtém a "Stage" (janela) atual para que a nova janela seja aberta sobre ela
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        // 4. Abre a janela e captura a pasta selecionada
        File selectedDirectory = directoryChooser.showDialog(stage);

        // 5. Verifica se o usuário selecionou algo ou cancelou
        if (selectedDirectory != null) {
            System.out.println("Pasta selecionada: " + selectedDirectory.getAbsolutePath());

            File[] files = selectedDirectory.listFiles();








        } else {
            System.out.println("Seleção cancelada.");
        }

    }
}
