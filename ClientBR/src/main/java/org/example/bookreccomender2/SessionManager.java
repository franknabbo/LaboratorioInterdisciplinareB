package org.example.bookreccomender2;

public class SessionManager {
    private static boolean loggedIn = false;
    private static String userId = null;

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static String getUserId() {
        return userId;
    }

    public static void login(String userId) {
        SessionManager.loggedIn = true;
        SessionManager.userId = userId;
    }

    public static void logout() {

        SessionManager.loggedIn = false;
        SessionManager.userId = null;
    }
}