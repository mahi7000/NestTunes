package com.example.app.models;

import java.io.Serializable;

public class Song implements Serializable {
    private final String title;
    private final String artist;
    private final String imagePath;
    private final String filePath;
    private int userId;

    public Song(String title, String artist, String imagePath) {
        this(title, artist, imagePath, null, 1);
    }


    public Song(String title, String artist, String imagePath, String filePath, int userId) {
        this.title = title;
        this.artist = artist;
        this.imagePath = imagePath;
        this.filePath = filePath;
        this.userId = userId;
    }

    // Getters
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getImagePath() { return imagePath; }
    public String getFilePath() { return filePath; }
}