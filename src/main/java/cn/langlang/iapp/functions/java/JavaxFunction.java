package cn.langlang.iapp.functions.java;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class JavaxFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "javax";
    }
    
    @Override
    public int getMinParameters() {
        return 4;
    }
    
    @Override
    public int getMaxParameters() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Object instanceObj = arguments.get(0);
        Object classObj = arguments.get(1);
        String methodName = toString(arguments.get(2));
        
        Class<?> clazz;
        Object targetInstance;
        
        if (classObj instanceof Class) {
            clazz = (Class<?>) classObj;
            targetInstance = instanceObj;
        } else if (classObj instanceof String) {
            try {
                clazz = Class.forName((String) classObj);
                targetInstance = instanceObj;
            } catch (ClassNotFoundException e) {
                throw new FunctionException("类未找到: " + classObj, e);
            }
        } else if (classObj == null) {
            if (instanceObj == null) {
                throw new FunctionException("实例和类参数不能同时为空");
            }
            clazz = instanceObj.getClass();
            targetInstance = instanceObj;
        } else {
            throw new FunctionException("无效的类参数");
        }
        
        List<Class<?>> paramTypes = new ArrayList<>();
        List<Object> paramValues = new ArrayList<>();
        
        for (int i = 3; i < arguments.size(); i += 2) {
            if (i + 1 < arguments.size()) {
                String typeName = toString(arguments.get(i));
                Object value = arguments.get(i + 1);
                
                Class<?> paramType = parseType(typeName);
                paramTypes.add(paramType);
                paramValues.add(convertValue(value, paramType));
            }
        }
        
        try {
            Method method = findMethod(clazz, methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(targetInstance, paramValues.toArray());
        } catch (Exception e) {
            throw new FunctionException("javax 函数调用失败: " + methodName, e);
        }
    }
    
    private Class<?> parseType(String typeName) throws FunctionException {
        switch (typeName) {
            case "int": return int.class;
            case "long": return long.class;
            case "short": return short.class;
            case "byte": return byte.class;
            case "float": return float.class;
            case "double": return double.class;
            case "boolean": return boolean.class;
            case "char": return char.class;
            case "String": return String.class;
            case "int[]": return int[].class;
            case "long[]": return long[].class;
            case "String[]": return String[].class;
            case "Object": return Object.class;
            case "Object[]": return Object[].class;
            default:
                try {
                    return Class.forName(typeName);
                } catch (ClassNotFoundException e) {
                    throw new FunctionException("未知类型: " + typeName);
                }
        }
    }
    
    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) return null;
        
        if (targetType == int.class || targetType == Integer.class) {
            if (value instanceof Number) return ((Number) value).intValue();
            return Integer.parseInt(value.toString());
        }
        if (targetType == long.class || targetType == Long.class) {
            if (value instanceof Number) return ((Number) value).longValue();
            return Long.parseLong(value.toString());
        }
        if (targetType == short.class || targetType == Short.class) {
            if (value instanceof Number) return ((Number) value).shortValue();
            return Short.parseShort(value.toString());
        }
        if (targetType == byte.class || targetType == Byte.class) {
            if (value instanceof Number) return ((Number) value).byteValue();
            return Byte.parseByte(value.toString());
        }
        if (targetType == float.class || targetType == Float.class) {
            if (value instanceof Number) return ((Number) value).floatValue();
            return Float.parseFloat(value.toString());
        }
        if (targetType == double.class || targetType == Double.class) {
            if (value instanceof Number) return ((Number) value).doubleValue();
            return Double.parseDouble(value.toString());
        }
        if (targetType == boolean.class || targetType == Boolean.class) {
            if (value instanceof Boolean) return value;
            return Boolean.parseBoolean(value.toString());
        }
        if (targetType == char.class || targetType == Character.class) {
            String s = value.toString();
            return s.isEmpty() ? '\0' : s.charAt(0);
        }
        
        return value;
    }
    
    private Method findMethod(Class<?> clazz, String methodName, List<Class<?>> paramTypes) throws NoSuchMethodException {
        try {
            return clazz.getMethod(methodName, paramTypes.toArray(new Class<?>[0]));
        } catch (NoSuchMethodException e) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName) && 
                    method.getParameterCount() == paramTypes.size()) {
                    return method;
                }
            }
            throw e;
        }
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OUTPUT, ParamType.OBJECT, ParamType.OBJECT, ParamType.STRING, ParamType.STRING, ParamType.OBJECT);
    }
}
