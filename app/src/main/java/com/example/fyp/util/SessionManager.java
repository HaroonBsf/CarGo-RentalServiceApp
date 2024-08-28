package com.example.fyp.util;

public class SessionManager {
    private static SessionManager instance;
    private boolean isLoggedIn;
    private String userId;

    // Private constructor to prevent instantiation
    private SessionManager() {
    }

    // Method to get the single instance of the class
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Method to set the login status
    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    // Method to check if the user is logged in
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    // Method to set the user ID
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Method to get the user ID
    public String getUserId() {
        return userId;
    }
}
