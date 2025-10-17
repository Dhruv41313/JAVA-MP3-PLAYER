import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;

public class PlaylistJDBCUI extends Application {
    private static final String URL = "jdbc:sqlite:playlist.db";
    private TextArea playlistArea = new TextArea();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        initDB();

        // UI controls
        TextField songNameField = new TextField();
        songNameField.setPromptText("Song Name");

        TextField songPathField = new TextField();
        songPathField.setPromptText("File Path");

        Button addButton = new Button("Add Song");
        Button refreshButton = new Button("Refresh Playlist");
        Button deleteButton = new Button("Delete by ID");

        TextField deleteIdField = new TextField();
        deleteIdField.setPromptText("Song ID");

        playlistArea.setEditable(false);
        playlistArea.setPrefHeight(200);

        // Layout
        VBox root = new VBox(10);
        root.getChildren().addAll(
                new HBox(10, songNameField, songPathField, addButton),
                new HBox(10, deleteIdField, deleteButton),
                refreshButton,
                playlistArea
        );

        // Button actions
        addButton.setOnAction(e -> {
            String name = songNameField.getText();
            String path = songPathField.getText();
            if (!name.isEmpty() && !path.isEmpty()) {
                addSong(name, path);
                songNameField.clear();
                songPathField.clear();
                loadPlaylist();
            }
        });

        refreshButton.setOnAction(e -> loadPlaylist());

        deleteButton.setOnAction(e -> {
            try {
                int id = Integer.parseInt(deleteIdField.getText());
                deleteSong(id);
                deleteIdField.clear();
                loadPlaylist();
            } catch (NumberFormatException ex) {
                playlistArea.setText("Invalid ID entered.");
            }
        });

        // Scene setup
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Playlist Manager (JDBC Demo)");
        stage.show();

        loadPlaylist();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private void initDB() {
        String sql = "CREATE TABLE IF NOT EXISTS playlist (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "song_name TEXT," +
                "file_path TEXT)";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addSong(String name, String path) {
        String sql = "INSERT INTO playlist(song_name, file_path) VALUES(?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, path);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPlaylist() {
        String sql = "SELECT * FROM playlist";
        StringBuilder sb = new StringBuilder("--- Playlist ---\n");
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                sb.append(rs.getInt("id"))
                        .append(": ")
                        .append(rs.getString("song_name"))
                        .append(" | ")
                        .append(rs.getString("file_path"))
                        .append("\n");
            }
            playlistArea.setText(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteSong(int id) {
        String sql = "DELETE FROM playlist WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}