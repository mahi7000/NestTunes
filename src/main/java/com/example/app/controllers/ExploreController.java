package com.example.app.controllers;

import com.example.app.database.DatabaseManager;
import com.example.app.models.Song;
import com.example.app.services.SongSearchService;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ExploreController {
    @FXML private ListView<Song> songsListView;
    @FXML private TextField searchField;
    @FXML private Label pageInfoLabel;
    @FXML private Button prevButton;
    @FXML private Button nextButton;

    private final ObservableList<Song> allSongs = FXCollections.observableArrayList();
    private final ObservableList<Song> filteredSongs = FXCollections.observableArrayList();
    private final IntegerProperty currentPage = new SimpleIntegerProperty(1);
    private final int itemsPerPage = 10;
    private final BooleanProperty canGoPrevious = new SimpleBooleanProperty(false);
    private final BooleanProperty canGoNext = new SimpleBooleanProperty(true);

    // Reference to your media manager
    private final MediaManager mediaManager = MediaManager.getInstance();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final SongSearchService searchService = new SongSearchService();

    @FXML
    public void initialize() {
        loadSongsFromDatabase();  // Changed from loadSampleSongs()
        setupSearch();
        setupListView();
        setupPagination();
    }

    private void loadSongsFromDatabase() {
        // Replace with your actual database loading logic
        DatabaseManager dbManager = new DatabaseManager();
        allSongs.setAll(dbManager.getAllSongs());
        filteredSongs.setAll(allSongs);
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                performSearch(newVal);
            } else {
                filteredSongs.clear();
                currentPage.set(1);
            }
        });
    }

    private void setupListView() {
        songsListView.setCellFactory(lv -> new ListCell<>() {
            private final HBox root = new HBox();
            private final Label titleLabel = new Label();
            private final Label artistLabel = new Label();
            private final Button playBtn = new Button();

            {
                VBox textBox = new VBox(titleLabel, artistLabel);
                textBox.setSpacing(2);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                FontIcon playIcon = new FontIcon("fas-play");
                playIcon.setIconSize(16);
                playIcon.setIconColor(Color.web("#d81b60"));
                playBtn.setGraphic(playIcon);
                playBtn.getStyleClass().add("play-icon");
                playBtn.setOnAction(e -> handlePlay(getItem()));

                root.getChildren().addAll(textBox, spacer, playBtn);
                root.setAlignment(Pos.CENTER_LEFT);
                root.getStyleClass().add("song-item");
                root.setSpacing(10);

                root.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                        handlePlay(getItem());
                    }
                });
            }

            @Override
            protected void updateItem(Song song, boolean empty) {
                super.updateItem(song, empty);
                if (empty || song == null) {
                    setGraphic(null);
                } else {
                    titleLabel.setText(song.getTitle());
                    artistLabel.setText(song.getArtist());
                    setGraphic(root);
                }
            }
        });
    }

    private void setupPagination() {
        prevButton.disableProperty().bind(canGoPrevious.not());
        nextButton.disableProperty().bind(canGoNext.not());

        currentPage.addListener((obs, oldVal, newVal) -> updateListView());
        updateListView();
    }

    private void filterSongs(String searchText) {
        filteredSongs.setAll(
                searchText == null || searchText.isEmpty() ?
                        allSongs :
                        allSongs.stream()
                                .filter(song -> song.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                                        song.getArtist().toLowerCase().contains(searchText.toLowerCase()))
                                .collect(Collectors.toList())
        );
        currentPage.set(1);
    }

    private void updateListView() {
        int fromIndex = (currentPage.get() - 1) * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, filteredSongs.size());

        songsListView.setItems(FXCollections.observableArrayList(
                filteredSongs.subList(fromIndex, toIndex)
        ));

        updatePaginationInfo();
    }

    private void updatePaginationInfo() {
        int totalPages = (int) Math.ceil((double) filteredSongs.size() / itemsPerPage);
        pageInfoLabel.setText(String.format("Page %d of %d", currentPage.get(), totalPages));

        canGoPrevious.set(currentPage.get() > 1);
        canGoNext.set(currentPage.get() < totalPages);
    }

    @FXML
    private void handleSearch() {
        performSearch(searchField.getText());
    }

    @FXML
    private void handlePlay(Song song) {
        if (song != null) {
            // Play the song using your media manager
            mediaManager.playSong(song);

            // If you need to navigate to NowPlaying view:
            NavigationController navController = (NavigationController)
                    songsListView.getScene().getRoot().getProperties().get("controller");
            if (navController != null) {
                navController.playSongAndNavigate(song);
            }
        }
    }

    @FXML
    private void previousPage() {
        if (canGoPrevious.get()) {
            currentPage.set(currentPage.get() - 1);
        }
    }

    @FXML
    private void nextPage() {
        if (canGoNext.get()) {
            currentPage.set(currentPage.get() + 1);
        }
    }

    private void performSearch(String query) {
        if (query == null || query.isBlank()) {
            Platform.runLater(() -> {
                filteredSongs.setAll(allSongs);
                currentPage.set(1);
                updateListView(); // ✅ force UI refresh
                System.out.println("Search query is empty. Showing all songs.");
            });
            return;
        }

        executor.execute(() -> {
            try {
                System.out.println("Searching for: " + query);
                List<Song> results = searchService.searchSongs(query);

                Platform.runLater(() -> {
                    filteredSongs.setAll(results);
                    currentPage.set(1);
                    updateListView(); // ✅ force ListView to update
                    System.out.println("Search results: " + results.size() + " songs found.");
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    System.err.println("Error during search: " + e.getMessage());
                });
            }
        });
    }
}