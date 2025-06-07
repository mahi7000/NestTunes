package com.example.app.controllers;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

public class FloatingPlayerController {

    @FXML private StackPane floatingPlayer;
    @FXML private StackPane albumArtContainer;
    @FXML private ImageView miniAlbumArt;
    @FXML private StackPane playPauseButton;
    @FXML private FontIcon playPauseIcon;

    private boolean isPlaying = true;
    private RotateTransition rotateTransition;

    @FXML
    private void initialize() {
        // Rotation animation setup
        rotateTransition = new RotateTransition(Duration.seconds(10), albumArtContainer);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
        rotateTransition.setInterpolator(javafx.animation.Interpolator.LINEAR);

        // Start rotating by default
        rotateTransition.play();

        // Toggle playback when button clicked
        playPauseButton.setOnMouseClicked(event -> togglePlayback());

        // Optional: navigate to now playing on click
        floatingPlayer.setOnMouseClicked(event -> {
            // e.g., call NavigationController.navigateToNowPlaying()
        });
    }

    public void togglePlayback() {
        if (isPlaying) {
            pause();
        } else {
            play();
        }
    }

    public void play() {
        isPlaying = true;
        playPauseIcon.setIconLiteral("fas-pause");
        rotateTransition.play();

        // Optional: notify MediaManager
         MediaManager.getInstance().resume();
    }

    public void pause() {
        isPlaying = false;
        playPauseIcon.setIconLiteral("fas-play");
        rotateTransition.pause();

        // Optional: notify MediaManager
         MediaManager.getInstance().pause();
    }

    public void setAlbumArt(Image image) {
        miniAlbumArt.setImage(image);
    }

    // When setting album art from a file path:
    public void setAlbumArt(String imagePath) {
        try {
            Image image;
            if (imagePath != null && !imagePath.isEmpty()) {
                image = new Image("file:" + imagePath);
            } else {
                image = new Image(getClass().getResource("/com/example/app/images/music_icon.jpg").toExternalForm());
            }
            miniAlbumArt.setImage(image);
        } catch (Exception e) {
            System.err.println("Failed to load album art: " + imagePath);
            e.printStackTrace();
        }
    }


    public void setVisible(boolean visible) {
        floatingPlayer.setVisible(visible);
    }

    public void setPlaying(boolean playing) {
        if (playing) {
            play();
        } else {
            pause();
        }
    }

    public ImageView getMiniAlbumArt() {
        return miniAlbumArt;
    }
}
