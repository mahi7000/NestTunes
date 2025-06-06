package com.example.app.controllers;

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
import java.util.function.Consumer;

public class NewPostController {
    @FXML private TextField songTitleField;
    @FXML private TextField artistNameField;
    @FXML private ImageView albumCoverPreview;
    @FXML private Label audioFileLabel;

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
            // Create directories if they don't exist
            File imagesDir = new File("images");
            File soundsDir = new File("sounds");
            if (!imagesDir.exists()) imagesDir.mkdir();
            if (!soundsDir.exists()) soundsDir.mkdir();

            // Copy files to application directories
            String imageName = "cover_" + System.currentTimeMillis() + getFileExtension(imageFile.getName());
            String audioName = "track_" + System.currentTimeMillis() + getFileExtension(audioFile.getName());

            File destImage = new File("images/" + imageName);
            File destAudio = new File("sounds/" + audioName);

            Files.copy(imageFile.toPath(), destImage.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(audioFile.toPath(), destAudio.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Format: "Song Title - Artist Name"
            String postTitle = songTitleField.getText() + " - " + artistNameField.getText();
            postHandler.accept(postTitle);

            // Close the window
            ((Stage) songTitleField.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not save post");
        }
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}