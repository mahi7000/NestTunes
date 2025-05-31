package com.example.app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class NavigationController {
//    @FXML private StackPane contentPane;
//    @FXML private Button homeButton;
//    @FXML private Button exploreButton;
//    @FXML private Button nowPlayingButton;
//    @FXML private Button profileButton;
//
//    private Button currentActiveButton;
//
//    @FXML
//    public void initialize() {
//        loadHome();
//        setActiveButton(homeButton);
//    }
//
//    @FXML
//    public void loadHome() {
//        loadPage("/application/views/Home.fxml");
//        setActiveButton(homeButton);
//    }
//
//    @FXML
//    public void loadExplore() {
//        loadPage("/application/views/Explore.fxml");
//        setActiveButton(exploreButton);
//    }
//
//    @FXML
//    public void loadNowPlaying() {
//        loadPage("/application/views/NowPlaying.fxml");
//        setActiveButton(nowPlayingButton);
//    }
//
//    @FXML
//    public void loadProfile() {
//        loadPage("/application/views/Profile.fxml");
//        setActiveButton(profileButton);
//    }
//
//    private void setActiveButton(Button button) {
//        // Remove active style from previous button
//        if (currentActiveButton != null) {
//            currentActiveButton.getStyleClass().remove("active");
//        }
//
//        // Add active style to new button
//        button.getStyleClass().add("active");
//        currentActiveButton = button;
//    }
//
//    private void loadPage(String fxmlPath) {
//        try {
//            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
//            contentPane.getChildren().setAll(view);
//        } catch (IOException e) {
//            showErrorAlert("Failed to load page: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private void showErrorAlert(String message) {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setTitle("Navigation Error");
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
}
