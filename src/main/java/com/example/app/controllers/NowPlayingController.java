package com.example.app.controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;

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
    @FXML private Button likeButton;
    @FXML private FontIcon likeIcon;

    private RotateTransition rotateTransition;
    private Timeline progressTimeline;
    private MediaManager mediaManager;

    @FXML
    public void initialize() {
        mediaManager = MediaManager.getInstance();
        setupRotationAnimation();
        setupMediaListeners();
        setupPlayButton();
        setupLikeButton();

        // Set initial state based on current media player
        if (mediaManager.getMediaPlayer() != null) {
            updateUI();
            if (mediaManager.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING) {
                rotateTransition.play();
                startProgressTimer();
            }
        }
    }

    public void setSong(Song song) {
        // Stop current animations and timers
        stopProgressTimer();
        rotateTransition.stop();

        // Update media manager with new song
        mediaManager.playSong(song);

        // Setup new listeners for the new media player
        setupMediaListeners();

        // Update UI with new song data
        updateUI();

        // Start animations if playing
        if (mediaManager.getMediaPlayer() != null &&
                mediaManager.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING) {
            rotateTransition.play();
            startProgressTimer();
        }
    }

    private void updateUI() {
        Song song = mediaManager.getCurrentSong();
        if (song != null) {
            musicTitle.setText(song.getTitle());
            authorName.setText(song.getArtist());

            try {
                Image image = new Image(new File(song.getImagePath()).toURI().toString());
                albumArt.setImage(image);
            } catch (Exception e) {
                albumArt.setImage(new Image(getClass().getResourceAsStream("/images/default_album.png")));
            }

            playIcon.setIconLiteral(mediaManager.getMediaPlayer() != null &&
                    mediaManager.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING ?
                    "fas-pause" : "fas-play");
        }
    }

    private void setupRotationAnimation() {
        rotateTransition = new RotateTransition(Duration.seconds(10), albumArtContainer);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(Animation.INDEFINITE);
        rotateTransition.setInterpolator(Interpolator.LINEAR);

        MediaPlayer player = mediaManager.getMediaPlayer();
        if (player != null && player.getStatus() == MediaPlayer.Status.PLAYING) {
            rotateTransition.play();
        } else {
            rotateTransition.pause();
        }
    }

    private void setupMediaListeners() {
        MediaPlayer player = mediaManager.getMediaPlayer();
        if (player != null) {
            // Clear existing listeners to avoid duplicates
            player.setOnReady(null);
            player.setOnEndOfMedia(null);

            player.setOnReady(() -> {
                totalTimeLabel.setText(formatTime(mediaManager.getTotalDuration().toSeconds()));
                playingSlider.setMax(mediaManager.getTotalDuration().toSeconds());
                playingSlider.setValue(0);
                currentTimeLabel.setText("00:00");
            });

            player.setOnEndOfMedia(() -> {
                playIcon.setIconLiteral("fas-play");
                rotateTransition.pause();
                stopProgressTimer();
                playingSlider.setValue(0);
                currentTimeLabel.setText("00:00");
            });

            player.setOnPlaying(() -> {
                playIcon.setIconLiteral("fas-pause");
                rotateTransition.play();
                startProgressTimer();
            });

            player.setOnPaused(() -> {
                playIcon.setIconLiteral("fas-play");
                rotateTransition.pause();
                stopProgressTimer();
            });
        }
    }

    private void setupPlayButton() {
        playButton.setOnAction(event -> {
            mediaManager.togglePlayPause();
            MediaPlayer.Status status = mediaManager.getMediaPlayer().getStatus();
            playIcon.setIconLiteral(status == MediaPlayer.Status.PLAYING ? "fas-pause" : "fas-play");

            if (status == MediaPlayer.Status.PLAYING) {
                rotateTransition.play();
                startProgressTimer();
            } else {
                rotateTransition.pause();
                stopProgressTimer();
            }
        });
    }

    private void setupLikeButton() {
        likeButton.setOnAction(event -> toggleLike());
    }

    private void toggleLike() {
        likeIcon.setIconLiteral(likeIcon.getIconLiteral().equals("far-star") ? "fas-star" : "far-star");
    }

    private void startProgressTimer() {
        stopProgressTimer();

        progressTimeline = new Timeline(
                new KeyFrame(Duration.millis(100), e -> {
                    currentTimeLabel.setText(formatTime(mediaManager.getCurrentTime().toSeconds()));
                    playingSlider.setValue(mediaManager.getCurrentTime().toSeconds());
                })
        );
        progressTimeline.setCycleCount(Animation.INDEFINITE);
        progressTimeline.play();
    }

    private void stopProgressTimer() {
        if (progressTimeline != null) {
            progressTimeline.stop();
        }
    }

    private String formatTime(double seconds) {
        int minutes = (int) seconds / 60;
        int secs = (int) seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    @FXML
    private void handleAuthorClick(MouseEvent event) {
        try {
            // Get the current song's artist
            Song currentSong = mediaManager.getCurrentSong();
            if (currentSong == null) return;

            // Load the OthersProfile view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/app/views/OthersProfile.fxml"));
            Node othersProfileView = loader.load();

            // Get the controller and pass artist data if needed

            // Get the NavigationController from the current scene
            StackPane contentPane = (StackPane) authorName.getScene().lookup("#contentPane");
            if (contentPane != null) {
                contentPane.getChildren().setAll(othersProfileView);

                // Update navigation state if using NavigationController
                NavigationController navController = (NavigationController)
                        contentPane.getScene().getRoot().getProperties().get("controller");
                if (navController != null) {
                    navController.updateActiveButton("profile");
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading author profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ImageView getAlbumArtImageView() {
        return albumArt; // assuming albumArt is already defined as @FXML ImageView
    }
}