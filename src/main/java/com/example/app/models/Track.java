package com.example.app.models;

import java.io.File;
import java.time.Duration;

public class Track {
    private String id;
    private String title;
    private String artist;
    private String album;
    private Duration duration;
    private File file;
    private String albumArtPath;
    private String userId; // ID of the user who uploaded the track

    public Track(String id, String title, String artist, String album, Duration duration, File file, String albumArtPath, String userId) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.file = file;
        this.albumArtPath = albumArtPath;
        this.userId = userId;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }

    public Duration getDuration() { return duration; }
    public void setDuration(Duration duration) { this.duration = duration; }

    public File getFile() { return file; }
    public void setFile(File file) { this.file = file; }

    public String getAlbumArtPath() { return albumArtPath; }
    public void setAlbumArtPath(String albumArtPath) { this.albumArtPath = albumArtPath; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    @Override
    public String toString() {
        return title + " - " + artist;
    }
} 