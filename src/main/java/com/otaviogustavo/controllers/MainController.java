package com.otaviogustavo.controllers;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.otaviogustavo.App;

import com.otaviogustavo.GerenciadorEstruturas;
import com.otaviogustavo.models.Musica;
import com.otaviogustavo.models.PlayList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

public class MainController {

    private GerenciadorEstruturas gerenciadorEstruturas;
    private MediaPlayer mediaPlayer;
    private final ObjectProperty<Musica> musicaAtual = new SimpleObjectProperty<>(null);
    private final BooleanProperty tocando = new SimpleBooleanProperty(false);

    public BooleanProperty tocandoProperty() {
        return tocando;
    }

    public ObjectProperty<Musica> musicaAtualProperty() {
        return musicaAtual;
    }

    public void setGerenciador(GerenciadorEstruturas gerenciadorEstruturas) {
        this.gerenciadorEstruturas = gerenciadorEstruturas;
    }

    @FXML private VBox contentArea;
    @FXML private ToggleButton btnHome;
    @FXML private ToggleButton btnLibrary;
    @FXML private ComboBox<String> comboPlaylists;
    @FXML private Button btnNewPlaylist;

    @FXML private ToggleButton btnPlay;
    @FXML private Button btnNext;
    @FXML private Button btnPrevious;
    @FXML private FontIcon iconPlay;

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
    private void handleNewPlaylistAction(ActionEvent event) {
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

            if (mainPlaylistController.isCriado()) {

                String nome = mainPlaylistController.getTitulo();
                String descricao = mainPlaylistController.getDescricao();
                System.out.println("Nova playlist criada: " + nome + " - " + descricao);

                gerenciadorEstruturas.criaPlaylistVazia(new PlayList(nome, descricao));

                Map<PlayList, List<Musica>> playlist =  gerenciadorEstruturas.getPlaylists();

                if (playlist != null){

                    Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();

                    // Grava o arquivo de configuração na raiz do projeto
                    try (FileWriter writer = new FileWriter("PlayLists.json")) {
                        gson.toJson(gerenciadorEstruturas, writer);
                        System.out.println("Playlist salva com sucesso em PlayLists.json!");

                    } catch (IOException e) {
                        System.err.println("Erro ao salvar playlist:" + e.getMessage());
                        e.printStackTrace();
                    }

                    comboPlaylists.getItems().add(nome);
                    comboPlaylists.setValue(nome);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMenuAction(ActionEvent event) {
        ToggleButton selectedButton = (ToggleButton) event.getSource();
        String fxml = "";

        if (selectedButton == btnHome) {
            fxml = "main_home";
        } else if (selectedButton == btnLibrary) {
            fxml = "main_library";
        }

        if (!fxml.isEmpty()) {
            loadView(fxml);
        }
    }

    @FXML
    private void handlePlayAction(ActionEvent event) {
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
    private void handleNextAction(ActionEvent event) {
        tocarProximaMusica();
    }

    @FXML
    private void handlePreviousAction(ActionEvent event) {
        tocarMusicaAnterior();
    }

    public void tocarMusica(Musica musica) {
        if (musica == null) return;

        // Se a música clicada já é a que está tocando, alterna entre play/pause
        if (musicaAtual.get() != null && musicaAtual.get().equals(musica)) {
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

        // Se ja estiver tocando uma musica diferente, para para dar os recursos para outra que for tocar
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        try {
            this.musicaAtual.set(musica);
            File file = new File(musica.getCaminho());
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            lblTitulo.setText(musica.getTitulo());
            lblArtista.setText(musica.getArtista());
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

    private void tocarProximaMusica() {
        if (gerenciadorEstruturas == null || musicaAtual.get() == null) return;

        Set<Musica> biblioteca = gerenciadorEstruturas.getBibliotecaGeral();
        boolean encontrouAtual = false;

        for (Musica m : biblioteca) {
            if (encontrouAtual) {
                tocarMusica(m);
                return;
            }
            if (m.equals(musicaAtual.get())) {
                encontrouAtual = true;
            }
        }
        System.out.println("Fim da biblioteca.");
    }

    private void tocarMusicaAnterior() {
        if (gerenciadorEstruturas == null || musicaAtual.get() == null) return;

        Set<Musica> biblioteca = gerenciadorEstruturas.getBibliotecaGeral();
        Musica anterior = null;

        for (Musica m : biblioteca) {
            if (m.equals(musicaAtual.get())) {
                if (anterior != null) {
                    tocarMusica(anterior);
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

    private void loadView(String fxml) {

        carregaComboPlaylist();

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
                libraryController.setGerenciador(this.gerenciadorEstruturas);
                libraryController.setMainController(this);
            }

            /*if (subController instanceof MainPlaylistController) {
                MainPlaylistController mainPlaylistController = (MainPlaylistController) subController;
                mainPlaylistController.setGerenciador(this.gerenciadorEstruturas);
                mainPlaylistController.setMainController(this);
            }*/

            contentArea.getChildren().setAll(root);

        } catch (IOException e) {
            System.err.println("Erro ao carregar a view: " + fxml);
            e.printStackTrace();
        }
    }

    private void carregaComboPlaylist(){

        File arquivoJson = new File("PlayLists.json");

        if (!arquivoJson.exists()) {
            return;
        }

        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

        try(FileReader reader = new FileReader(arquivoJson)){

            GerenciadorEstruturas dados = gson.fromJson(reader, GerenciadorEstruturas.class);

            if (dados != null){
                comboPlaylists.getItems().clear();
                this.gerenciadorEstruturas = dados;
            }
            atualizaComboBoxPlaylist();

        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo JSON: " + arquivoJson);
            e.printStackTrace();
        }
    }

    private void atualizaComboBoxPlaylist(){
        if (gerenciadorEstruturas != null){
            gerenciadorEstruturas.getPlaylists().keySet().forEach(playlist -> {
                comboPlaylists.getItems().add(playlist.getNome());
            });
        }
    }

    @FXML
    public void initialize() {
        loadView("main_home");

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
}
