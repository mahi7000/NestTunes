package com.example.app.services;

import com.example.app.models.Track;
import com.example.app.models.Playlist;
import com.example.app.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseService {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ap";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static DatabaseService instance;
    private Connection connection;

    private DatabaseService() {
        initializeDatabase();
    }

    public static synchronized DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            // First connect to MySQL server to create database if it doesn't exist
            Connection tempConn = DriverManager.getConnection("jdbc:mysql://localhost:3306", USER, PASSWORD);
            try (Statement stmt = tempConn.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS ap");
            }
            tempConn.close();

            // Now connect to the ap database
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            createTables();
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            // Users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(255) NOT NULL,
                    email VARCHAR(255) NOT NULL UNIQUE,
                    password_hash VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    profile_pic VARCHAR(255)
                )
            """);

            // Tracks table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tracks (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    artist VARCHAR(255) NOT NULL,
                    album VARCHAR(255) NOT NULL,
                    duration BIGINT NOT NULL,
                    file_path VARCHAR(255) NOT NULL,
                    album_art_path VARCHAR(255),
                    user_id INT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);

            // Playlists table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS playlists (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    user_id INT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);

            // Playlist tracks table (junction table)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS playlist_tracks (
                    playlist_id INT NOT NULL,
                    track_id INT NOT NULL,
                    position INT NOT NULL,
                    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    PRIMARY KEY (playlist_id, track_id),
                    FOREIGN KEY (playlist_id) REFERENCES playlists(id),
                    FOREIGN KEY (track_id) REFERENCES tracks(id)
                )
            """);

        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    // User operations
    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, email, password_hash, profile_pic) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setString(4, null); // profile_pic
            pstmt.executeUpdate();
            
            // Get the generated ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(String.valueOf(rs.getInt(1)));
                }
            }
        }
    }

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    String.valueOf(rs.getInt("id")),
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    rs.getString("email")
                );
            }
        }
        return null;
    }

    // Track operations
    public void saveTrack(Track track) throws SQLException {
        String sql = "INSERT INTO tracks (title, artist, album, duration, file_path, album_art_path, user_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, track.getTitle());
            pstmt.setString(2, track.getArtist());
            pstmt.setString(3, track.getAlbum());
            pstmt.setLong(4, track.getDuration().toMillis());
            pstmt.setString(5, track.getFile().getAbsolutePath());
            pstmt.setString(6, track.getAlbumArtPath());
            pstmt.setInt(7, Integer.parseInt(track.getUserId()));
            pstmt.executeUpdate();
            
            // Get the generated ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    track.setId(String.valueOf(rs.getInt(1)));
                }
            }
        }
    }

    public List<Track> getTracksByUser(String userId) throws SQLException {
        List<Track> tracks = new ArrayList<>();
        String sql = "SELECT * FROM tracks WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(userId));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tracks.add(new Track(
                    String.valueOf(rs.getInt("id")),
                    rs.getString("title"),
                    rs.getString("artist"),
                    rs.getString("album"),
                    java.time.Duration.ofMillis(rs.getLong("duration")),
                    new java.io.File(rs.getString("file_path")),
                    rs.getString("album_art_path"),
                    String.valueOf(rs.getInt("user_id"))
                ));
            }
        }
        return tracks;
    }

    // Playlist operations
    public void createPlaylist(Playlist playlist) throws SQLException {
        String sql = "INSERT INTO playlists (name, user_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, playlist.getName());
            pstmt.setInt(2, Integer.parseInt(playlist.getUserId()));
            pstmt.executeUpdate();
            
            // Get the generated ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    playlist.setId(String.valueOf(rs.getInt(1)));
                }
            }
        }
    }

    public void addTrackToPlaylist(String playlistId, String trackId, int position) throws SQLException {
        String sql = "INSERT INTO playlist_tracks (playlist_id, track_id, position) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(playlistId));
            pstmt.setInt(2, Integer.parseInt(trackId));
            pstmt.setInt(3, position);
            pstmt.executeUpdate();
        }
    }

    public List<Playlist> getPlaylistsByUser(String userId) throws SQLException {
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT * FROM playlists WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(userId));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                playlists.add(new Playlist(
                    String.valueOf(rs.getInt("id")),
                    rs.getString("name"),
                    String.valueOf(rs.getInt("user_id"))
                ));
            }
        }
        return playlists;
    }

    public List<Track> getTracksInPlaylist(String playlistId) throws SQLException {
        List<Track> tracks = new ArrayList<>();
        String sql = """
            SELECT t.* FROM tracks t
            JOIN playlist_tracks pt ON t.id = pt.track_id
            WHERE pt.playlist_id = ?
            ORDER BY pt.position
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(playlistId));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tracks.add(new Track(
                    String.valueOf(rs.getInt("id")),
                    rs.getString("title"),
                    rs.getString("artist"),
                    rs.getString("album"),
                    java.time.Duration.ofMillis(rs.getLong("duration")),
                    new java.io.File(rs.getString("file_path")),
                    rs.getString("album_art_path"),
                    String.valueOf(rs.getInt("user_id"))
                ));
            }
        }
        return tracks;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
} 