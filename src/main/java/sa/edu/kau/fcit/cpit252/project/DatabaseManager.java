package sa.edu.kau.fcit.cpit252.project;

import java.util.HashMap;
import java.util.Map;

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
        if (usersDb.containsKey(username)) {
            return false;
        }
        usersDb.put(username, password);
        return true;
    }
}