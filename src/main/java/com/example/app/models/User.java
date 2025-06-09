package com.example.app.models;

public class User {
    private int id;
    private String username;
    private String email;
    private String profilePic;
    private int followerCount;
    private int followingCount;
    private boolean isFollowing;

    public User(int id, String username, String email, String profilePic) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profilePic = profilePic;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getProfilePic() { return profilePic; }
}