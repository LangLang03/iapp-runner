package cn.langlang.yuweb.database;

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
    private final Map<Connection, String> connectionSourceMap = new ConcurrentHashMap<>();
    private Database defaultDatabase;
    private String defaultKey;
    
    private boolean useConnectionPool = true;
    private int maxPoolSize = 100;
    private int initialPoolSize = 10;
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
            SQLiteConnectionPool pool = null;
            if (useConnectionPool) {
                pool = new SQLiteConnectionPool(path, maxPoolSize, connectionTimeout);
                pool.initialize(initialPoolSize);
                connectionPools.put(key, pool);
                logger.info("SQLite connection pool initialized: size={}", maxPoolSize);
            }
            db = new SQLiteDatabase(path);
            if (pool != null) {
                ((SQLiteDatabase) db).setConnectionPool(pool);
            }
        } else if ("mysql".equalsIgnoreCase(type)) {
            MySQLDatabase mysqlDb = new MySQLDatabase(path);
            if (useConnectionPool) {
                MySQLConnectionPool mysqlPool = new MySQLConnectionPool(
                        mysqlDb.getHost(), mysqlDb.getPort(), mysqlDb.getDatabase(),
                        mysqlDb.getUsername(), mysqlDb.getPassword(),
                        maxPoolSize, connectionTimeout, mysqlDb.isUseSSL());
                mysqlPool.initialize(initialPoolSize);
                connectionPools.put(key, mysqlPool);
                mysqlDb.setConnectionPool(mysqlPool);
                logger.info("MySQL connection pool initialized: size={}", maxPoolSize);
            }
            db = mysqlDb;
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
        
        MySQLConnectionPool pool = null;
        if (useConnectionPool) {
            pool = new MySQLConnectionPool(host, port, database, username, password, 
                    maxPoolSize, connectionTimeout);
            pool.initialize(initialPoolSize);
            connectionPools.put(key, pool);
            logger.info("MySQL connection pool initialized: size={}", maxPoolSize);
        }
        
        MySQLDatabase db = new MySQLDatabase(host, port, database, username, password);
        if (pool != null) {
            db.setConnectionPool(pool);
        }
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
            Connection conn = pool.getConnection();
            connectionSourceMap.put(conn, defaultKey);
            return conn;
        }
        
        throw new SQLException("No connection pool available");
    }
    
    public Connection getConnection(String key) throws SQLException {
        ConnectionPool pool = connectionPools.get(key);
        if (pool != null) {
            Connection conn = pool.getConnection();
            connectionSourceMap.put(conn, key);
            return conn;
        }
        
        throw new SQLException("No connection pool available for: " + key);
    }
    
    public void releaseConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        
        String sourceKey = connectionSourceMap.remove(connection);
        if (sourceKey != null) {
            ConnectionPool pool = connectionPools.get(sourceKey);
            if (pool != null) {
                pool.releaseConnection(connection);
                return;
            }
        }
        
        try {
            connection.close();
            logger.debug("Connection closed directly (no pool found)");
        } catch (SQLException e) {
            logger.error("Error closing connection", e);
        }
    }
    
    public void closeAll() {
        connectionSourceMap.clear();
        
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
        this.maxPoolSize = maxPoolSize > 0 ? maxPoolSize : 100;
    }
    
    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize > 0 ? initialPoolSize : 10;
    }
    
    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout > 0 ? connectionTimeout : 30000;
    }
    
    public void configureFromConfig(cn.langlang.yuweb.YuWebConfig config) {
        this.useConnectionPool = config.isUseConnectionPool();
        this.maxPoolSize = config.getMaxPoolSize();
        this.initialPoolSize = config.getInitialPoolSize();
        this.connectionTimeout = config.getConnectionTimeout();
        logger.info("DatabaseManager configured: poolSize={}, initialSize={}, timeout={}ms", 
                maxPoolSize, initialPoolSize, connectionTimeout);
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
    
    public int getActiveConnectionsCount() {
        return connectionSourceMap.size();
    }
    
    public PoolStats getPoolStats() {
        int total = 0, available = 0, active = 0;
        for (ConnectionPool pool : connectionPools.values()) {
            total += pool.getTotalConnections();
            available += pool.getAvailableConnections();
        }
        active = connectionSourceMap.size();
        return new PoolStats(total, available, active, maxPoolSize);
    }
    
    public static class PoolStats {
        private final int total;
        private final int available;
        private final int active;
        private final int maxSize;
        
        public PoolStats(int total, int available, int active, int maxSize) {
            this.total = total;
            this.available = available;
            this.active = active;
            this.maxSize = maxSize;
        }
        
        public int getTotal() { return total; }
        public int getAvailable() { return available; }
        public int getActive() { return active; }
        public int getMaxSize() { return maxSize; }
        
        @Override
        public String toString() {
            return String.format("PoolStats{total=%d, available=%d, active=%d, maxSize=%d}", 
                    total, available, active, maxSize);
        }
    }
}
