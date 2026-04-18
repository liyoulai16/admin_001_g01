package com.community.context;

public class LoginUserContext {
    
    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();
    
    public static void setCurrentUser(String username) {
        currentUser.set(username);
    }
    
    public static String getCurrentUser() {
        return currentUser.get();
    }
    
    public static void clear() {
        currentUser.remove();
    }
}
