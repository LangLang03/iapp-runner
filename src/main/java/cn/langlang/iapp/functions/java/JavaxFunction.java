package cn.langlang.iapp.functions.java;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
                throw new FunctionException("Class not found: " + classObj, e);
            }
        } else if (classObj == null) {
            throw new FunctionException("Class parameter cannot be null");
        } else {
            throw new FunctionException("Invalid class parameter");
        }
        
        List<Class<?>> paramTypes = new ArrayList<>();
        List<Object> paramValues = new ArrayList<>();
        
        for (int i = 3; i < arguments.size(); i += 2) {
            if (i + 1 < arguments.size()) {
                String typeName = toString(arguments.get(i));
                Object value = arguments.get(i + 1);
                
                Class<?> paramType = parseType(typeName);
                paramTypes.add(paramType);
                paramValues.add(value);
            }
        }
        
        try {
            Method method = findMethod(clazz, methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(targetInstance, paramValues.toArray());
        } catch (Exception e) {
            throw new FunctionException("Javax function call failed: " + methodName, e);
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
                    throw new FunctionException("Unknown type: " + typeName);
                }
        }
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
        return types(ParamType.OBJECT, ParamType.OBJECT, ParamType.STRING, ParamType.STRING, ParamType.OBJECT);
    }
}
