package com.example.app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;

import java.io.File;

public class SongCardController {
    @FXML private VBox root;
    @FXML private ImageView albumArt;
    @FXML private Label songTitle;
    @FXML private Label artistName;
    @FXML private Button playButton;

    private Song song;
    private Runnable playAction;

    /**
     * Original method - maintains backward compatibility
     */
    public void setSongData(String title, String artist, String imagePath) {
        this.song = new Song(title, artist, imagePath);
        updateUI();
        setupClickHandler();

        playButton.setOnAction(e -> {
            playAction.run(); // This should call the fixed playSong method
        });
    }

    /**
     * New method - supports play action callback
     */
    public void setSongData(Song song, Runnable playAction) {
        this.song = song;
        this.playAction = playAction;
        updateUI();
        setupClickHandler();
    }

    private void updateUI() {
        if (song == null) return;

        songTitle.setText(song.getTitle());
        artistName.setText(song.getArtist());

        try {
            if (song.getImagePath() != null && !song.getImagePath().isEmpty()) {
                File file = new File(song.getImagePath());
                if (file.exists()) {
                    albumArt.setImage(new Image(file.toURI().toString()));
                    return;
                }
            }
            // Fallback to default image if specified path is invalid
            albumArt.setImage(loadDefaultImage());
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            albumArt.setImage(loadDefaultImage());
        }
    }

    private Image loadDefaultImage() {
        try {
            return new Image(getClass().getResourceAsStream("/com/example/music/images/music_icon.jpg"));
        } catch (Exception e) {
            System.err.println("Error loading default image: " + e.getMessage());
            return null;
        }
    }

    private void setupClickHandler() {
        root.setOnMouseClicked(this::handleCardClick);
    }

    @FXML
    private void handleCardClick(MouseEvent event) {
        if (event.getClickCount() == 1 && playAction != null) {
            playAction.run();
        }
    }

    @FXML
    private void handlePlayAction() {
        if (playAction != null) {
            playAction.run();
        } else if (song != null) {
            System.out.println("Playing (fallback): " + song.getTitle());
            // Fallback behavior if no playAction was set
        }
    }

    // Helper method to get the current song
    public Song getSong() {
        return song;
    }
}