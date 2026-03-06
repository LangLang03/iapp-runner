package cn.langlang.yuweb.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionPool {
    
    Connection getConnection() throws SQLException;
    
    void releaseConnection(Connection connection);
    
    void closeAll();
    
    int getAvailableConnections();
    
    int getTotalConnections();
    
    int getMaxPoolSize();
}
