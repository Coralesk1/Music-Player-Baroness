package com.otaviogustavo.controllers;

import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mpatric.mp3agic.Mp3File;
import com.otaviogustavo.App;

import com.otaviogustavo.GerenciadorEstruturas;
import com.otaviogustavo.Musica;
import com.otaviogustavo.PlayList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import com.otaviogustavo.commands.*;

public class MainController {

    private GerenciadorEstruturas gerenciadorEstruturas;
    private MediaPlayer mediaPlayer;
    private final ObjectProperty<Musica> musicaAtual = new SimpleObjectProperty<>(null);
    private final BooleanProperty tocando = new SimpleBooleanProperty(false);
    private Queue<Musica> filaReproducao = new LinkedList<>();
    private List<Musica> listaContexto;
    private Object contextoAtivo = "BIBLIOTECA";

    // Comandos
    private Comando comandoPlay;
    private Comando comandoProximo;
    private Comando comandoAnterior;

    public BooleanProperty tocandoProperty() {
        return tocando;
    }

    public ObjectProperty<Musica> musicaAtualProperty() {
        return musicaAtual;
    }

    public void definirGerenciador(GerenciadorEstruturas gerenciadorEstruturas) {
        this.gerenciadorEstruturas = gerenciadorEstruturas;
    }

    public void definirFilaReproducao(List<Musica> fila, Object contexto) {
        this.listaContexto = fila;
        this.contextoAtivo = contexto;
    }

    public Object getContextoAtivo() {
        return contextoAtivo;
    }

    @FXML private VBox contentArea;
    @FXML private ToggleButton btnHome;
    @FXML private ToggleButton btnLibrary;
    @FXML private ListView<String> listPlaylists;
    @FXML private Button btnNewPlaylist;
    @FXML private ImageView imgCapa;

    @FXML private ToggleButton btnPlay;
    @FXML private Button btnNext;
    @FXML private Button btnPrevious;
    @FXML private FontIcon iconPlay;
    @FXML private ToggleButton btnAleatorio;
    @FXML private FontIcon iconAleatorio;

    @FXML private Label lblTitulo;
    @FXML private Label lblArtista;
    @FXML private Label lblTempoAtual;
    @FXML private Label lblTempoTotal;
    @FXML private Slider sliderTime;

    @FXML private Button btnSilenciar;
    @FXML private FontIcon iconVolume;
    @FXML private Slider sliderVolume;

    private double volumeAtual = 1.0;
    private boolean estaMudo = false;
    private double volumeAntesDeSilenciar = 1.0;

