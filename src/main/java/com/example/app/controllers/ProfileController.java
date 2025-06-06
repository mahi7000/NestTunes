package com.example.app.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProfileController {
    // UI Components
    @FXML private ListView<String> postsListView;
    @FXML private ListView<String> favoritesListView;
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private Label coverPathLabel;
    @FXML private Label audioPathLabel;
    @FXML private Button uploadButton;

    // File handling
    private File coverFile;
    private File audioFile;
    private final Path imagesDir = Paths.get("images");
    private final Path soundsDir = Paths.get("sounds");

    // Data
    private final ObservableList<String> posts = FXCollections.observableArrayList();
    private final ObservableList<String> favorites = FXCollections.observableArrayList();

    // Constants
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS =
            Set.of("png", "jpg", "jpeg");
    private static final Set<String> ALLOWED_AUDIO_EXTENSIONS =
            Set.of("mp3", "wav", "aac", "ogg");

    @FXML
    public void initialize() {
        setupDirectories();
        initializeLists();
        setupEventHandlers();
        loadExistingData();
    }

    private void setupDirectories() {
        try {
            Files.createDirectories(imagesDir);
            Files.createDirectories(soundsDir);
        } catch (IOException e) {
            showError("Directory Error", "Failed to create storage directories");
        }
    }

    private void initializeLists() {
        postsListView.setItems(posts);
        favoritesListView.setItems(favorites);

        // Enable drag and drop for both lists
        setupDragAndDrop(postsListView);
        setupDragAndDrop(favoritesListView);
    }

    private void setupEventHandlers() {
        uploadButton.setOnAction(e -> handleUpload());
    }

    private void loadExistingData() {
        // Load previously saved posts from file/database in real implementation
        posts.addAll(
                "Blinding Lights - The Weeknd",
                "Save Your Tears - The Weeknd",
                "Stay - The Kid LAROI, Justin Bieber"
        );

        favorites.addAll(
                "Dynamite - BTS",
                "Watermelon Sugar - Harry Styles"
        );
    }

    @FXML
    private void handleCoverBrowse() {
        FileChooser fileChooser = createFileChooser(
                "Select Album Cover",
                ALLOWED_IMAGE_EXTENSIONS,
                "Image Files"
        );

        coverFile = fileChooser.showOpenDialog(getStage());
        if (coverFile != null) {
            if (isValidExtension(coverFile, ALLOWED_IMAGE_EXTENSIONS)) {
                coverPathLabel.setText(coverFile.getName());
            } else {
                showError("Invalid File", "Please select a valid image file (PNG, JPG, JPEG)");
            }
        }
    }

    @FXML
    private void handleAudioBrowse() {
        FileChooser fileChooser = createFileChooser(
                "Select Audio File",
                ALLOWED_AUDIO_EXTENSIONS,
                "Audio Files"
        );

        audioFile = fileChooser.showOpenDialog(getStage());
        if (audioFile != null) {
            if (isValidExtension(audioFile, ALLOWED_AUDIO_EXTENSIONS)) {
                audioPathLabel.setText(audioFile.getName());
            } else {
                showError("Invalid File", "Please select a valid audio file (MP3, WAV, AAC, OGG)");
            }
        }
    }

    private void handleUpload() {
        if (!validateInput()) return;

        try {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String baseFilename = sanitizeFilename(titleField.getText()) + "_" + timestamp;

            // Save cover image if selected
            String coverPath = null;
            if (coverFile != null) {
                String extension = getFileExtension(coverFile.getName());
                coverPath = saveFile(coverFile, imagesDir, baseFilename + "_cover." + extension);
            }

            // Save audio file if selected
            String audioPath = null;
            if (audioFile != null) {
                String extension = getFileExtension(audioFile.getName());
                audioPath = saveFile(audioFile, soundsDir, baseFilename + "_audio." + extension);
            }

            // Add to posts
            String entry = String.format("%s - %s", titleField.getText(), authorField.getText());
            posts.add(entry);

            // Save metadata to database/file in real implementation

            resetForm();
            showSuccess("Upload Successful", "Your music has been uploaded successfully");

        } catch (IOException e) {
            showError("Upload Failed", "Error saving files: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        if (titleField.getText().trim().isEmpty()) {
            showError("Missing Title", "Please enter a music title");
            return false;
        }

        if (authorField.getText().trim().isEmpty()) {
            showError("Missing Author", "Please enter an author name");
            return false;
        }

        if (audioFile == null) {
            showError("Missing Audio", "Please select an audio file");
            return false;
        }

        return true;
    }

    private String saveFile(File sourceFile, Path destinationDir, String filename) throws IOException {
        Path destination = destinationDir.resolve(filename);
        Files.copy(sourceFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        return destination.toString();
    }

    private String sanitizeFilename(String input) {
        return input.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    private void resetForm() {
        titleField.clear();
        authorField.clear();
        coverPathLabel.setText("No file selected");
        audioPathLabel.setText("No file selected");
        coverFile = null;
        audioFile = null;
    }

    // Helper methods
    private FileChooser createFileChooser(String title, Set<String> extensions, String description) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(description,
                        extensions.stream().map(ext -> "*." + ext).toArray(String[]::new))
        );
        return fileChooser;
    }

    private boolean isValidExtension(File file, Set<String> allowedExtensions) {
        String extension = getFileExtension(file.getName());
        return allowedExtensions.contains(extension.toLowerCase());
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1).toLowerCase();
    }

    private Stage getStage() {
        return (Stage) uploadButton.getScene().getWindow();
    }

    private void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }

    private void showSuccess(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Drag and drop functionality for lists
    private void setupDragAndDrop(ListView<String> listView) {
        listView.setOnDragOver(event -> {
            if (event.getGestureSource() != listView &&
                    event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        listView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                ObservableList<String> sourceList =
                        event.getGestureSource() instanceof ListView ?
                                ((ListView<String>)event.getGestureSource()).getItems() : null;

                if (sourceList != null) {
                    String selectedItem = db.getString();
                    sourceList.remove(selectedItem);
                    listView.getItems().add(selectedItem);
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }
}