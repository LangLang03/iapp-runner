package cn.langlang.iapp.api;

import java.util.List;

public class IAppVariable {
    
    public enum VariableScope {
        LOCAL("s"),
        INTERFACE("ss"),
        GLOBAL("sss");
        
        private final String keyword;
        
        VariableScope(String keyword) {
            this.keyword = keyword;
        }
        
        public String getKeyword() {
            return keyword;
        }
        
        public static VariableScope fromKeyword(String keyword) {
            if (keyword == null) return LOCAL;
            switch (keyword) {
                case "s": return LOCAL;
                case "ss": return INTERFACE;
                case "sss": return GLOBAL;
                default: return LOCAL;
            }
        }
    }
    
    private final String name;
    private final Object value;
    private final VariableScope scope;
    
    public IAppVariable(String name, Object value) {
        this(name, value, VariableScope.LOCAL);
    }
    
    public IAppVariable(String name, Object value, VariableScope scope) {
        this.name = name;
        this.value = value;
        this.scope = scope != null ? scope : VariableScope.LOCAL;
    }
    
    public String getName() {
        return name;
    }
    
    public Object getValue() {
        return value;
    }
    
    public VariableScope getScope() {
        return scope;
    }
    
    public boolean isNil() {
        return value == null;
    }
    
    public boolean isString() {
        return value instanceof String;
    }
    
    public boolean isNumber() {
        return value instanceof Number;
    }
    
    public boolean isBoolean() {
        return value instanceof Boolean;
    }
    
    public boolean isArray() {
        return value instanceof Object[];
    }
    
    public boolean isList() {
        return value instanceof List;
    }
    
    public Class<?> getType() {
        if (value == null) return null;
        return value.getClass();
    }
    
    public String asString() {
        return asString(null);
    }
    
    public String asString(String defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof String) return (String) value;
        return value.toString();
    }
    
    public int asInt() {
        return asInt(0);
    }
    
    public int asInt(int defaultValue) {
        if (value == null) return defaultValue;
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
        if (value == null) return defaultValue;
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
        if (value == null) return defaultValue;
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
        if (value == null) return defaultValue;
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
        if (value == null) return new Object[0];
        if (value instanceof Object[]) return (Object[]) value;
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            return list.toArray();
        }
        return new Object[]{value};
    }
    
    public List<?> asList() {
        if (value == null) return java.util.Collections.emptyList();
        if (value instanceof List) return (List<?>) value;
        if (value instanceof Object[]) {
            return java.util.Arrays.asList((Object[]) value);
        }
        return java.util.Collections.singletonList(value);
    }
    
    public IAppValue toValue() {
        return IAppValue.valueOf(value);
    }
    
    @Override
    public String toString() {
        return "IAppVariable{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", scope=" + scope +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof IAppVariable) {
            IAppVariable other = (IAppVariable) obj;
            if (!this.name.equals(other.name)) return false;
            if (this.value == null) return other.value == null;
            return this.value.equals(other.value);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + scope.hashCode();
        return result;
    }
}
