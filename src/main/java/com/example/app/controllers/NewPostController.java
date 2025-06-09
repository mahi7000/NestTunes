package com.example.app.controllers;
import com.example.app.utils.DatabaseConnection;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Consumer;

public class NewPostController {
    @FXML private TextField songTitleField;
    @FXML private TextField artistNameField;
    @FXML private ImageView albumCoverPreview;
    @FXML private Button audioFileLabel;

    private File audioFile;
    private File imageFile;
    private Consumer<String> postHandler;

    public void setPostHandler(Consumer<String> postHandler) {
        this.postHandler = postHandler;
    }

    @FXML
    private void selectAlbumCover() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Album Cover");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        imageFile = fileChooser.showOpenDialog(albumCoverPreview.getScene().getWindow());
        if (imageFile != null) {
            albumCoverPreview.setImage(new Image(imageFile.toURI().toString()));
        }
    }

    @FXML
    private void selectAudioFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Audio File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.aac")
        );

        audioFile = fileChooser.showOpenDialog(albumCoverPreview.getScene().getWindow());
        if (audioFile != null) {
            audioFileLabel.setText(audioFile.getName());
        }
    }

    @FXML
    private void createPost() {
        if (songTitleField.getText().isEmpty() || artistNameField.getText().isEmpty() ||
                audioFile == null || imageFile == null) {
            showAlert("Error", "All fields are required");
            return;
        }

        try {
            // Create folders if not exist
            File imagesDir = new File("src/main/resources/com/example/app/images");
            File soundsDir = new File("src/main/resources/com/example/app/sounds");
            if (!imagesDir.exists()) imagesDir.mkdirs();
            if (!soundsDir.exists()) soundsDir.mkdirs();

            // Unique filenames
            String imageName = "cover_" + System.currentTimeMillis() + getFileExtension(imageFile.getName());
            String audioName = "track_" + System.currentTimeMillis() + getFileExtension(audioFile.getName());

            File destImage = new File(imagesDir, imageName);
            File destAudio = new File(soundsDir, audioName);

            Files.copy(imageFile.toPath(), destImage.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(audioFile.toPath(), destAudio.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Save to DB
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO posts (user_id, song_title, artist_name, album_picture, song_file) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, getCurrentUserId()); // replace with actual user ID logic
                stmt.setString(2, songTitleField.getText());
                stmt.setString(3, artistNameField.getText());
                stmt.setString(4, "src/main/resources/com/example/app/images/" + imageName);
                stmt.setString(5, "src/main/resources/com/example/app/sounds/" + audioName);
                stmt.executeUpdate();
            }



            postHandler.accept(songTitleField.getText() + " - " + artistNameField.getText());
            ((Stage) songTitleField.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not save files");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to save post in database");
        }
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    private int getCurrentUserId() {
        // TODO: Replace with session-based logged-in user ID
        return 1;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
