package com.example.app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NavigationController {
    @FXML private VBox sideNav;
    @FXML private StackPane contentPane;
    @FXML private ToggleButton themeToggle;
    @FXML private FontIcon themeIcon;

    private final Map<String, Node> screens = new HashMap<>();

    @FXML
    public void initialize() {
        setupThemeToggle();
        navigateToHome();
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

    private void setScreen(String fxmlPath) {
        try {
            if (!screens.containsKey(fxmlPath)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Node screen = loader.load();

                if (screen instanceof Region) {
                    Region region = (Region) screen;
                    region.getStyleClass().add("page-content");
                    region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                }

                screens.put(fxmlPath, screen);
            }

            contentPane.getChildren().setAll(screens.get(fxmlPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Navigation methods
    @FXML private void navigateToHome() { setScreen("/com/example/app/views/home.fxml"); }
    @FXML private void navigateToExplore() { setScreen("/com/example/app/views/explore.fxml"); }
    @FXML private void navigateToNowPlaying() { setScreen("/com/example/app/views/nowplaying.fxml"); }
    @FXML private void navigateToProfile() { setScreen("/com/example/app/views/profile.fxml"); }

    // Theme methods
    private void setDarkTheme() {
        contentPane.getScene().getRoot().setStyle("-fx-base: #2b2b2b;");
        sideNav.setStyle("-fx-background-color: #3c3c3c; -fx-border-color: #4d4d4d;");
    }

    private void setLightTheme() {
        contentPane.getScene().getRoot().setStyle("-fx-base: #f5f5f5;");
        sideNav.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;");
    }
}