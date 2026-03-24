package cn.langlang.yuweb.database;

import java.util.Arrays;
import java.util.List;

public class QueryCondition {
    public enum Type {
        IN, LIKE, BETWEEN, IS_NULL, NOT_NULL, AND, OR
    }
    
    private Type type;
    private String field;
    private Object value;
    private Object value2;
    private List<Object> children;
    
    public QueryCondition(Type type, String field, Object value) {
        this.type = type;
        this.field = field;
        this.value = value;
    }
    
    public QueryCondition(Type type, String field, Object value, Object value2) {
        this.type = type;
        this.field = field;
        this.value = value;
        this.value2 = value2;
    }
    
    public QueryCondition(Type type, String field) {
        this.type = type;
        this.field = field;
    }
    
    public QueryCondition(Type type, Object... conditions) {
        this.type = type;
        this.children = Arrays.asList(conditions);
    }
    
    public Type getType() { return type; }
    public String getField() { return field; }
    public Object getValue() { return value; }
    public Object getValue2() { return value2; }
    public List<Object> getChildren() { return children; }
    
    public static QueryCondition in(String field, Object values) {
        return new QueryCondition(Type.IN, field, values);
    }
    
    public static QueryCondition like(String field, String pattern) {
        return new QueryCondition(Type.LIKE, field, pattern);
    }
    
    public static QueryCondition between(String field, Object value1, Object value2) {
        return new QueryCondition(Type.BETWEEN, field, value1, value2);
    }
    
    public static QueryCondition isNull(String field) {
        return new QueryCondition(Type.IS_NULL, field);
    }
    
    public static QueryCondition notNull(String field) {
        return new QueryCondition(Type.NOT_NULL, field);
    }
    
    public static QueryCondition and(Object... conditions) {
        return new QueryCondition(Type.AND, conditions);
    }
    
    public static QueryCondition or(Object... conditions) {
        return new QueryCondition(Type.OR, conditions);
    }
}
