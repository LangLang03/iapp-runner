package cn.langlang.yuweb.database;

import java.util.List;
import java.util.Map;

public interface Database {
    void connect() throws DatabaseException;
    void disconnect();
    boolean isConnected();
    
    long insert(String table, Map<String, Object> data) throws DatabaseException;
    int update(String table, Map<String, Object> data, Object condition) throws DatabaseException;
    int delete(String table, Object condition) throws DatabaseException;
    
    Map<String, Object> findOne(String table, Object condition) throws DatabaseException;
    List<Map<String, Object>> findAll(String table, Object condition) throws DatabaseException;
    Map<String, Object> findPage(String table, Object condition, int page, int size) throws DatabaseException;
    long count(String table, Object condition) throws DatabaseException;
    
    Map<String, Object> search(String table, Object fields, String keyword, int page, int size) throws DatabaseException;
    
    void execute(String sql) throws DatabaseException;
    void execute(String sql, Object... params) throws DatabaseException;
    
    List<Map<String, Object>> query(String sql) throws DatabaseException;
    List<Map<String, Object>> query(String sql, Object... params) throws DatabaseException;
}
