package com.example.app.utils;

public class Session {
    private static String username;
    private static String email;
    private static String profilePic;

    public static void setUser(String uname, String mail, String pic) {
        username = uname;
        email = mail;
        profilePic = pic;
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

    public static void clearSession() {
        username = null;
        email = null;
        profilePic = null;
    }

    public static Object getInstance() {
        throw new UnsupportedOperationException("Unimplemented method 'getInstance'");
    }
}
