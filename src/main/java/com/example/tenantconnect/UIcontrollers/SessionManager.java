package com.example.tenantconnect.UIcontrollers;

/**
 * Manages the current user session (e.g., who is logged in).
 * This uses the Singleton pattern to ensure a single instance.
 */
public class SessionManager {
    private static SessionManager instance;
    private int loggedInUserId = -1; // -1 indicates no user is logged in

    private SessionManager() {
        // Private constructor to prevent direct instantiation
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(int userId) {
        this.loggedInUserId = userId;
        System.out.println("User " + userId + " logged in.");
    }

    public void logout() {
        this.loggedInUserId = -1;
        System.out.println("User logged out.");
    }

    public int getLoggedInUserId() {
        return loggedInUserId;
    }

    public boolean isLoggedIn() {
        return loggedInUserId != -1;
    }
}