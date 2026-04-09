package com.HabitNova;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Map<String, String> usersDb;

    private DatabaseManager() {
        usersDb = new HashMap<>();
        System.out.println("System: Initializing the Single Database Instance...");
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public boolean registerUser(String username, String password) {
        if (userDb.containsKey(username)) {
            return false; // User already exists
        }
        userDb.put(username, password);
        return true;
    }
}
