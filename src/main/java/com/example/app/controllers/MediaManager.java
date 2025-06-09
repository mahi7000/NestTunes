package com.example.app.controllers;

import com.example.app.models.Song;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;

public class MediaManager {
    private static MediaManager instance;
    private MediaPlayer mediaPlayer;
    private Song currentSong;

    private MediaManager() {}

    public static synchronized MediaManager getInstance() {
        if (instance == null) {
            instance = new MediaManager();
        }
        return instance;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public void playSong(Song song) {
        stopCurrentMedia();

        this.currentSong = song;
        Media media = new Media(new File(song.getFilePath()).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    }

    public void togglePlayPause() {
        if (mediaPlayer != null) {
            if (isPlaying()) {  // If currently playing
                mediaPlayer.pause();  // Pause it
            } else {  // If paused or stopped
                mediaPlayer.play();  // Play it
            }
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        }
    }

    public void resume() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            mediaPlayer.play();
        }
    }


    public void stopCurrentMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public Duration getCurrentTime() {
        return mediaPlayer != null ? mediaPlayer.getCurrentTime() : Duration.ZERO;
    }

    public Duration getTotalDuration() {
        return mediaPlayer != null && mediaPlayer.getMedia() != null ?
                mediaPlayer.getMedia().getDuration() : Duration.ZERO;
    }
}