    @FXML
    private void lidarComSilenciar(ActionEvent event) {
        if (estaMudo) {
            estaMudo = false;
            volumeAtual = volumeAntesDeSilenciar;
            sliderVolume.setValue(volumeAtual * 100);
            iconVolume.setIconLiteral("ion4-ios-volume-high");
        } else {
            volumeAntesDeSilenciar = volumeAtual;
            estaMudo = true;
            volumeAtual = 0;
            sliderVolume.setValue(0);
            iconVolume.setIconLiteral("ion4-ios-volume-off");
        }

        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volumeAtual);
        }
    }

    @FXML
    private void lidarComNovaPlaylist(ActionEvent event) {
        try {

            URL fxmlLocation = App.class.getResource("/com/otaviogustavo/views/create_playlist_dialog.fxml");
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            MainPlaylistController mainPlaylistController = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initOwner(btnNewPlaylist.getScene().getWindow());

            Scene scene = new Scene(root);
            scene.getStylesheets().add(App.class.getResource("/com/otaviogustavo/css/main.css").toExternalForm());
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            stage.showAndWait();

            if (mainPlaylistController.estaCriado()) {

                String nome = mainPlaylistController.getTitulo();
                String descricao = mainPlaylistController.getDescricao();
                LocalDateTime dtAgora = LocalDateTime.now();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                String dtCriacao = dtAgora.format(formatter);

                System.out.println("Nova playlist criada: " + nome + " - " + descricao);

                gerenciadorEstruturas.adicionaPlaylistVazia(new PlayList(nome, descricao, dtCriacao));

                salvarDadosNoJson();

                listPlaylists.getItems().add(nome);
                listPlaylists.getSelectionModel().select(nome);

            }

        } catch (Exception e) {
            System.out.println("Erro ao salvar playlist");
            e.printStackTrace();
        }
    }

    public void salvarDadosNoJson() {
        if (gerenciadorEstruturas != null) {
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();

            // Grava o arquivo de configuração na raiz do projeto
            try (FileWriter writer = new FileWriter("BibliotecaEPlaylists.json")) {
                gson.toJson(gerenciadorEstruturas, writer);
                System.out.println("Dados salvos com sucesso em BibliotecaEPlaylists.json!");

            } catch (IOException e) {
                System.err.println("Erro ao salvar dados no json: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void lidarComRemoverPlaylist(String nome) {
        if (gerenciadorEstruturas != null) {
            gerenciadorEstruturas.removerPlaylist(nome);

            //atualiza o json depois da remoção da playlist
            salvarDadosNoJson();
            listPlaylists.getItems().remove(nome);


            carregarView("main_home");
        }
    }

    @FXML
    private void lidarComAcaoMenu(ActionEvent event) {
        ToggleButton selectedButton = (ToggleButton) event.getSource();
        String fxml = "";

        if (selectedButton == btnHome) {
            fxml = "main_home";
        } else if (selectedButton == btnLibrary) {
            fxml = "main_library";
        }

        if (!fxml.isEmpty()) {
            carregarView(fxml);
        }
    }

    @FXML
    private void lidarComAcaoPlay(ActionEvent event) {
        comandoPlay.executar();
    }

    public void alternarReproducao() {
        if (mediaPlayer == null) {
            btnPlay.setSelected(false);
            tocando.set(false);
            return;
        }

        if (btnPlay.isSelected()) {
            mediaPlayer.play();
            tocando.set(true);
            iconPlay.setIconLiteral("ion4-ios-pause");
        } else {
            mediaPlayer.pause();
            tocando.set(false);
            iconPlay.setIconLiteral("ion4-ios-play");
        }
    }

    @FXML
    private void lidarComAcaoProxima(ActionEvent event) {
        comandoProximo.executar();
    }

    @FXML
    private void lidarComAcaoAnterior(ActionEvent event) {
        comandoAnterior.executar();
    }

    public void tocarMusica(Musica musica, List<Musica> novaFila, Object novoContexto) {
        if (musica == null) return;

        // Se a música clicada já é a que está tocando E o contexto é o mesmo, alterna entre play/pause
        if (musicaAtual.get() != null && musicaAtual.get().equals(musica) &&
                java.util.Objects.equals(novoContexto, this.contextoAtivo)) {
            if (tocando.get()) {
                mediaPlayer.pause();
                tocando.set(false);
                btnPlay.setSelected(false);
                iconPlay.setIconLiteral("ion4-ios-play");
            } else {
                mediaPlayer.play();
                tocando.set(true);
                btnPlay.setSelected(true);
                iconPlay.setIconLiteral("ion4-ios-pause");
            }
            return;
        }

        // Se for uma música nova ou um contexto novo, define a nova lista e contexto
        if (novaFila != null) {
            this.listaContexto = novaFila;
            this.contextoAtivo = novoContexto;
        } else if (this.listaContexto == null) {
            this.listaContexto = new java.util.ArrayList<>(gerenciadorEstruturas.getBibliotecaGeral());
        }

        // monta a fila com as músicas que vem depois da selecionada
        prepararFila(musica);

        reproduzirMusica(musica);
    }

    public void removerDaFilaEContexto(Musica musica) {
        if (listaContexto != null) {
            listaContexto.remove(musica);
        }
        if (filaReproducao != null) {
            filaReproducao.remove(musica);
        }
    }

    @FXML
    private void lidarComAcaoAleatorio(ActionEvent event) {
        if (btnAleatorio.isSelected()) {
            iconAleatorio.setStyle("-fx-icon-color: white;");
        } else {
            iconAleatorio.setStyle("-fx-icon-color: grey;");
        }
        if (musicaAtual.get() != null) {
            prepararFila(musicaAtual.get());
        }
    }

    private void prepararFila(Musica musicaReferencia) {
        filaReproducao.clear();
        if (listaContexto == null || listaContexto.isEmpty()) return;

        if (btnAleatorio != null && btnAleatorio.isSelected()) {
            List<Musica> embaralhada = new java.util.ArrayList<>(listaContexto);
            embaralhada.remove(musicaReferencia);
            java.util.Collections.shuffle(embaralhada);
            filaReproducao.addAll(embaralhada);
        } else {
            boolean encontrou = false;
            for (Musica m : listaContexto) {
                if (encontrou) {
                    filaReproducao.offer(m);
                }
                if (m.equals(musicaReferencia)) {
                    encontrou = true;
                }
            }
        }
    }

    private void reproduzirMusica(Musica musica) {

        File file = new File(musica.getCaminho());

        if (!file.exists()) {

            System.err.println("Arquivo não encontrado no disco (deletado ou movido): " + musica.getCaminho());

            if (gerenciadorEstruturas != null) {

                gerenciadorEstruturas.removerMusicaBiblioteca(musica);

                for (java.util.List<Musica> pList : gerenciadorEstruturas.getPlaylists().values()) {
                    pList.remove(musica);
                }
                gerenciadorEstruturas.getHistoricoReproducao().remove(musica);
                removerDaFilaEContexto(musica);
                salvarDadosNoJson();
            }
            javafx.application.Platform.runLater(() -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                alert.setTitle("Música não encontrada");
                alert.setHeaderText(null);
                alert.setContentText("O arquivo da música '" + musica.getTitulo() + "' não foi encontrado. Ele será removido da biblioteca.");
                alert.showAndWait();
                tocarProximaMusica();
            });
            return;
        }

        // Atualiza o histórico
        if (gerenciadorEstruturas != null) {
            gerenciadorEstruturas.adicionarMusicaHistorico(musica);
            salvarDadosNoJson();
        }

        // Se ja estiver tocando uma musica diferente, para para dar os recursos para outra que for tocar
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        try {
            this.musicaAtual.set(musica);
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            lblTitulo.setText(musica.getTitulo());
            lblArtista.setText(musica.getArtista());

            // Carrega a capa sob demanda para economizar memória
            byte[] capaBytes = null;
            try {
                Mp3File mp3File = new Mp3File(musica.getCaminho());
                if (mp3File.hasId3v2Tag()) {
                    capaBytes = mp3File.getId3v2Tag().getAlbumImage();
                }
            } catch (Exception e) {
                System.err.println("Erro ao carregar capa da música: " + e.getMessage());
            }

            if (capaBytes != null) {
                imgCapa.setImage(new Image(new ByteArrayInputStream(capaBytes)));
            } else {
                imgCapa.setImage(null);
            }

            btnPlay.setSelected(true);
            tocando.set(true);
            iconPlay.setIconLiteral("ion4-ios-pause");

            mediaPlayer.setOnReady(() -> {
                lblTempoTotal.setText(formatarTempo(mediaPlayer.getTotalDuration()));
                sliderTime.setMax(mediaPlayer.getTotalDuration().toSeconds());
            });

            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (!sliderTime.isValueChanging()) {
                    sliderTime.setValue(newTime.toSeconds());
                }
                lblTempoAtual.setText(formatarTempo(newTime));
            });

            sliderTime.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (sliderTime.isValueChanging()) {
                    mediaPlayer.seek(Duration.seconds(newVal.doubleValue()));
                }
            });

            // Arrastar o slider para buscar uma posição
            sliderTime.setOnMousePressed(e -> {
                if (mediaPlayer != null) mediaPlayer.pause();
            });
            sliderTime.setOnMouseReleased(e -> {
                if (mediaPlayer != null) {
                    mediaPlayer.seek(Duration.seconds(sliderTime.getValue()));
                    if (btnPlay.isSelected()) mediaPlayer.play();
                }
            });

            mediaPlayer.setOnEndOfMedia(this::tocarProximaMusica);

            mediaPlayer.setVolume(volumeAtual);
            mediaPlayer.play();

        } catch (Exception e) {
            System.err.println("Erro ao tocar música: " + e.getMessage());
        }
    }

    public void tocarProximaMusica() {

        Musica proxima = filaReproducao.poll();

        if (proxima != null) {
            reproduzirMusica(proxima);
        } else { //fim da fila
            System.out.println("Fim da fila de reprodução no contexto ativo.");
            pararReproducao();
        }
    }

    private void pararReproducao() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            tocando.set(false);
            btnPlay.setSelected(false);
            iconPlay.setIconLiteral("ion4-ios-play");
        }
    }

    public void tocarMusicaAnterior() {
        if (musicaAtual.get() == null || listaContexto == null) return;

        Musica anterior = null;

        for (Musica m : listaContexto) {
            if (m.equals(musicaAtual.get())) {
                if (anterior != null) {
                    tocarMusica(anterior, listaContexto, contextoAtivo);
                }
                return;
            }
            anterior = m;
        }
    }

    private String formatarTempo(Duration duration) {
        int segundosTotais = (int) duration.toSeconds();
        int minutos = segundosTotais / 60;
        int segundos = segundosTotais % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }

    private void carregarView(String fxml) {

        carregarDadosDoJson();

        try {

            URL fxmlLocation = App.class.getResource("/com/otaviogustavo/views/" + fxml + ".fxml");
            if (fxmlLocation == null) {
                System.err.println("FXML file not found: /com/otaviogustavo/views/" + fxml + ".fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            Object subController = loader.getController();

            if (subController instanceof MainLibraryController) {
                MainLibraryController libraryController = (MainLibraryController) subController;

                libraryController.definirGerenciador(this.gerenciadorEstruturas);
                libraryController.definirMainController(this);
            }

            if (subController instanceof MainPlaylistController) {
                MainPlaylistController mainPlaylistController = (MainPlaylistController) subController;
                mainPlaylistController.definirGerenciador(this.gerenciadorEstruturas);
                mainPlaylistController.definirMainController(this);
            }
            
            if (subController instanceof MainHomeController) {
                MainHomeController homeController = (MainHomeController) subController;
                homeController.definirGerenciador(this.gerenciadorEstruturas);
                homeController.definirMainController(this);
            }

            contentArea.getChildren().setAll(root);

        } catch (IOException e) {
            System.err.println("Erro ao carregar a view: " + fxml);
            e.printStackTrace();
        }
    }

    private void carregarDadosDoJson(){

        File arquivoJson = new File("BibliotecaEPlaylists.json");

        if (!arquivoJson.exists()) {
            return;
        }

        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

        try(FileReader reader = new FileReader(arquivoJson)){

            GerenciadorEstruturas dados = gson.fromJson(reader, GerenciadorEstruturas.class);

            if (dados != null){
                listPlaylists.getItems().clear();
                this.gerenciadorEstruturas = dados;
            }
            limparMusicasDeletadas(true);
            atualizarListaPlaylists();

        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo JSON: " + arquivoJson);
            e.printStackTrace();
        }
    }

    private void limparMusicasDeletadas(boolean salvarJson) {
        if (gerenciadorEstruturas == null) return;
        boolean houveAlteracao = false;

        if (gerenciadorEstruturas.getBibliotecaGeral() != null) {
            java.util.Iterator<Musica> itBib = gerenciadorEstruturas.getBibliotecaGeral().iterator();
            while (itBib.hasNext()) {
                Musica m = itBib.next();
                if (!new File(m.getCaminho()).exists()) {
                    itBib.remove();
                    houveAlteracao = true;
                }
            }
        }

        if (gerenciadorEstruturas.getPlaylists() != null) {
            for (java.util.List<Musica> pList : gerenciadorEstruturas.getPlaylists().values()) {
                if (pList != null) {
                    java.util.Iterator<Musica> itPl = pList.iterator();
                    while (itPl.hasNext()) {
                        Musica m = itPl.next();
                        if (!new File(m.getCaminho()).exists()) {
                            itPl.remove();
                            houveAlteracao = true;
                        }
                    }
                }
            }
        }

        if (gerenciadorEstruturas.getHistoricoReproducao() != null) {
            java.util.Iterator<Musica> itHist = gerenciadorEstruturas.getHistoricoReproducao().iterator();
            while (itHist.hasNext()) {
                Musica m = itHist.next();
                if (!new File(m.getCaminho()).exists()) {
                    itHist.remove();
                    houveAlteracao = true;
                }
            }
        }

        if (houveAlteracao && salvarJson) {
            salvarDadosNoJson();
            System.out.println("Sincronização: Músicas deletadas do disco foram removidas das listas.");
        }
    }

    private void atualizarListaPlaylists(){
        if (gerenciadorEstruturas != null){
            gerenciadorEstruturas.getPlaylists().keySet().forEach(playlist -> {
                listPlaylists.getItems().add(playlist.getNome());
            });
        }
    }

    @FXML
    public void initialize() {
        // Inicializa os comandos
        comandoPlay = new ComandoPlay(this);
        comandoProximo = new ComandoProximo(this);
        comandoAnterior = new ComandoAnterior(this);

        carregarView("main_home");

        listPlaylists.setCellFactory(lv -> new ListCell<String>() {
            private final HBox container = new HBox(10);
            private final Label label = new Label();
            private final Button btnDelete = new Button();
            private final FontIcon iconDelete = new FontIcon("ion4-md-trash");

            {
                label.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(label, Priority.ALWAYS);
                iconDelete.setIconSize(16);
                btnDelete.setGraphic(iconDelete);
                btnDelete.getStyleClass().add("button-delete-playlist");
                btnDelete.setOnAction(e -> {
                    String item = getItem();
                    if (item != null) {
                        lidarComRemoverPlaylist(item);
                    }
                });
                container.getChildren().addAll(label, btnDelete);
                container.setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    label.setText(item);
                    setGraphic(container);
                }
            }
        });

        listPlaylists.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !carregando) {
                lidarComSelecaoPlaylist(newVal);
            }
        });

        sliderTime.valueProperty().addListener((obs, oldVal, newVal) -> atualizaCorSlider(sliderTime));
        sliderTime.maxProperty().addListener((obs, oldVal, newVal) -> atualizaCorSlider(sliderTime));

        sliderVolume.valueProperty().addListener((obs, oldVal, newVal) -> {
            volumeAtual = newVal.doubleValue() / 100.0;
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(volumeAtual);
            }

            if (volumeAtual > 0) {
                estaMudo = false;
                if (volumeAtual > 0.5) {
                    iconVolume.setIconLiteral("ion4-ios-volume-high");
                } else {
                    iconVolume.setIconLiteral("ion4-ios-volume-low");
                }
            } else {
                estaMudo = true;
                iconVolume.setIconLiteral("ion4-ios-volume-off");
            }
            atualizaCorSlider(sliderVolume);
        });

        javafx.application.Platform.runLater(() -> {
            atualizaCorSlider(sliderVolume);
            atualizaCorSlider(sliderTime);
        });
    }

    private void atualizaCorSlider(Slider slider) {
        javafx.scene.Node track = slider.lookup(".track");
        if (track != null) {
            double max = slider.getMax();
            double val = slider.getValue();
            double percentage = (max == 0) ? 0 : (val / max) * 100;
            if (Double.isNaN(percentage) || Double.isInfinite(percentage)) percentage = 0;
            String style = String.format(java.util.Locale.US, "-fx-background-color: linear-gradient(to right, #FF1493 0%%, #9370DB %.1f%%, #1a1a1a %.1f%%, #1a1a1a 100%%);", percentage, percentage);
            track.setStyle(style);
        }
    }

    private boolean carregando = false;

    private void lidarComSelecaoPlaylist(String nomePlaylist) {
        if (nomePlaylist == null || nomePlaylist.isEmpty()) {
            return;
        }

        carregando = true;
        try {
            // Recarrega os dados do JSON
            carregarDadosDoJson();

            // Garante que o ListView mantenha o valor selecionado após o recarregamento
            listPlaylists.getSelectionModel().select(nomePlaylist);

            PlayList playlistSelecionada = null;
            if (gerenciadorEstruturas != null) {
                for (PlayList pl : gerenciadorEstruturas.getPlaylists().keySet()) {
                    if (pl.getNome().equals(nomePlaylist)) {
                        playlistSelecionada = pl;
                        break;
                    }
                }
            }

            if (playlistSelecionada != null) {
                carregarTelaPlaylist(playlistSelecionada);
            }
        } finally {
            carregando = false;
        }
    }

    private void carregarTelaPlaylist(PlayList playlist) {
        try {
            URL fxmlLocation = App.class.getResource("/com/otaviogustavo/views/playlist_view.fxml");
            if (fxmlLocation == null) {
                System.err.println("Erro ao encontrar o arquivo FXML para visualizacao de playlist.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            Object subController = loader.getController();

            if (subController instanceof MainPlaylistController) {
                MainPlaylistController mainPlaylistController = (MainPlaylistController) subController;
                mainPlaylistController.definirPlaylist(playlist, this.gerenciadorEstruturas, this);
            }

            contentArea.getChildren().setAll(root);
        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela da playlist: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
