package com.example.app.controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.*;

public class NowPlayingController {
    @FXML private Label musicTitle;
    @FXML private Label authorName;
    @FXML private ImageView albumArt;
    @FXML private Slider playingSlider;
    @FXML private StackPane albumArtContainer;
    @FXML private Button playButton;
    @FXML private FontIcon playIcon;
    @FXML private Label currentTimeLabel;
    @FXML private Label totalTimeLabel;
    @FXML private Button backButton;

    private MediaPlayer mediaPlayer;
    private RotateTransition rotateTransition;
    private Timeline progressTimeline;
    private boolean isPlaying = false;
    private Song currentSong;

    @FXML
    public void initialize() {
        setupRotationAnimation();
        setupPlayPauseToggle();
        setupBackButton();
    }

    public void setSong(Song song) {
        this.currentSong = song;

        // Update UI with song data
        musicTitle.setText(song.getTitle());
        authorName.setText(song.getArtist());

        try {
            Image image = new Image(new File(song.getImagePath()).toURI().toString());
            albumArt.setImage(image);
        } catch (Exception e) {
            albumArt.setImage(new Image(getClass().getResourceAsStream("/com/example/app/images/music_icon.jpg")));
        }

        // Initialize media player
        initializeMediaPlayer(song.getFilePath());
    }

    private void initializeMediaPlayer(String filePath) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        try {
            Media media = new Media(new File(filePath).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            // Set up media player event handlers
            mediaPlayer.setOnReady(() -> {
                Duration duration = mediaPlayer.getMedia().getDuration();
                totalTimeLabel.setText(formatTime(duration.toSeconds()));
                playingSlider.setMax(duration.toSeconds());
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                playIcon.setIconLiteral("fas-play");
                isPlaying = false;
                rotateTransition.pause();
                progressTimeline.stop();
            });

            // Start playback automatically
            play();
        } catch (Exception e) {
            System.err.println("Error loading media: " + e.getMessage());
        }
    }

    private void setupRotationAnimation() {
        rotateTransition = new RotateTransition(Duration.seconds(10), albumArtContainer);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(Animation.INDEFINITE);
        rotateTransition.setInterpolator(Interpolator.LINEAR);
        rotateTransition.pause();
    }

    private void setupPlayPauseToggle() {
        playButton.setOnAction(event -> togglePlayPause());
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) return;

        if (isPlaying) {
            pause();
        } else {
            play();
        }
    }

    private void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
            playIcon.setIconLiteral("fas-pause");
            rotateTransition.play();
            startProgressTimer();
            isPlaying = true;
        }
    }

    private void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            playIcon.setIconLiteral("fas-play");
            rotateTransition.pause();
            progressTimeline.stop();
            isPlaying = false;
        }
    }

    private void startProgressTimer() {
        if (progressTimeline != null) {
            progressTimeline.stop();
        }

        progressTimeline = new Timeline(
                new KeyFrame(Duration.millis(100), event -> {
                    if (mediaPlayer != null) {
                        Duration currentTime = mediaPlayer.getCurrentTime();
                        currentTimeLabel.setText(formatTime(currentTime.toSeconds()));
                        playingSlider.setValue(currentTime.toSeconds());
                    }
                })
        );
        progressTimeline.setCycleCount(Animation.INDEFINITE);
        progressTimeline.play();
    }

    private void setupBackButton() {
        backButton.setOnAction(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            // Return to home screen
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/music/views/Home.fxml"));
                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private String formatTime(double seconds) {
        int minutes = (int) seconds / 60;
        int secs = (int) seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }
}