package com.example.app.database;

import com.example.app.models.Song;
import com.example.app.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    // Song-related operations
    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT song_title, artist_name, album_picture, song_file FROM posts";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                songs.add(new Song(
                        rs.getString("song_title"),
                        rs.getString("artist_name"),
                        rs.getString("album_picture"),
                        rs.getString("song_file")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching songs: " + e.getMessage());
            e.printStackTrace();
        }
        return songs;
    }

    public List<Song> getRecentSongs(int limit) {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT song_title, artist_name, album_picture, song_file " +
                "FROM posts ORDER BY created_at DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                songs.add(new Song(
                        rs.getString("song_title"),
                        rs.getString("artist_name"),
                        rs.getString("album_picture"),
                        rs.getString("song_file")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching recent songs: " + e.getMessage());
            e.printStackTrace();
        }
        return songs;
    }

    // Add more methods as needed:
    public boolean addSong(Song song, int userId) {
        String query = "INSERT INTO posts (user_id, song_title, artist_name, album_picture, song_file) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setString(2, song.getTitle());
            stmt.setString(3, song.getArtist());
            stmt.setString(4, song.getImagePath());
            stmt.setString(5, song.getFilePath());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding song: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}