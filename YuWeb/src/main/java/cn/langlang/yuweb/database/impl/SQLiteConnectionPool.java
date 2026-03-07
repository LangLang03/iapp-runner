package cn.langlang.yuweb.database.impl;

import cn.langlang.yuweb.database.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SQLiteConnectionPool implements ConnectionPool {
    private static final Logger logger = LoggerFactory.getLogger(SQLiteConnectionPool.class);
    
    private final String path;
    private final BlockingQueue<Connection> pool;
    private final AtomicInteger totalConnections;
    private final AtomicInteger activeConnections;
    private final int maxPoolSize;
    private final long connectionTimeout;
    private volatile boolean initialized = false;
    
    private static final String PRAGMA_WAL = "PRAGMA journal_mode=WAL";
    private static final String PRAGMA_BUSY_TIMEOUT = "PRAGMA busy_timeout=10000";
    private static final String PRAGMA_SYNCHRONOUS = "PRAGMA synchronous=NORMAL";
    private static final String PRAGMA_CACHE_SIZE = "PRAGMA cache_size=-64000";
    private static final String PRAGMA_FOREIGN_KEYS = "PRAGMA foreign_keys=ON";
    
    public SQLiteConnectionPool(String path) {
        this(path, 50, 5000);
    }
    
    public SQLiteConnectionPool(String path, int maxPoolSize, long connectionTimeout) {
        this.path = path;
        this.maxPoolSize = maxPoolSize;
        this.connectionTimeout = connectionTimeout;
        this.pool = new LinkedBlockingQueue<>(maxPoolSize);
        this.totalConnections = new AtomicInteger(0);
        this.activeConnections = new AtomicInteger(0);
        
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            logger.error("SQLite JDBC driver not found", e);
        }
        
        logger.info("SQLite connection pool created for: {} (max size: {})", path, maxPoolSize);
    }
    
    public synchronized void initialize(int initialSize) {
        if (initialized) {
            return;
        }
        
        for (int i = 0; i < Math.min(initialSize, maxPoolSize); i++) {
            try {
                Connection conn = createNewConnection();
                pool.offer(conn);
            } catch (SQLException e) {
                logger.error("Failed to create initial connection", e);
            }
        }
        
        initialized = true;
        logger.info("SQLite connection pool initialized with {} connections", pool.size());
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = pool.poll();
        
        if (conn != null && isConnectionValid(conn)) {
            activeConnections.incrementAndGet();
            return conn;
        } else if (conn != null) {
            totalConnections.decrementAndGet();
            conn = null;
        }
        
        if (totalConnections.get() < maxPoolSize) {
            int current = totalConnections.incrementAndGet();
            if (current <= maxPoolSize) {
                activeConnections.incrementAndGet();
                return createNewConnection();
            } else {
                totalConnections.decrementAndGet();
            }
        }
        
        try {
            conn = pool.poll(connectionTimeout, TimeUnit.MILLISECONDS);
            if (conn != null && isConnectionValid(conn)) {
                activeConnections.incrementAndGet();
                return conn;
            } else if (conn != null) {
                totalConnections.decrementAndGet();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        throw new SQLException("Connection pool exhausted (active: " + activeConnections.get() + 
                ", total: " + totalConnections.get() + ", max: " + maxPoolSize + ")");
    }
    
    @Override
    public void releaseConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        
        activeConnections.decrementAndGet();
        
        try {
            if (!connection.isClosed() && isConnectionValid(connection)) {
                if (!pool.offer(connection)) {
                    closeConnection(connection);
                    totalConnections.decrementAndGet();
                }
            } else {
                totalConnections.decrementAndGet();
            }
        } catch (SQLException e) {
            logger.error("Error releasing connection", e);
            totalConnections.decrementAndGet();
        }
    }
    
    @Override
    public void closeAll() {
        Connection conn;
        while ((conn = pool.poll()) != null) {
            closeConnection(conn);
        }
        totalConnections.set(0);
        activeConnections.set(0);
        initialized = false;
        logger.info("SQLite connection pool closed for: {}", path);
    }
    
    @Override
    public int getAvailableConnections() {
        return pool.size();
    }
    
    @Override
    public int getTotalConnections() {
        return totalConnections.get();
    }
    
    public int getActiveConnections() {
        return activeConnections.get();
    }
    
    @Override
    public int getMaxPoolSize() {
        return maxPoolSize;
    }
    
    public PoolStats getStats() {
        return new PoolStats(pool.size(), totalConnections.get(), activeConnections.get(), maxPoolSize);
    }
    
    private Connection createNewConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path);
        configureConnection(conn);
        return conn;
    }
    
    private void configureConnection(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(PRAGMA_WAL);
            stmt.execute(PRAGMA_BUSY_TIMEOUT);
            stmt.execute(PRAGMA_SYNCHRONOUS);
            stmt.execute(PRAGMA_CACHE_SIZE);
            stmt.execute(PRAGMA_FOREIGN_KEYS);
        }
        conn.setAutoCommit(true);
    }
    
    private boolean isConnectionValid(Connection conn) {
        try {
            if (conn == null || conn.isClosed()) {
                return false;
            }
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SELECT 1");
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    private void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.error("Error closing connection", e);
        }
    }
    
    public static class PoolStats {
        private final int available;
        private final int total;
        private final int active;
        private final int maxSize;
        
        public PoolStats(int available, int total, int active, int maxSize) {
            this.available = available;
            this.total = total;
            this.active = active;
            this.maxSize = maxSize;
        }
        
        public int getAvailable() {
            return available;
        }
        
        public int getTotal() {
            return total;
        }
        
        public int getActive() {
            return active;
        }
        
        public int getMaxSize() {
            return maxSize;
        }
        
        @Override
        public String toString() {
            return String.format("PoolStats{available=%d, total=%d, active=%d, maxSize=%d}", 
                    available, total, active, maxSize);
        }
    }
}
