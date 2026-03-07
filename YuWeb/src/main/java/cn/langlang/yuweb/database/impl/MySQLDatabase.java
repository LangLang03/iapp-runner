package cn.langlang.yuweb.database.impl;

import cn.langlang.yuweb.database.ConnectionPool;
import cn.langlang.yuweb.database.Database;
import cn.langlang.yuweb.database.DatabaseException;
import cn.langlang.yuweb.database.QueryCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class MySQLDatabase implements Database {
    private static final Logger logger = LoggerFactory.getLogger(MySQLDatabase.class);
    
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private String charset;
    private boolean useSSL;
    private ConnectionPool connectionPool;
    private Connection maintenanceConnection;
    
    public MySQLDatabase(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.charset = "utf8mb4";
        this.useSSL = true;
    }
    
    public MySQLDatabase(String connectionString) {
        parseConnectionString(connectionString);
    }
    
    public void setConnectionPool(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
    
    private void parseConnectionString(String connectionString) {
        this.host = "localhost";
        this.port = 3306;
        this.charset = "UTF-8";
        this.useSSL = true;
        
        int atIndex = connectionString.indexOf("@");
        if (atIndex > 0) {
            String userPass = connectionString.substring(0, atIndex);
            if (userPass.startsWith("mysql://")) {
                userPass = userPass.substring(8);
            }
            String[] up = userPass.split(":", 2);
            this.username = up[0];
            this.password = up.length > 1 ? up[1] : "";
        }
        
        String hostAndDb = connectionString;
        if (atIndex > 0) {
            hostAndDb = connectionString.substring(atIndex + 1);
        } else if (hostAndDb.startsWith("mysql://")) {
            hostAndDb = hostAndDb.substring(8);
        }
        
        String[] parts = hostAndDb.split("/", 2);
        if (parts.length >= 2) {
            String hostPort = parts[0];
            this.database = parts[1].split("[?]")[0];
            
            if (hostPort.contains(":")) {
                int lastColon = hostPort.lastIndexOf(":");
                this.host = hostPort.substring(0, lastColon);
                this.port = Integer.parseInt(hostPort.substring(lastColon + 1));
            } else {
                this.host = hostPort;
            }
            
            if (parts[1].contains("?")) {
                String queryString = parts[1].split("[?]")[1];
                for (String param : queryString.split("&")) {
                    String[] kv = param.split("=");
                    if (kv.length == 2) {
                        if ("charset".equalsIgnoreCase(kv[0])) {
                            this.charset = kv[1];
                        } else if ("useSSL".equalsIgnoreCase(kv[0])) {
                            this.useSSL = Boolean.parseBoolean(kv[1]);
                        }
                    }
                }
            }
        }
    }
    
    public void setCharset(String charset) {
        this.charset = charset;
    }
    
    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }
    
    public String getDatabase() {
        return database;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public boolean isUseSSL() {
        return useSSL;
    }
    
    @Override
    public void connect() throws DatabaseException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(String.format("jdbc:mysql://%s:%d/%s?", host, port, database));
            urlBuilder.append("useSSL=").append(useSSL);
            urlBuilder.append("&serverTimezone=UTC");
            urlBuilder.append("&characterEncoding=").append(charset);
            urlBuilder.append("&cachePrepStmts=true");
            urlBuilder.append("&prepStmtCacheSize=250");
            urlBuilder.append("&prepStmtCacheSqlLimit=2048");
            urlBuilder.append("&useServerPrepStmts=true");
            urlBuilder.append("&useLocalSessionState=true");
            urlBuilder.append("&rewriteBatchedStatements=true");
            urlBuilder.append("&cacheResultSetMetadata=true");
            urlBuilder.append("&cacheResultSetConfiguration=true");
            if (useSSL) {
                urlBuilder.append("&requireSSL=true");
            } else {
                urlBuilder.append("&allowPublicKeyRetrieval=true");
            }
            
            maintenanceConnection = DriverManager.getConnection(urlBuilder.toString(), username, password);
            maintenanceConnection.setAutoCommit(true);
            
            logger.info("Connected to MySQL database: {}@{}:{}/{} (SSL: {})", 
                    username, host, port, database, useSSL);
        } catch (ClassNotFoundException | SQLException e) {
            throw new DatabaseException("Failed to connect to MySQL: " + e.getMessage(), e);
        }
    }
    
    private Connection getConnection() throws SQLException {
        if (connectionPool != null) {
            return connectionPool.getConnection();
        }
        return maintenanceConnection;
    }
    
    private void releaseConnection(Connection conn) {
        if (connectionPool != null && conn != null) {
            connectionPool.releaseConnection(conn);
        }
    }
    
    @Override
    public void disconnect() {
        if (maintenanceConnection != null) {
            try {
                maintenanceConnection.close();
                logger.info("Disconnected from MySQL database");
            } catch (SQLException e) {
                logger.error("Error closing MySQL connection: {}", e.getMessage());
            }
            maintenanceConnection = null;
        }
    }
    
    @Override
    public boolean isConnected() {
        try {
            return maintenanceConnection != null && !maintenanceConnection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    @Override
    public long insert(String table, Map<String, Object> data) throws DatabaseException {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(table).append(" (");
        
        StringBuilder placeholders = new StringBuilder();
        List<Object> values = new ArrayList<>();
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getValue() != null) {
                if (!first) {
                    sql.append(", ");
                    placeholders.append(", ");
                }
                sql.append(entry.getKey());
                placeholders.append("?");
                values.add(entry.getValue());
                first = false;
            }
        }
        
        sql.append(") VALUES (").append(placeholders).append(")");
        
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < values.size(); i++) {
                    stmt.setObject(i + 1, values.get(i));
                }
                
                stmt.executeUpdate();
                
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return -1;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Insert failed: " + e.getMessage(), e);
        } finally {
            releaseConnection(conn);
        }
    }
    
    @Override
    public int update(String table, Map<String, Object> data, Object condition) throws DatabaseException {
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(table).append(" SET ");
        
        List<Object> values = new ArrayList<>();
        boolean first = true;
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) {
                sql.append(", ");
            }
            sql.append(entry.getKey()).append(" = ?");
            values.add(entry.getValue());
            first = false;
        }
        
        String whereClause = buildWhereClause(condition, values);
        sql.append(" WHERE ").append(whereClause);
        
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < values.size(); i++) {
                    stmt.setObject(i + 1, values.get(i));
                }
                return stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Update failed: " + e.getMessage(), e);
        } finally {
            releaseConnection(conn);
        }
    }
    
    @Override
    public int delete(String table, Object condition) throws DatabaseException {
        StringBuilder sql = new StringBuilder("DELETE FROM ");
        sql.append(table);
        
        List<Object> values = new ArrayList<>();
        String whereClause = buildWhereClause(condition, values);
        sql.append(" WHERE ").append(whereClause);
        
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < values.size(); i++) {
                    stmt.setObject(i + 1, values.get(i));
                }
                return stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Delete failed: " + e.getMessage(), e);
        } finally {
            releaseConnection(conn);
        }
    }
    
    @Override
    public Map<String, Object> findOne(String table, Object condition) throws DatabaseException {
        StringBuilder sql = new StringBuilder("SELECT * FROM ");
        sql.append(table);
        
        List<Object> values = new ArrayList<>();
        if (condition != null) {
            String whereClause = buildWhereClause(condition, values);
            sql.append(" WHERE ").append(whereClause);
        }
        
        sql.append(" LIMIT 1");
        
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < values.size(); i++) {
                    stmt.setObject(i + 1, values.get(i));
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return resultSetToMap(rs);
                    }
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Query failed: " + e.getMessage(), e);
        } finally {
            releaseConnection(conn);
        }
    }
    
    @Override
    public List<Map<String, Object>> findAll(String table, Object condition) throws DatabaseException {
        StringBuilder sql = new StringBuilder("SELECT * FROM ");
        sql.append(table);
        
        List<Object> values = new ArrayList<>();
        if (condition != null) {
            String whereClause = buildWhereClause(condition, values);
            sql.append(" WHERE ").append(whereClause);
        }
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < values.size(); i++) {
                    stmt.setObject(i + 1, values.get(i));
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        results.add(resultSetToMap(rs));
                    }
                }
                return results;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Query failed: " + e.getMessage(), e);
        } finally {
            releaseConnection(conn);
        }
    }
    
    @Override
    public Map<String, Object> findPage(String table, Object condition, int page, int size) throws DatabaseException {
        int offset = (page - 1) * size;
        
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM ");
        countSql.append(table);
        
        StringBuilder dataSql = new StringBuilder("SELECT * FROM ");
        dataSql.append(table);
        
        List<Object> values = new ArrayList<>();
        if (condition != null) {
            String whereClause = buildWhereClause(condition, values);
            countSql.append(" WHERE ").append(whereClause);
            dataSql.append(" WHERE ").append(whereClause);
        }
        
        dataSql.append(" LIMIT ?, ?");
        
        Map<String, Object> result = new HashMap<>();
        
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement countStmt = conn.prepareStatement(countSql.toString())) {
                for (int i = 0; i < values.size(); i++) {
                    countStmt.setObject(i + 1, values.get(i));
                }
                try (ResultSet rs = countStmt.executeQuery()) {
                    long total = rs.next() ? rs.getLong(1) : 0;
                    result.put("total", total);
                    result.put("page", page);
                    result.put("size", size);
                    result.put("totalPages", (total + size - 1) / size);
                }
            }
            
            List<Map<String, Object>> data = new ArrayList<>();
            try (PreparedStatement dataStmt = conn.prepareStatement(dataSql.toString())) {
                int paramIndex = 1;
                for (Object value : values) {
                    dataStmt.setObject(paramIndex++, value);
                }
                dataStmt.setInt(paramIndex++, offset);
                dataStmt.setInt(paramIndex, size);
                
                try (ResultSet rs = dataStmt.executeQuery()) {
                    while (rs.next()) {
                        data.add(resultSetToMap(rs));
                    }
                }
            }
            result.put("data", data);
            
            return result;
        } catch (SQLException e) {
            throw new DatabaseException("Page query failed: " + e.getMessage(), e);
        } finally {
            releaseConnection(conn);
        }
    }
    
    @Override
    public long count(String table, Object condition) throws DatabaseException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ");
        sql.append(table);
        
        List<Object> values = new ArrayList<>();
        if (condition != null) {
            String whereClause = buildWhereClause(condition, values);
            sql.append(" WHERE ").append(whereClause);
        }
        
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < values.size(); i++) {
                    stmt.setObject(i + 1, values.get(i));
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Count failed: " + e.getMessage(), e);
        } finally {
            releaseConnection(conn);
        }
    }
    
    @Override
    public Map<String, Object> search(String table, Object fields, String keyword, int page, int size) throws DatabaseException {
        int offset = (page - 1) * size;
        
        List<String> searchFields = new ArrayList<>();
        if (fields instanceof String) {
            String fieldStr = (String) fields;
            if (fieldStr.contains(",")) {
                for (String f : fieldStr.split(",")) {
                    searchFields.add(f.trim());
                }
            } else {
                searchFields.add(fieldStr.trim());
            }
        } else if (fields instanceof List) {
            for (Object f : (List<?>) fields) {
                if (f != null) {
                    searchFields.add(f.toString().trim());
                }
            }
        }
        
        if (searchFields.isEmpty()) {
            throw new DatabaseException("Search fields cannot be empty");
        }
        
        String searchPattern = "%" + keyword + "%";
        
        StringBuilder whereClause = new StringBuilder();
        List<Object> values = new ArrayList<>();
        
        for (int i = 0; i < searchFields.size(); i++) {
            if (i > 0) {
                whereClause.append(" OR ");
            }
            whereClause.append(searchFields.get(i)).append(" LIKE ?");
            values.add(searchPattern);
        }
        
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM ");
        countSql.append(table).append(" WHERE (").append(whereClause).append(")");
        
        StringBuilder dataSql = new StringBuilder("SELECT * FROM ");
        dataSql.append(table).append(" WHERE (").append(whereClause).append(") LIMIT ?, ?");
        
        Map<String, Object> result = new HashMap<>();
        
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement countStmt = conn.prepareStatement(countSql.toString())) {
                for (int i = 0; i < values.size(); i++) {
                    countStmt.setObject(i + 1, values.get(i));
                }
                try (ResultSet rs = countStmt.executeQuery()) {
                    long total = rs.next() ? rs.getLong(1) : 0;
                    result.put("total", total);
                    result.put("page", page);
                    result.put("size", size);
                    result.put("totalPages", (total + size - 1) / size);
                    result.put("keyword", keyword);
                }
            }
            
            List<Map<String, Object>> data = new ArrayList<>();
            try (PreparedStatement dataStmt = conn.prepareStatement(dataSql.toString())) {
                int paramIndex = 1;
                for (Object value : values) {
                    dataStmt.setObject(paramIndex++, value);
                }
                dataStmt.setInt(paramIndex++, offset);
                dataStmt.setInt(paramIndex, size);
                
                try (ResultSet rs = dataStmt.executeQuery()) {
                    while (rs.next()) {
                        data.add(resultSetToMap(rs));
                    }
                }
            }
            result.put("data", data);
            
            return result;
        } catch (SQLException e) {
            throw new DatabaseException("Search failed: " + e.getMessage(), e);
        } finally {
            releaseConnection(conn);
        }
    }
    
    @Override
    public void execute(String sql) throws DatabaseException {
        if (sql == null || sql.trim().isEmpty()) {
            throw new DatabaseException("SQL statement cannot be empty");
        }
        Connection conn = null;
        try {
            conn = getConnection();
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Execute failed: " + e.getMessage(), e);
        } finally {
            releaseConnection(conn);
        }
    }
    
    @Override
    public void execute(String sql, Object... params) throws DatabaseException {
        if (sql == null || sql.trim().isEmpty()) {
            throw new DatabaseException("SQL statement cannot be empty");
        }
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                stmt.execute();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Execute failed: " + e.getMessage(), e);
        } finally {
            releaseConnection(conn);
        }
    }
    
    @Override
    public List<Map<String, Object>> query(String sql) throws DatabaseException {
        if (sql == null || sql.trim().isEmpty()) {
            throw new DatabaseException("SQL statement cannot be empty");
        }
        List<Map<String, Object>> results = new ArrayList<>();
        Connection conn = null;
        try {
            conn = getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    results.add(resultSetToMap(rs));
                }
            }
            return results;
        } catch (SQLException e) {
            throw new DatabaseException("Query failed: " + e.getMessage(), e);
        } finally {
            releaseConnection(conn);
        }
    }
    
    @Override
    public List<Map<String, Object>> query(String sql, Object... params) throws DatabaseException {
        if (sql == null || sql.trim().isEmpty()) {
            throw new DatabaseException("SQL statement cannot be empty");
        }
        List<Map<String, Object>> results = new ArrayList<>();
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        results.add(resultSetToMap(rs));
                    }
                }
            }
            return results;
        } catch (SQLException e) {
            throw new DatabaseException("Query failed: " + e.getMessage(), e);
        } finally {
            releaseConnection(conn);
        }
    }
    
    private String buildWhereClause(Object condition, List<Object> values) {
        if (condition == null) {
            return "1=1";
        }
        
        if (condition instanceof String) {
            return (String) condition;
        }
        
        if (condition instanceof Map) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) condition).entrySet()) {
                if (!first) {
                    sb.append(" AND ");
                }
                String key = entry.getKey();
                String field = key;
                String op = "=";
                
                if (key.contains(" ")) {
                    String[] parts = key.split("\\s+", 2);
                    field = parts[0];
                    op = parts[1];
                }
                
                sb.append(field).append(" ").append(op).append(" ?");
                values.add(entry.getValue());
                first = false;
            }
            return sb.toString();
        }
        
        if (condition instanceof QueryCondition) {
            return buildQueryCondition((QueryCondition) condition, values);
        }
        
        return condition.toString();
    }
    
    private String buildQueryCondition(QueryCondition condition, List<Object> values) {
        switch (condition.getType()) {
            case IN:
                Object inValue = condition.getValue();
                if (inValue instanceof List) {
                    List<?> inValues = (List<?>) inValue;
                    String placeholders = inValues.stream()
                            .map(v -> "?")
                            .collect(Collectors.joining(","));
                    values.addAll(inValues);
                    return condition.getField() + " IN (" + placeholders + ")";
                } else {
                    values.add(inValue);
                    return condition.getField() + " IN (?)";
                }
            case LIKE:
                values.add(condition.getValue());
                return condition.getField() + " LIKE ?";
            case BETWEEN:
                values.add(condition.getValue());
                values.add(condition.getValue2());
                return condition.getField() + " BETWEEN ? AND ?";
            case IS_NULL:
                return condition.getField() + " IS NULL";
            case NOT_NULL:
                return condition.getField() + " IS NOT NULL";
            case AND:
                StringBuilder andSb = new StringBuilder();
                List<Object> children = condition.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    if (i > 0) andSb.append(" AND ");
                    Object child = children.get(i);
                    if (child instanceof QueryCondition) {
                        andSb.append("(").append(buildQueryCondition((QueryCondition) child, values)).append(")");
                    } else {
                        andSb.append(buildWhereClause(child, values));
                    }
                }
                return andSb.toString();
            case OR:
                StringBuilder orSb = new StringBuilder();
                children = condition.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    if (i > 0) orSb.append(" OR ");
                    Object child = children.get(i);
                    if (child instanceof QueryCondition) {
                        orSb.append("(").append(buildQueryCondition((QueryCondition) child, values)).append(")");
                    } else {
                        orSb.append(buildWhereClause(child, values));
                    }
                }
                return orSb.toString();
            default:
                return "1=1";
        }
    }
    
    private Map<String, Object> resultSetToMap(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        ResultSetMetaData meta = rs.getMetaData();
        int count = meta.getColumnCount();
        
        for (int i = 1; i <= count; i++) {
            String name = meta.getColumnName(i);
            Object value = rs.getObject(i);
            map.put(name, value);
        }
        
        return map;
    }
}
