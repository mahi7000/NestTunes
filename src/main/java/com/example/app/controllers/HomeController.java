package com.example.app.controllers;

import com.example.app.models.Song;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.example.app.database.DatabaseManager;

public class HomeController {
    private static final String MUSIC_FOLDER = "C:\\Users\\HP\\Documents\\Education\\Year 3\\Semester 2\\Advanced Programming\\Code\\app\\src\\main\\resources\\com\\example\\app\\sounds";
    private static final String DEFAULT_IMAGE = "C:\\Users\\HP\\Documents\\Education\\Year 3\\Semester 2\\Advanced Programming\\Code\\app\\src\\main\\resources\\com\\example\\app\\images\\music_icon.jpg";
    private static final String[] SUPPORTED_FORMATS = {".mp3", ".wav", ".m4a", ".flac"};

    // Library pagination
    @FXML private GridPane librarySongsContainer;
    @FXML private Label libraryPageLabel;
    @FXML private Button libraryPrevBtn;
    @FXML private Button libraryNextBtn;
    private List<Song> allLibrarySongs = new ArrayList<>();
    private int currentLibraryPage = 0;
    private final int LIBRARY_PAGE_SIZE = 6;

    // Recent songs pagination
    @FXML private GridPane recentSongsContainer;
    @FXML private Label recentPageLabel;
    @FXML private Button recentPrevBtn;
    @FXML private Button recentNextBtn;
    private List<Song> allRecentSongs = new ArrayList<>();
    private int currentRecentPage = 0;
    private final int RECENT_PAGE_SIZE = 6;
    private final DatabaseManager dbManager = new DatabaseManager();

    @FXML
    public void initialize() {
        loadSongsFromDatabase();
        updateLibraryPagination();
        updateRecentPagination();
    }

    private void loadSongsFromDatabase() {
        allLibrarySongs = dbManager.getAllSongs();
        allRecentSongs = dbManager.getRecentSongs(10); // Get 7 most recent songs
    }

    private void loadAllSongs() {
        DatabaseManager dbManager = new DatabaseManager();
        allLibrarySongs = dbManager.getAllSongs();
    }

    private boolean isSupportedFormat(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        for (String format : SUPPORTED_FORMATS) {
            if (fileName.endsWith(format)) {
                return true;
            }
        }
        return false;
    }

    private Song createSongFromFile(Path filePath, int userId) {
        String fileName = filePath.getFileName().toString();
        String songName = fileName.substring(0, fileName.lastIndexOf('.'));

        return new Song(
                songName,
                "Unknown Artist",  // Fixed typo in "Unknown"
                DEFAULT_IMAGE,
                filePath.toString(),
                userId
        );
    }

    private void loadAllRecentSongs() {
        int recentCount = Math.min(7, allLibrarySongs.size());
        allRecentSongs = new ArrayList<>(allLibrarySongs.subList(0, recentCount));
    }

    // Library pagination methods
    @FXML
    public void nextLibraryPage() {
        int maxPage = (int) Math.ceil((double) allLibrarySongs.size() / LIBRARY_PAGE_SIZE) - 1;
        if (currentLibraryPage < maxPage) {
            currentLibraryPage++;
            updateLibraryPagination();
        }
    }

    @FXML
    public void previousLibraryPage() {
        if (currentLibraryPage > 0) {
            currentLibraryPage--;
            updateLibraryPagination();
        }
    }

    private void updateLibraryPagination() {
        librarySongsContainer.getChildren().clear();

        int start = currentLibraryPage * LIBRARY_PAGE_SIZE;
        int end = Math.min(start + LIBRARY_PAGE_SIZE, allLibrarySongs.size());

        int row = 0, col = 0;
        for (int i = start; i < end; i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/app/views/SongCard.fxml"));
                VBox songCard = loader.load();
                SongCardController controller = loader.getController();

                // Pass the song and a callback to handle playback
                Song currentSong = allLibrarySongs.get(i);
                controller.setSongData(
                        currentSong,
                        () -> playSong(currentSong)
                );

                librarySongsContainer.add(songCard, col, row);

                col++;
                if (col >= 3) {
                    col = 0;
                    row++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Update pagination controls
        libraryPageLabel.setText("Page " + (currentLibraryPage + 1));
        libraryPrevBtn.setDisable(currentLibraryPage == 0);
        libraryNextBtn.setDisable((currentLibraryPage + 1) * LIBRARY_PAGE_SIZE >= allLibrarySongs.size());
    }

    private void playSong(Song song) {
        // Get the root node from any component in the scene
        Node node = librarySongsContainer; // or recentSongsContainer
        Scene scene = node.getScene();

        // Look for the navigation controller in the scene
        NavigationController navController = (NavigationController) scene.getRoot().getProperties().get("controller");

        if (navController != null) {
            navController.playSongAndNavigate(song);
        } else {
            System.err.println("NavigationController not found!");
        }
    }

    // Recent songs pagination methods (similar to library)
    @FXML
    public void nextRecentPage() {
        int maxPage = (int) Math.ceil((double) allRecentSongs.size() / RECENT_PAGE_SIZE) - 1;
        if (currentRecentPage < maxPage) {
            currentRecentPage++;
            updateRecentPagination();
        }
    }

    @FXML
    public void previousRecentPage() {
        if (currentRecentPage > 0) {
            currentRecentPage--;
            updateRecentPagination();
        }
    }

    private void updateRecentPagination() {
        recentSongsContainer.getChildren().clear();

        int start = currentRecentPage * RECENT_PAGE_SIZE;
        int end = Math.min(start + RECENT_PAGE_SIZE, allRecentSongs.size());

        int row = 0, col = 0;
        for (int i = start; i < end; i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/app/views/SongCard.fxml"));
                VBox songCard = loader.load();
                SongCardController controller = loader.getController();
                controller.setSongData(
                        allRecentSongs.get(i).getTitle(),
                        allRecentSongs.get(i).getArtist(),
                        allRecentSongs.get(i).getImagePath()
                );

                recentSongsContainer.add(songCard, col, row);

                col++;
                if (col >= 3) {
                    col = 0;
                    row++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        recentPageLabel.setText("Page " + (currentRecentPage + 1));
        recentPrevBtn.setDisable(currentRecentPage == 0);
        recentNextBtn.setDisable((currentRecentPage + 1) * RECENT_PAGE_SIZE >= allRecentSongs.size());
    }
}