package cn.langlang.yuweb.database.impl;

import cn.langlang.yuweb.database.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MySQLConnectionPool implements ConnectionPool {
    private static final Logger logger = LoggerFactory.getLogger(MySQLConnectionPool.class);
    
    private final String url;
    private final String username;
    private final String password;
    private final BlockingQueue<Connection> pool;
    private final AtomicInteger totalConnections;
    private final AtomicInteger activeConnections;
    private final int maxPoolSize;
    private final long connectionTimeout;
    private volatile boolean initialized = false;
    
    public MySQLConnectionPool(String host, int port, String database, String username, String password) {
        this(host, port, database, username, password, 20, 30000, true);
    }
    
    public MySQLConnectionPool(String host, int port, String database, String username, String password, 
                               int maxPoolSize, long connectionTimeout) {
        this(host, port, database, username, password, maxPoolSize, connectionTimeout, true);
    }
    
    public MySQLConnectionPool(String host, int port, String database, String username, String password, 
                               int maxPoolSize, long connectionTimeout, boolean useSSL) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("jdbc:mysql://").append(host).append(":").append(port).append("/").append(database);
        urlBuilder.append("?useSSL=").append(useSSL);
        urlBuilder.append("&serverTimezone=UTC");
        urlBuilder.append("&characterEncoding=UTF-8");
        urlBuilder.append("&cachePrepStmts=true");
        urlBuilder.append("&prepStmtCacheSize=250");
        urlBuilder.append("&prepStmtCacheSqlLimit=2048");
        urlBuilder.append("&useServerPrepStmts=true");
        urlBuilder.append("&useLocalSessionState=true");
        urlBuilder.append("&rewriteBatchedStatements=true");
        urlBuilder.append("&cacheResultSetMetadata=true");
        urlBuilder.append("&cacheResultSetConfiguration=true");
        urlBuilder.append("&connectTimeout=5000");
        urlBuilder.append("&socketTimeout=30000");
        if (useSSL) {
            urlBuilder.append("&requireSSL=true");
        } else {
            urlBuilder.append("&allowPublicKeyRetrieval=true");
        }
        this.url = urlBuilder.toString();
        this.username = username;
        this.password = password;
        this.maxPoolSize = maxPoolSize;
        this.connectionTimeout = connectionTimeout;
        this.pool = new LinkedBlockingQueue<>(maxPoolSize);
        this.totalConnections = new AtomicInteger(0);
        this.activeConnections = new AtomicInteger(0);
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("MySQL JDBC driver not found", e);
        }
        
        logger.info("MySQL connection pool created for: {}@{}:{}/{} (SSL: {}, maxSize: {})", 
                username, host, port, database, useSSL, maxPoolSize);
    }
    
    public synchronized void initialize(int initialSize) {
        if (initialized) {
            return;
        }
        
        for (int i = 0; i < Math.min(initialSize, maxPoolSize); i++) {
            try {
                Connection conn = createNewConnection();
                pool.offer(conn);
                totalConnections.incrementAndGet();
            } catch (SQLException e) {
                logger.error("Failed to create initial connection", e);
            }
        }
        
        initialized = true;
        logger.info("MySQL connection pool initialized with {} connections", pool.size());
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = pool.poll();
        
        if (conn != null) {
            if (isConnectionValid(conn)) {
                activeConnections.incrementAndGet();
                return conn;
            } else {
                totalConnections.decrementAndGet();
                conn = null;
            }
        }
        
        if (totalConnections.get() < maxPoolSize) {
            int current = totalConnections.incrementAndGet();
            if (current <= maxPoolSize) {
                conn = createNewConnection();
                activeConnections.incrementAndGet();
                return conn;
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
        logger.info("MySQL connection pool closed");
    }
    
    @Override
    public int getAvailableConnections() {
        return pool.size();
    }
    
    @Override
    public int getTotalConnections() {
        return totalConnections.get();
    }
    
    @Override
    public int getMaxPoolSize() {
        return maxPoolSize;
    }
    
    public int getActiveConnections() {
        return activeConnections.get();
    }
    
    private Connection createNewConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(url, username, password);
        conn.setAutoCommit(true);
        return conn;
    }
    
    private boolean isConnectionValid(Connection conn) {
        try {
            return conn != null && !conn.isClosed() && conn.isValid(1);
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
    
    public String getPoolStatus() {
        return String.format("MySQLPool{total=%d, active=%d, available=%d, max=%d}",
                totalConnections.get(), activeConnections.get(), pool.size(), maxPoolSize);
    }
}
