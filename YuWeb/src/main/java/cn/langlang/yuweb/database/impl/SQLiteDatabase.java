package cn.langlang.yuweb.database.impl;

import cn.langlang.yuweb.database.Database;
import cn.langlang.yuweb.database.DatabaseException;
import cn.langlang.yuweb.database.QueryCondition;
import com.google.gson.Gson;

import java.sql.*;
import java.util.*;

public class SQLiteDatabase implements Database {
    private String path;
    private Connection connection;
    private static final Gson gson = new Gson();
    
    public SQLiteDatabase(String path) {
        this.path = path;
    }
    
    @Override
    public void connect() throws DatabaseException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            connection.setAutoCommit(true);
        } catch (ClassNotFoundException | SQLException e) {
            throw new DatabaseException("Failed to connect to database: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
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
        
        dataSql.append(" LIMIT ? OFFSET ?");
        
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
                dataStmt.setInt(paramIndex++, size);
                dataStmt.setInt(paramIndex, offset);
                
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
                values.add(condition.getValue());
                return condition.getField() + " IN (?)";
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
