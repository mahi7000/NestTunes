package com.example.app.controllers;

import com.example.app.models.User;

// CurrentUserSession.java
public class CurrentUserSession {
    private static CurrentUserSession instance;
    private User currentUser;

    private CurrentUserSession() {} // Private constructor

    public static CurrentUserSession getInstance() {
        if (instance == null) {
            instance = new CurrentUserSession();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public int getUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }

    public void clearSession() {
        currentUser = null;
    }
}