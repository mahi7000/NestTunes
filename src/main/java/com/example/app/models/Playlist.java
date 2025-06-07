package com.example.app.models;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private String id;
    private String name;
    private String userId;
    private List<Track> tracks;

    public Playlist(String id, String name, String userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.tracks = new ArrayList<>();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<Track> getTracks() { return tracks; }
    public void setTracks(List<Track> tracks) { this.tracks = tracks; }

    public void addTrack(Track track) {
        tracks.add(track);
    }

    public void removeTrack(Track track) {
        tracks.remove(track);
    }

    @Override
    public String toString() {
        return name;
    }
} 