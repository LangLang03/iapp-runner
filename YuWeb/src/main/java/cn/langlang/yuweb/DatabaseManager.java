package cn.langlang.yuweb;

import cn.langlang.yuweb.database.ConnectionPool;
import cn.langlang.yuweb.database.Database;
import cn.langlang.yuweb.database.impl.MySQLConnectionPool;
import cn.langlang.yuweb.database.impl.MySQLDatabase;
import cn.langlang.yuweb.database.impl.SQLiteDatabase;
import cn.langlang.yuweb.database.impl.SQLiteConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    
    private final Map<String, Database> connections = new ConcurrentHashMap<>();
    private final Map<String, ConnectionPool> connectionPools = new ConcurrentHashMap<>();
    private Database defaultDatabase;
    private String defaultKey;
    
    private boolean useConnectionPool = true;
    private int maxPoolSize = 10;
    private long connectionTimeout = 30000;
    
    public void connect(String type, String path) throws Exception {
        String key = type + ":" + path;
        
        if (connections.containsKey(key)) {
            defaultDatabase = connections.get(key);
            defaultKey = key;
            return;
        }
        
        Database db;
        if ("sqlite".equalsIgnoreCase(type)) {
            if (useConnectionPool) {
                SQLiteConnectionPool pool = new SQLiteConnectionPool(path, maxPoolSize, connectionTimeout);
                connectionPools.put(key, pool);
            }
            db = new SQLiteDatabase(path);
        } else if ("mysql".equalsIgnoreCase(type)) {
            db = new MySQLDatabase(path);
        } else {
            throw new IllegalArgumentException("Unsupported database type: " + type);
        }
        
        db.connect();
        connections.put(key, db);
        defaultDatabase = db;
        defaultKey = key;
        
        logger.info("Database connected: {}", key);
    }
    
    public void connectMySQL(String host, int port, String database, String username, String password) throws Exception {
        String key = "mysql:" + host + ":" + port + "/" + database;
        
        if (connections.containsKey(key)) {
            defaultDatabase = connections.get(key);
            defaultKey = key;
            return;
        }
        
        if (useConnectionPool) {
            MySQLConnectionPool pool = new MySQLConnectionPool(host, port, database, username, password, 
                    maxPoolSize, connectionTimeout);
            connectionPools.put(key, pool);
        }
        
        MySQLDatabase db = new MySQLDatabase(host, port, database, username, password);
        db.connect();
        connections.put(key, db);
        defaultDatabase = db;
        defaultKey = key;
        
        logger.info("MySQL database connected: {}", key);
    }
    
    public Database getDefaultDatabase() {
        return defaultDatabase;
    }
    
    public Database getDatabase(String name) {
        return connections.get(name);
    }
    
    public Connection getConnection() throws SQLException {
        if (defaultKey == null) {
            throw new SQLException("No database connected");
        }
        
        ConnectionPool pool = connectionPools.get(defaultKey);
        if (pool != null) {
            return pool.getConnection();
        }
        
        throw new SQLException("No connection pool available");
    }
    
    public Connection getConnection(String key) throws SQLException {
        ConnectionPool pool = connectionPools.get(key);
        if (pool != null) {
            return pool.getConnection();
        }
        
        throw new SQLException("No connection pool available for: " + key);
    }
    
    public void releaseConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        
        for (ConnectionPool pool : connectionPools.values()) {
            pool.releaseConnection(connection);
            return;
        }
        
        try {
            connection.close();
        } catch (SQLException e) {
            logger.error("Error closing connection", e);
        }
    }
    
    public void closeAll() {
        for (ConnectionPool pool : connectionPools.values()) {
            pool.closeAll();
        }
        connectionPools.clear();
        
        for (Database db : connections.values()) {
            try {
                db.disconnect();
            } catch (Exception e) {
                logger.error("Error closing database connection: {}", e.getMessage());
            }
        }
        connections.clear();
        defaultDatabase = null;
        defaultKey = null;
        
        logger.info("All database connections closed");
    }
    
    public void setUseConnectionPool(boolean useConnectionPool) {
        this.useConnectionPool = useConnectionPool;
    }
    
    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }
    
    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    public Map<String, ConnectionPool> getConnectionPools() {
        return connectionPools;
    }
    
    public int getTotalPoolConnections() {
        int total = 0;
        for (ConnectionPool pool : connectionPools.values()) {
            total += pool.getTotalConnections();
        }
        return total;
    }
    
    public int getAvailablePoolConnections() {
        int total = 0;
        for (ConnectionPool pool : connectionPools.values()) {
            total += pool.getAvailableConnections();
        }
        return total;
    }
}
