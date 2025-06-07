package com.example.app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NavigationController {

    @FXML private VBox sideNav;
    @FXML private StackPane contentPane;
    @FXML private ToggleButton themeToggle;
    @FXML private FontIcon themeIcon;
    @FXML private Button homeButton;
    @FXML private Button exploreButton;
    @FXML private Button nowPlayingButton;
    @FXML private Button profileButton;
    @FXML private AnchorPane floatingWidgetContainer;

    private FloatingPlayerController floatingPlayerController;
    private NowPlayingController nowPlayingController;
    private Song currentSong;
    private String currentPage = "";

    private final Map<String, Node> screens = new HashMap<>();

    @FXML
    public void initialize() {
        setupThemeToggle();
        loadFloatingPlayer();
        navigateToHome();
    }

    private void loadFloatingPlayer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/app/views/FloatingPlayer.fxml"));
            Node floatingWidget = loader.load();
            floatingPlayerController = loader.getController();
            floatingWidgetContainer.getChildren().add(floatingWidget);
            floatingPlayerController.setVisible(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playSong(Song song) {
        currentSong = song;
        MediaManager.getInstance().playSong(song);

        if (nowPlayingController != null) {
            nowPlayingController.setSong(song);
        }

        if (floatingPlayerController != null) {
            floatingPlayerController.setVisible(true);
            floatingPlayerController.setPlaying(true);
            floatingPlayerController.setAlbumArt(song.getImagePath());
        }

        navigateToNowPlaying();
    }

    private void setScreen(String fxmlPath) {
        try {
            Node screen;

            if (!screens.containsKey(fxmlPath)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                screen = loader.load();

                if (fxmlPath.equals("/com/example/app/views/NowPlaying.fxml")) {
                    nowPlayingController = loader.getController();
                    if (currentSong != null) {
                        nowPlayingController.setSong(currentSong);
                    }
                }

                if (screen instanceof Region region) {
                    region.getStyleClass().add("page-content");
                    region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                }

                screens.put(fxmlPath, screen);
            }

            screen = screens.get(fxmlPath);
            if (contentPane.getChildren().isEmpty() || contentPane.getChildren().get(0) != screen) {
                contentPane.getChildren().setAll(screen);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateActiveButton(String page) {
        homeButton.getStyleClass().remove("active");
        exploreButton.getStyleClass().remove("active");
        nowPlayingButton.getStyleClass().remove("active");
        profileButton.getStyleClass().remove("active");

        switch (page) {
            case "home" -> homeButton.getStyleClass().add("active");
            case "explore" -> exploreButton.getStyleClass().add("active");
            case "nowPlaying" -> nowPlayingButton.getStyleClass().add("active");
            case "profile" -> profileButton.getStyleClass().add("active");
        }

        currentPage = page;
    }

    @FXML
    private void navigateToHome() {
        setScreen("/com/example/app/views/home.fxml");
        updateActiveButton("home");
        if (floatingPlayerController != null && currentSong != null) {
            floatingPlayerController.setVisible(true);
        }
    }

    @FXML
    private void navigateToExplore() {
        setScreen("/com/example/app/views/explore.fxml");
        updateActiveButton("explore");
        if (floatingPlayerController != null && currentSong != null) {
            floatingPlayerController.setVisible(true);
        }
    }

    @FXML
    private void navigateToNowPlaying() {
        if (!"nowPlaying".equals(currentPage)) {
            setScreen("/com/example/app/views/NowPlaying.fxml");
            updateActiveButton("nowPlaying");
        }
        if (floatingPlayerController != null) {
            floatingPlayerController.setVisible(false);
        }
    }

    @FXML
    private void navigateToProfile() {
        setScreen("/com/example/app/views/profile.fxml");
        updateActiveButton("profile");
        if (floatingPlayerController != null && currentSong != null) {
            floatingPlayerController.setVisible(true);
        }
    }

    private void setupThemeToggle() {
        themeToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                themeIcon.setIconLiteral("fas-sun");
                setDarkTheme();
            } else {
                themeIcon.setIconLiteral("fas-moon");
                setLightTheme();
            }
        });
    }

    private void setDarkTheme() {
        contentPane.getScene().getRoot().setStyle("-fx-base: #2b2b2b;");
        sideNav.setStyle("-fx-background-color: #3c3c3c; -fx-border-color: #4d4d4d;");
    }

    private void setLightTheme() {
        contentPane.getScene().getRoot().setStyle("-fx-base: #f5f5f5;");
        sideNav.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;");
    }

    public StackPane getContentPane() {
        return contentPane;
    }
}
