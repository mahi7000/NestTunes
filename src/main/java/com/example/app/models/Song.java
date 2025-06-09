package com.example.app.models;

public class Song {
    private final String title;
    private final String artist;
    private final String imagePath;
    private final String filePath;

    public Song(String title, String artist, String imagePath) {
        this(title, artist, imagePath, null);
    }


    public Song(String title, String artist, String imagePath, String filePath) {
        this.title = title;
        this.artist = artist;
        this.imagePath = imagePath;
        this.filePath = filePath;
    }

    // Getters
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getImagePath() { return imagePath; }
    public String getFilePath() { return filePath; }
}