package cn.langlang.iapp.api;

import java.util.List;

public class IAppValue {
    
    public enum ValueType {
        NIL, STRING, NUMBER, BOOLEAN, ARRAY, LIST, FUNCTION, OBJECT
    }
    
    private static final IAppValue NIL = new IAppValue(null, ValueType.NIL);
    
    private final Object value;
    private final ValueType type;
    
    private IAppValue(Object value, ValueType type) {
        this.value = value;
        this.type = type;
    }
    
    public ValueType getType() {
        return type;
    }
    
    public Object toObject() {
        return value;
    }
    
    public boolean isNil() {
        return type == ValueType.NIL || value == null;
    }
    
    public boolean isString() {
        return type == ValueType.STRING || value instanceof String;
    }
    
    public boolean isNumber() {
        return type == ValueType.NUMBER || value instanceof Number;
    }
    
    public boolean isBoolean() {
        return type == ValueType.BOOLEAN || value instanceof Boolean;
    }
    
    public boolean isArray() {
        return type == ValueType.ARRAY || value instanceof Object[];
    }
    
    public boolean isList() {
        return type == ValueType.LIST || value instanceof List;
    }
    
    public boolean isFunction() {
        return type == ValueType.FUNCTION || value instanceof IAppFunction;
    }
    
    public String asString() {
        return asString(null);
    }
    
    public String asString(String defaultValue) {
        if (isNil()) return defaultValue;
        if (value instanceof String) return (String) value;
        return value != null ? value.toString() : defaultValue;
    }
    
    public int asInt() {
        return asInt(0);
    }
    
    public int asInt(int defaultValue) {
        if (isNil()) return defaultValue;
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
    
    public long asLong() {
        return asLong(0L);
    }
    
    public long asLong(long defaultValue) {
        if (isNil()) return defaultValue;
        if (value instanceof Number) return ((Number) value).longValue();
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
    
    public double asDouble() {
        return asDouble(0.0);
    }
    
    public double asDouble(double defaultValue) {
        if (isNil()) return defaultValue;
        if (value instanceof Number) return ((Number) value).doubleValue();
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
    
    public boolean asBoolean() {
        return asBoolean(false);
    }
    
    public boolean asBoolean(boolean defaultValue) {
        if (isNil()) return defaultValue;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof String) {
            String s = ((String) value).toLowerCase();
            return "true".equals(s) || "1".equals(s) || "yes".equals(s);
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0;
        }
        return defaultValue;
    }
    
    public Object[] asArray() {
        if (isNil()) return new Object[0];
        if (value instanceof Object[]) return (Object[]) value;
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            return list.toArray();
        }
        return new Object[]{value};
    }
    
    public List<?> asList() {
        if (isNil()) return java.util.Collections.emptyList();
        if (value instanceof List) return (List<?>) value;
        if (value instanceof Object[]) {
            return java.util.Arrays.asList((Object[]) value);
        }
        return java.util.Collections.singletonList(value);
    }
    
    public IAppFunction asFunction() {
        if (value instanceof IAppFunction) return (IAppFunction) value;
        return null;
    }
    
    public static IAppValue valueOf(Object value) {
        if (value == null) return NIL;
        if (value instanceof IAppValue) return (IAppValue) value;
        if (value instanceof String) return valueOf((String) value);
        if (value instanceof Integer) return valueOf(((Integer) value).intValue());
        if (value instanceof Long) return valueOf(((Long) value).longValue());
        if (value instanceof Double) return valueOf(((Double) value).doubleValue());
        if (value instanceof Float) return valueOf(((Float) value).doubleValue());
        if (value instanceof Boolean) return valueOf(((Boolean) value).booleanValue());
        if (value instanceof Object[]) return new IAppValue(value, ValueType.ARRAY);
        if (value instanceof List) return new IAppValue(value, ValueType.LIST);
        if (value instanceof IAppFunction) return new IAppValue(value, ValueType.FUNCTION);
        if (value instanceof Number) return new IAppValue(value, ValueType.NUMBER);
        return new IAppValue(value, ValueType.OBJECT);
    }
    
    public static IAppValue nil() {
        return NIL;
    }
    
    public static IAppValue valueOf(String value) {
        if (value == null) return NIL;
        return new IAppValue(value, ValueType.STRING);
    }
    
    public static IAppValue valueOf(int value) {
        return new IAppValue(value, ValueType.NUMBER);
    }
    
    public static IAppValue valueOf(long value) {
        return new IAppValue(value, ValueType.NUMBER);
    }
    
    public static IAppValue valueOf(double value) {
        return new IAppValue(value, ValueType.NUMBER);
    }
    
    public static IAppValue valueOf(boolean value) {
        return new IAppValue(value, ValueType.BOOLEAN);
    }
    
    public static IAppValue valueOf(Object[] value) {
        if (value == null) return NIL;
        return new IAppValue(value, ValueType.ARRAY);
    }
    
    public static IAppValue valueOf(List<?> value) {
        if (value == null) return NIL;
        return new IAppValue(value, ValueType.LIST);
    }
    
    @Override
    public String toString() {
        if (isNil()) return "nil";
        return String.valueOf(value);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof IAppValue) {
            IAppValue other = (IAppValue) obj;
            if (this.isNil() && other.isNil()) return true;
            if (this.value == null) return other.value == null;
            return this.value.equals(other.value);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
