package com.example.app.controllers;

import javafx.collections.*;
import javafx.fxml.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.*;
import java.util.stream.Collectors;

import org.kordamp.ikonli.javafx.FontIcon;

public class ExploreController {
    @FXML private ListView<Song> songsListView;
    @FXML private TextField searchField;
    @FXML private Label pageInfoLabel;

    private ObservableList<Song> allSongs = FXCollections.observableArrayList();
    private ObservableList<Song> filteredSongs = FXCollections.observableArrayList();
    private int currentPage = 1;
    private final int itemsPerPage = 10;
    private boolean canGoPrevious = false;
    private boolean canGoNext = true;

    @FXML
    public void initialize() {
        // Initialize with sample data (replace with your actual data loading)
        loadSampleSongs();

        // Set up search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSongs(newValue);
        });

        // Set up ListView cell factory
        songsListView.setCellFactory(lv -> new ListCell<Song>() {
            private final HBox root;
            private final Label songTitleLabel = new Label();
            private final Label authorLabel = new Label();
            private final Button playButton = new Button();

            {
                // Set up UI
                VBox textBox = new VBox(songTitleLabel, authorLabel);
                textBox.setSpacing(2);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                FontIcon playIcon = new FontIcon("fas-play");
                playIcon.setIconSize(16);
                playButton.setGraphic(playIcon);
                playButton.getStyleClass().add("play-button");
                playButton.setOnAction(event -> handlePlay(getItem()));

                root = new HBox(textBox, spacer, playButton);
                root.setAlignment(Pos.CENTER_LEFT);
                root.getStyleClass().add("song-item");
                root.setSpacing(10);
            }

            @Override
            protected void updateItem(Song song, boolean empty) {
                super.updateItem(song, empty);
                if (empty || song == null) {
                    setGraphic(null);
                } else {
                    songTitleLabel.setText(song.getTitle());
                    authorLabel.setText(song.getAuthor());
                    setGraphic(root);
                }
            }
        });

        // Load first page
        updateListView();
    }

    private void loadSampleSongs() {
        // Replace with your actual data loading logic
        for (int i = 1; i <= 50; i++) {
            allSongs.add(new Song("Song " + i, "Artist " + (i % 5 + 1)));
        }
        filteredSongs.setAll(allSongs);
    }

    private void filterSongs(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            filteredSongs.setAll(allSongs);
        } else {
            filteredSongs.setAll((Song) allSongs.stream()
                    .filter(song -> song.getTitle().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList()));
        }
        currentPage = 1;
        updateListView();
    }

    private void updateListView() {
        int fromIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, filteredSongs.size());

        if (fromIndex >= filteredSongs.size()) {
            songsListView.getItems().clear();
        } else {
            songsListView.setItems(FXCollections.observableArrayList(
                    filteredSongs.subList(fromIndex, toIndex)
            ));
        }

        updatePaginationControls();
    }

    private void updatePaginationControls() {
        int totalPages = (int) Math.ceil((double) filteredSongs.size() / itemsPerPage);
        pageInfoLabel.setText(String.format("Page %d of %d", currentPage, totalPages));

        canGoPrevious = currentPage > 1;
        canGoNext = currentPage < totalPages;
    }

    @FXML
    private void handleSearch() {
        filterSongs(searchField.getText());
    }

    @FXML
    private void handlePlay(Song song) {
        // Implement your play logic here
        System.out.println("Playing: " + song.getTitle() + " by " + song.getAuthor());
    }

    @FXML
    private void previousPage() {
        if (canGoPrevious) {
            currentPage--;
            updateListView();
        }
    }

    @FXML
    private void nextPage() {
        if (canGoNext) {
            currentPage++;
            updateListView();
        }
    }

    // Song model class (could be in a separate file)
    public static class Song {
        private final String title;
        private final String author;

        public Song(String title, String author) {
            this.title = title;
            this.author = author;
        }

        public String getTitle() { return title; }
        public String getAuthor() { return author; }
    }
}