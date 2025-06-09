package com.example.app.database;

import com.example.app.models.Song;
import com.example.app.models.User;
import com.example.app.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.app.utils.PasswordUtils.hashPassword;

public class DatabaseManager {

    // Song-related operations
    // In DatabaseManager.java
    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT user_id, song_title, artist_name, album_picture, song_file FROM posts";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                songs.add(new Song(
                        rs.getString("song_title"),
                        rs.getString("artist_name"),
                        rs.getString("album_picture"),
                        rs.getString("song_file"),
                        rs.getInt("user_id") // Include user ID
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
        String query = "SELECT user_id, song_title, artist_name, album_picture, song_file " +
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
                        rs.getString("song_file"),
                        rs.getInt("user_id")
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

    // In DatabaseManager.java
    public int getUserIdFromSong(String songTitle) {
        String query = "SELECT user_id FROM posts WHERE song_title = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, songTitle);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user ID from song: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // Return -1 if not found
    }

    public User getUserById(int userId) {
        String query = "SELECT id, username, email, profile_pic FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("profile_pic")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public User getUserByUsername(String username) {
        String query = "SELECT id, username, email, profile_pic FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("profile_pic")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getUserPosts(int userId) {
        List<String> posts = new ArrayList<>();
        String query = "SELECT song_title FROM posts WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                posts.add(rs.getString("song_title"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user posts: " + e.getMessage());
            e.printStackTrace();
        }
        return posts;
    }

    // In DatabaseManager.java
    public boolean followUser(int followerId, int followingId) {
        String query = "INSERT INTO followers (follower_id, following_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, followerId);
            stmt.setInt(2, followingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error following user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean unfollowUser(int followerId, int followingId) {
        String query = "DELETE FROM followers WHERE follower_id = ? AND following_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, followerId);
            stmt.setInt(2, followingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error unfollowing user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean isFollowing(int followerId, int followingId) {
        String query = "SELECT 1 FROM followers WHERE follower_id = ? AND following_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, followerId);
            stmt.setInt(2, followingId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking follow status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int getFollowerCount(int userId) {
        String query = "SELECT COUNT(*) FROM followers WHERE following_id = ?";
        return getCount(query, userId);
    }

    public int getFollowingCount(int userId) {
        String query = "SELECT COUNT(*) FROM followers WHERE follower_id = ?";
        return getCount(query, userId);
    }

    private int getCount(String query, int userId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.err.println("Error getting count: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    // DatabaseManager.java
    public User authenticateUser(String username, String password) {
        String query = "SELECT id, username, email, profile_pic FROM users " +
                "WHERE username = ? AND password_hash = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password)); // Implement password hashing

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("profile_pic")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the list of usernames who follow the specified user
     * @param userId The ID of the user whose followers we want
     * @return List of follower usernames
     */
    public List<String> getFollowers(int userId) {
        List<String> followers = new ArrayList<>();
        String query = "SELECT u.username FROM users u " +
                "JOIN followers f ON u.id = f.follower_id " +
                "WHERE f.following_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                followers.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching followers: " + e.getMessage());
            e.printStackTrace();
        }
        return followers;
    }

    /**
     * Gets the list of usernames that the specified user is following
     * @param userId The ID of the user whose following list we want
     * @return List of usernames the user is following
     */
    public List<String> getFollowing(int userId) {
        List<String> following = new ArrayList<>();
        String query = "SELECT u.username FROM users u " +
                "JOIN followers f ON u.id = f.following_id " +
                "WHERE f.follower_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                following.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching following list: " + e.getMessage());
            e.printStackTrace();
        }
        return following;
    }

    public List<Song> searchSongs(String query) {
        // This is a simple implementation - replace with your actual database query
        return getAllSongs().stream()
                .filter(song -> song.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        song.getArtist().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}