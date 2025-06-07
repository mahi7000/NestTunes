package com.example.app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.javafx.FontIcon;

public class MainController {
    @FXML
    private StackPane contentArea;
    
    @FXML
    private Label nowPlayingLabel;
    
    @FXML
    private Button previousButton;
    
    @FXML
    private Button playPauseButton;
    
    @FXML
    private Button nextButton;
    
    @FXML
    private Slider volumeSlider;
    
    @FXML
    private Slider progressSlider;
    
    @FXML
    public void initialize() {
        // Set up player control icons
        FontIcon previousIcon = new FontIcon("fas-step-backward");
        previousIcon.setIconSize(16);
        previousButton.setGraphic(previousIcon);
        
        FontIcon playIcon = new FontIcon("fas-play");
        playIcon.setIconSize(16);
        playPauseButton.setGraphic(playIcon);
        
        FontIcon nextIcon = new FontIcon("fas-step-forward");
        nextIcon.setIconSize(16);
        nextButton.setGraphic(nextIcon);
        
        // Initialize volume slider
        volumeSlider.setValue(100);
        
        // Add listeners
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            // TODO: Update volume
        });
        
        progressSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            // TODO: Update playback position
        });
    }
    
    public void loadView(String viewName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/app/views/" + viewName + ".fxml"));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: Show error dialog
        }
    }
    
    @FXML
    private void handlePlayPause() {
        // TODO: Implement play/pause functionality
        FontIcon icon = (FontIcon) playPauseButton.getGraphic();
        if (icon.getIconLiteral().equals("fas-play")) {
            icon.setIconLiteral("fas-pause");
        } else {
            icon.setIconLiteral("fas-play");
        }
    }
    
    @FXML
    private void handlePrevious() {
        // TODO: Implement previous track functionality
    }
    
    @FXML
    private void handleNext() {
        // TODO: Implement next track functionality
    }
    
    public void updateNowPlaying(String title, String artist) {
        nowPlayingLabel.setText(title + " - " + artist);
    }
} 