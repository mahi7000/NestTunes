package com.example.app.services;

import com.example.app.models.Track;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class MetadataService {
    private static final String ALBUM_ART_DIR = "data/album_art";

    public MetadataService() {
        // Create album art directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(ALBUM_ART_DIR));
        } catch (Exception e) {
            System.err.println("Error creating album art directory: " + e.getMessage());
        }
    }

    public Track extractMetadata(File audioFile) {
        try {
            Media media = new Media(audioFile.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            
            // Wait for metadata to be loaded
            mediaPlayer.setOnReady(() -> {
                mediaPlayer.dispose();
            });

            // Extract basic metadata
            String title = media.getMetadata().get("title") != null ? 
                media.getMetadata().get("title").toString() : 
                audioFile.getName().replaceFirst("[.][^.]+$", "");
            
            String artist = media.getMetadata().get("artist") != null ? 
                media.getMetadata().get("artist").toString() : 
                "Unknown Artist";
            
            String album = media.getMetadata().get("album") != null ? 
                media.getMetadata().get("album").toString() : 
                "Unknown Album";

            // Get duration and convert from JavaFX Duration to java.time.Duration
            long durationMillis = (long) media.getDuration().toMillis();
            java.time.Duration duration = java.time.Duration.ofMillis(durationMillis);

            return new Track(
                UUID.randomUUID().toString(),
                title,
                artist,
                album,
                duration,
                audioFile,
                null, // No album art support in basic version
                null  // userId will be set when the track is uploaded
            );

        } catch (Exception e) {
            System.err.println("Error extracting metadata from " + audioFile.getName() + ": " + e.getMessage());
            // Return a basic track with filename as title
            return new Track(
                UUID.randomUUID().toString(),
                audioFile.getName().replaceFirst("[.][^.]+$", ""),
                "Unknown Artist",
                "Unknown Album",
                java.time.Duration.ZERO,
                audioFile,
                null,
                null
            );
        }
    }

    public void updateMetadata(Track track, String title, String artist, String album) {
        // Note: JavaFX Media doesn't support writing metadata
        // We'll just update the track object
        track.setTitle(title);
        track.setArtist(artist);
        track.setAlbum(album);
    }
} 