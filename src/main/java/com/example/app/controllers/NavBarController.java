package com.example.app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.kordamp.ikonli.javafx.FontIcon;

public class NavBarController {
    @FXML
    private Button homeButton;
    
    @FXML
    private Button libraryButton;
    
    @FXML
    private Button playlistsButton;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private Button profileButton;
    
    @FXML
    private Label usernameLabel;
    
    private MainController mainController;
    
    public void setMainController(MainController controller) {
        this.mainController = controller;
    }
    
    public void setUsername(String username) {
        usernameLabel.setText(username);
    }
    
    @FXML
    public void initialize() {
        // Set up icons
        homeButton.setGraphic(new FontIcon("fas-home"));
        libraryButton.setGraphic(new FontIcon("fas-music"));
        playlistsButton.setGraphic(new FontIcon("fas-list"));
        searchButton.setGraphic(new FontIcon("fas-search"));
        profileButton.setGraphic(new FontIcon("fas-user"));
        
        // Add hover effects
        addHoverEffect(homeButton);
        addHoverEffect(libraryButton);
        addHoverEffect(playlistsButton);
        addHoverEffect(searchButton);
        addHoverEffect(profileButton);
    }
    
    private void addHoverEffect(Button button) {
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #282828;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent;"));
    }
    
    @FXML
    private void handleHomeClick() {
        if (mainController != null) {
            mainController.loadView("home");
        }
    }
    
    @FXML
    private void handleLibraryClick() {
        if (mainController != null) {
            mainController.loadView("library");
        }
    }
    
    @FXML
    private void handlePlaylistsClick() {
        if (mainController != null) {
            mainController.loadView("playlists");
        }
    }
    
    @FXML
    private void handleSearchClick() {
        if (mainController != null) {
            mainController.loadView("search");
        }
    }
    
    @FXML
    private void handleProfileClick() {
        if (mainController != null) {
            mainController.loadView("profile");
        }
    }
} 