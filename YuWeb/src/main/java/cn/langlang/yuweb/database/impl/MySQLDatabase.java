package cn.langlang.yuweb.database.impl;

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
    private Connection connection;
    
    public MySQLDatabase(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.charset = "utf8mb4";
    }
    
    public MySQLDatabase(String connectionString) {
        parseConnectionString(connectionString);
    }
    
    private void parseConnectionString(String connectionString) {
        this.host = "localhost";
        this.port = 3306;
        this.charset = "utf8mb4";
        
        String[] parts = connectionString.split("/");
        if (parts.length >= 4) {
            String hostPort = parts[2];
            this.database = parts[3].split("[?]")[0];
            
            if (hostPort.contains(":")) {
                String[] hp = hostPort.split(":");
                this.host = hp[0];
                this.port = Integer.parseInt(hp[1]);
            } else {
                this.host = hostPort;
            }
            
            if (parts.length > 3 && parts[3].contains("?")) {
                String queryString = parts[3].split("[?]")[1];
                for (String param : queryString.split("&")) {
                    String[] kv = param.split("=");
                    if (kv.length == 2) {
                        if ("charset".equalsIgnoreCase(kv[0])) {
                            this.charset = kv[1];
                        }
                    }
                }
            }
        }
        
        int atIndex = connectionString.indexOf("@");
        if (atIndex > 0) {
            String userPass = connectionString.substring(0, atIndex);
            if (userPass.startsWith("mysql://")) {
                userPass = userPass.substring(8);
            }
            String[] up = userPass.split(":");
            this.username = up[0];
            this.password = up.length > 1 ? up[1] : "";
        }
    }
    
    public void setCharset(String charset) {
        this.charset = charset;
    }
    
    @Override
    public void connect() throws DatabaseException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            String url = String.format(
                "jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&characterEncoding=%s&allowPublicKeyRetrieval=true",
                host, port, database, charset
            );
            
            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(true);
            
            logger.info("Connected to MySQL database: {}@{}:{}/{}", username, host, port, database);
        } catch (ClassNotFoundException | SQLException e) {
            throw new DatabaseException("Failed to connect to MySQL: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Disconnected from MySQL database");
            } catch (SQLException e) {
                logger.error("Error closing MySQL connection: {}", e.getMessage());
            }
            connection = null;
        }
    }
    
    @Override
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
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
        
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new DatabaseException("Insert failed: " + e.getMessage(), e);
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
        
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Update failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public int delete(String table, Object condition) throws DatabaseException {
        StringBuilder sql = new StringBuilder("DELETE FROM ");
        sql.append(table);
        
        List<Object> values = new ArrayList<>();
        String whereClause = buildWhereClause(condition, values);
        sql.append(" WHERE ").append(whereClause);
        
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Delete failed: " + e.getMessage(), e);
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
        
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return resultSetToMap(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Query failed: " + e.getMessage(), e);
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
        
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(resultSetToMap(rs));
            }
            return results;
        } catch (SQLException e) {
            throw new DatabaseException("Query failed: " + e.getMessage(), e);
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
        
        try {
            try (PreparedStatement countStmt = connection.prepareStatement(countSql.toString())) {
                for (int i = 0; i < values.size(); i++) {
                    countStmt.setObject(i + 1, values.get(i));
                }
                ResultSet rs = countStmt.executeQuery();
                long total = rs.next() ? rs.getLong(1) : 0;
                result.put("total", total);
                result.put("page", page);
                result.put("size", size);
                result.put("totalPages", (total + size - 1) / size);
            }
            
            List<Map<String, Object>> data = new ArrayList<>();
            try (PreparedStatement dataStmt = connection.prepareStatement(dataSql.toString())) {
                int paramIndex = 1;
                for (Object value : values) {
                    dataStmt.setObject(paramIndex++, value);
                }
                dataStmt.setInt(paramIndex++, offset);
                dataStmt.setInt(paramIndex, size);
                
                ResultSet rs = dataStmt.executeQuery();
                while (rs.next()) {
                    data.add(resultSetToMap(rs));
                }
            }
            result.put("data", data);
            
            return result;
        } catch (SQLException e) {
            throw new DatabaseException("Page query failed: " + e.getMessage(), e);
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
        
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Count failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void execute(String sql) throws DatabaseException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new DatabaseException("Execute failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void execute(String sql, Object... params) throws DatabaseException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.execute();
        } catch (SQLException e) {
            throw new DatabaseException("Execute failed: " + e.getMessage(), e);
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
