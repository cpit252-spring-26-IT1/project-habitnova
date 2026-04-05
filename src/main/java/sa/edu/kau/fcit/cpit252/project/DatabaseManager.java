package sa.edu.kau.fcit.cpit252.project;

import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
    private static DatabaseManager instance;

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
}
