package com.example.app.utils;

public class Session {
    private static String username;
    private static String email;
    private static String profilePic;
    private static int userId;

    public static void setUser(String uname, String mail, String pic, int id) {
        username = uname;
        email = mail;
        profilePic = pic;
        userId = id;
    }

    public static void setUsername(String uname) {
        username = uname;
    }

    public static String getUsername() {
        return username;
    }

    public static String setEmail() {
        return email;
    }
    public static String getEmail() {
        return email;
    }

    public static String getProfilePic() {
        return profilePic;
    }

    public static void setUserId(int id) {
        userId = id;
    }

    public static int getUserId() {
        return userId;
    }

    public static void clearSession() {
        username = null;
        email = null;
        profilePic = null;
        userId = -1;
    }

}
