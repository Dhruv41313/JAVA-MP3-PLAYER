import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.*;
import javafx.util.Duration;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.FieldKey;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main extends Application {

    private List<File> playlist = new ArrayList<>();
    private int currentIndex = 0;
    private MediaPlayer mediaPlayer;
    private ListView<String> playlistView;
    private Slider volumeSlider, progressSlider;
    private Label titleLabel, artistLabel, albumLabel, timeLabel;
    private ImageView albumArt;
    private TextArea lyricsArea;
    private boolean isRepeat = false;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;

        // --- UI Components ---
        playlistView = new ListView<>();
        playlistView.setPrefWidth(250);

        Button importBtn = new Button("Import Songs");
        Button playBtn = new Button("▶ Play");
        Button pauseBtn = new Button("⏸ Pause");
        Button stopBtn = new Button("⏹ Stop");
        Button nextBtn = new Button("⏭ Next");
        Button prevBtn = new Button("⏮ Previous");
        Button repeatBtn = new Button("🔁 Repeat: OFF");
        Button savePlaylistBtn = new Button("💾 Save Playlist");
        Button loadPlaylistBtn = new Button("📂 Load Playlist");

        HBox controlsBox = new HBox(10, prevBtn, playBtn, pauseBtn, stopBtn, nextBtn, repeatBtn);
        controlsBox.setAlignment(Pos.CENTER);

        volumeSlider = new Slider(0, 1, 0.5);
        progressSlider = new Slider();

        timeLabel = new Label("00:00 / 00:00");

        HBox bottomBox = new HBox(10, new Label("🔊 Volume"), volumeSlider, progressSlider, timeLabel);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));

        albumArt = new ImageView(new Image(getClass().getResourceAsStream("default_art.png")));
        albumArt.setFitWidth(200);
        albumArt.setFitHeight(200);
        albumArt.setPreserveRatio(true);

        titleLabel = new Label("Title: ");
        artistLabel = new Label("Artist: ");
        albumLabel = new Label("Album: ");

        VBox metadataBox = new VBox(5, albumArt, titleLabel, artistLabel, albumLabel);
        metadataBox.setAlignment(Pos.CENTER);

        lyricsArea = new TextArea();
        lyricsArea.setEditable(false);
        lyricsArea.setPrefRowCount(6);
        lyricsArea.setWrapText(true);
        lyricsArea.setPromptText("Lyrics will appear here...");

        VBox leftBox = new VBox(10, importBtn, savePlaylistBtn, loadPlaylistBtn, playlistView);
        leftBox.setPadding(new Insets(10));

        VBox rightBox = new VBox(15, metadataBox, controlsBox, bottomBox, lyricsArea);
        rightBox.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setLeft(leftBox);
        root.setCenter(rightBox);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f0f0f, #1c1c1c); -fx-background-radius: 10;");

        Scene scene = new Scene(root, 950, 600);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("JAVA MP3 PLAYER");
        primaryStage.show();

        // --- EVENT HANDLERS ---

        importBtn.setOnAction(e -> importSongs());
        playBtn.setOnAction(e -> playSong());
        pauseBtn.setOnAction(e -> pauseSong());
        stopBtn.setOnAction(e -> stopSong());
        nextBtn.setOnAction(e -> nextSong());
        prevBtn.setOnAction(e -> previousSong());
        repeatBtn.setOnAction(e -> {
            isRepeat = !isRepeat;
            repeatBtn.setText(isRepeat ? "🔁 Repeat: ON" : "🔁 Repeat: OFF");
        });

        savePlaylistBtn.setOnAction(e -> savePlaylist());
        loadPlaylistBtn.setOnAction(e -> loadPlaylist());

        playlistView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                int index = playlistView.getSelectionModel().getSelectedIndex();
                if (index >= 0) {
                    currentIndex = index;
                    playSelectedSong();
                }
            }
        });
    }

    private void importSongs() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.aac"));
        List<File> files = chooser.showOpenMultipleDialog(stage);
        if (files != null && !files.isEmpty()) {
            playlist.addAll(files);
            updatePlaylistView();
            if (mediaPlayer == null) {
                currentIndex = 0;
                playSelectedSong();
            }
        }
    }

    private void updatePlaylistView() {
        playlistView.getItems().clear();
        for (File file : playlist) {
            playlistView.getItems().add(file.getName());
        }
    }

    private void playSelectedSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        File selected = playlist.get(currentIndex);
        Media media = new Media(selected.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setOnEndOfMedia(() -> {
            if (isRepeat) {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
            } else {
                nextSong();
            }
        });

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            progressSlider.setValue(newTime.toSeconds());
            updateTimer();
        });

        mediaPlayer.setOnReady(() -> {
            progressSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            mediaPlayer.play();
            mediaPlayer.setVolume(volumeSlider.getValue());
            showMetadata(selected);
            loadLyrics(selected);
        });

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null) mediaPlayer.setVolume(newVal.doubleValue());
        });

        progressSlider.setOnMousePressed(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));
            }
        });
        progressSlider.setOnMouseDragged(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));
            }
        });

        playlistView.getSelectionModel().select(currentIndex);
    }

    private void playSong() {
        if (mediaPlayer == null && !playlist.isEmpty()) {
            playSelectedSong();
        } else if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    private void pauseSong() {
        if (mediaPlayer != null) mediaPlayer.pause();
    }

    private void stopSong() {
        if (mediaPlayer != null) mediaPlayer.stop();
    }

    private void nextSong() {
        if (!playlist.isEmpty()) {
            currentIndex = (currentIndex + 1) % playlist.size();
            playSelectedSong();
        }
    }

    private void previousSong() {
        if (!playlist.isEmpty()) {
            currentIndex = (currentIndex - 1 + playlist.size()) % playlist.size();
            playSelectedSong();
        }
    }

    private void showMetadata(File file) {
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();
            if (tag != null) {
                titleLabel.setText("Title: " + tag.getFirst(FieldKey.TITLE));
                artistLabel.setText("Artist: " + tag.getFirst(FieldKey.ARTIST));
                albumLabel.setText("Album: " + tag.getFirst(FieldKey.ALBUM));
            }

            if (tag != null && tag.getFirstArtwork() != null) {
                byte[] imageData = tag.getFirstArtwork().getBinaryData();
                if (imageData != null) {
                    InputStream in = new ByteArrayInputStream(imageData);
                    albumArt.setImage(new Image(in));
                    return;
                }
            }
            albumArt.setImage(new Image(getClass().getResourceAsStream("default_art.png")));
        } catch (Exception e) {
            titleLabel.setText("Title: Unknown");
            artistLabel.setText("Artist: Unknown");
            albumLabel.setText("Album: Unknown");
            albumArt.setImage(new Image(getClass().getResourceAsStream("default_art.png")));
        }
    }

    private void loadLyrics(File songFile) {
        try {
            String baseName = songFile.getName().substring(0, songFile.getName().lastIndexOf('.'));
            File lyricsFile = new File(songFile.getParent(), baseName + ".txt");
            if (lyricsFile.exists()) {
                lyricsArea.setText(Files.readString(lyricsFile.toPath()));
            } else {
                lyricsArea.setText("Lyrics not available.");
            }
        } catch (Exception e) {
            lyricsArea.setText("Error loading lyrics.");
        }
    }

    private void updateTimer() {
        if (mediaPlayer != null) {
            Duration current = mediaPlayer.getCurrentTime();
            Duration total = mediaPlayer.getTotalDuration();
            timeLabel.setText(formatTime(current) + " / " + formatTime(total));
        }
    }

    private String formatTime(Duration duration) {
        int seconds = (int) duration.toSeconds();
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void savePlaylist() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName("playlist.txt");
        File file = chooser.showSaveDialog(stage);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (File song : playlist) {
                    writer.write(song.getAbsolutePath());
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadPlaylist() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            playlist.clear();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    File song = new File(line.trim());
                    if (song.exists()) playlist.add(song);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            updatePlaylistView();
            if (!playlist.isEmpty()) {
                currentIndex = 0;
                playSelectedSong();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
