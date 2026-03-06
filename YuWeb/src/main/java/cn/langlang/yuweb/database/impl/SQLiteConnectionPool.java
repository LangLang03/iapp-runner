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

public class SQLiteConnectionPool implements ConnectionPool {
    private static final Logger logger = LoggerFactory.getLogger(SQLiteConnectionPool.class);
    
    private final String path;
    private final BlockingQueue<Connection> pool;
    private final AtomicInteger totalConnections;
    private final int maxPoolSize;
    private final long connectionTimeout;
    
    public SQLiteConnectionPool(String path) {
        this(path, 10, 30000);
    }
    
    public SQLiteConnectionPool(String path, int maxPoolSize, long connectionTimeout) {
        this.path = path;
        this.maxPoolSize = maxPoolSize;
        this.connectionTimeout = connectionTimeout;
        this.pool = new LinkedBlockingQueue<>(maxPoolSize);
        this.totalConnections = new AtomicInteger(0);
        
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            logger.error("SQLite JDBC driver not found", e);
        }
        
        logger.info("SQLite connection pool created for: {}", path);
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = pool.poll();
        
        if (conn != null) {
            if (isConnectionValid(conn)) {
                return conn;
            } else {
                totalConnections.decrementAndGet();
                conn = null;
            }
        }
        
        if (totalConnections.get() < maxPoolSize) {
            int current = totalConnections.incrementAndGet();
            if (current <= maxPoolSize) {
                return createNewConnection();
            } else {
                totalConnections.decrementAndGet();
            }
        }
        
        try {
            conn = pool.poll(connectionTimeout, TimeUnit.MILLISECONDS);
            if (conn != null && isConnectionValid(conn)) {
                return conn;
            } else if (conn != null) {
                totalConnections.decrementAndGet();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        throw new SQLException("Connection pool exhausted");
    }
    
    @Override
    public void releaseConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        
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
    
    @Override
    public int getMaxPoolSize() {
        return maxPoolSize;
    }
    
    private Connection createNewConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path);
        conn.setAutoCommit(true);
        return conn;
    }
    
    private boolean isConnectionValid(Connection conn) {
        try {
            return conn != null && !conn.isClosed();
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
}
