package cn.langlang.yuweb;

import cn.langlang.yuweb.database.Database;
import cn.langlang.yuweb.database.impl.SQLiteDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
    private Map<String, Database> connections = new HashMap<>();
    private Database defaultDatabase;
    
    public void connect(String type, String path) throws Exception {
        String key = type + ":" + path;
        
        if (connections.containsKey(key)) {
            defaultDatabase = connections.get(key);
            return;
        }
        
        Database db;
        if ("sqlite".equalsIgnoreCase(type)) {
            db = new SQLiteDatabase(path);
        } else {
            throw new IllegalArgumentException("Unsupported database type: " + type);
        }
        
        db.connect();
        connections.put(key, db);
        defaultDatabase = db;
    }
    
    public Database getDefaultDatabase() {
        return defaultDatabase;
    }
    
    public Database getDatabase(String name) {
        return connections.get(name);
    }
    
    public void closeAll() {
        for (Database db : connections.values()) {
            try {
                db.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        connections.clear();
        defaultDatabase = null;
    }
}
