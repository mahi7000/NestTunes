package com.example.app.services;

import com.example.app.models.Track;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MusicPlayer {
    private MediaPlayer mediaPlayer;
    private List<Track> playlist;
    private AtomicInteger currentTrackIndex;
    private DoubleProperty volume;
    private boolean isPlaying;
    private boolean isShuffle;
    private boolean isRepeat;

    public MusicPlayer() {
        this.playlist = new ArrayList<>();
        this.currentTrackIndex = new AtomicInteger(-1);
        this.volume = new SimpleDoubleProperty(1.0);
        this.isPlaying = false;
        this.isShuffle = false;
        this.isRepeat = false;
    }

    public void loadTrack(Track track) {
        if (track == null || track.getFile() == null) return;

        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }

        try {
            Media media = new Media(track.getFile().toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(volume.get());
            
            // Set up end of media handler
            mediaPlayer.setOnEndOfMedia(() -> {
                if (isRepeat) {
                    mediaPlayer.seek(Duration.ZERO);
                    mediaPlayer.play();
                } else {
                    playNext();
                }
            });

            // Set up error handler
            mediaPlayer.setOnError(() -> {
                System.err.println("Error playing media: " + mediaPlayer.getError().getMessage());
                playNext();
            });

        } catch (Exception e) {
            System.err.println("Error loading track: " + e.getMessage());
        }
    }

    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
            isPlaying = true;
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPlaying = false;
        }
    }

    public void playNext() {
        if (playlist.isEmpty()) return;

        int nextIndex;
        if (isShuffle) {
            nextIndex = (int) (Math.random() * playlist.size());
        } else {
            nextIndex = (currentTrackIndex.get() + 1) % playlist.size();
        }

        currentTrackIndex.set(nextIndex);
        loadTrack(playlist.get(nextIndex));
        play();
    }

    public void playPrevious() {
        if (playlist.isEmpty()) return;

        int prevIndex;
        if (isShuffle) {
            prevIndex = (int) (Math.random() * playlist.size());
        } else {
            prevIndex = (currentTrackIndex.get() - 1 + playlist.size()) % playlist.size();
        }

        currentTrackIndex.set(prevIndex);
        loadTrack(playlist.get(prevIndex));
        play();
    }

    public void seek(Duration duration) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(duration);
        }
    }

    public void setVolume(double volume) {
        this.volume.set(Math.max(0.0, Math.min(1.0, volume)));
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(this.volume.get());
        }
    }

    public DoubleProperty volumeProperty() {
        return volume;
    }

    public void setPlaylist(List<Track> tracks) {
        this.playlist = new ArrayList<>(tracks);
        this.currentTrackIndex.set(-1);
        if (!tracks.isEmpty()) {
            currentTrackIndex.set(0);
            loadTrack(tracks.get(0));
        }
    }

    public void toggleShuffle() {
        isShuffle = !isShuffle;
    }

    public void toggleRepeat() {
        isRepeat = !isRepeat;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isShuffle() {
        return isShuffle;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public Duration getCurrentTime() {
        return mediaPlayer != null ? mediaPlayer.getCurrentTime() : Duration.ZERO;
    }

    public Duration getTotalDuration() {
        return mediaPlayer != null ? mediaPlayer.getTotalDuration() : Duration.ZERO;
    }

    public void dispose() {
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }
    }
} 