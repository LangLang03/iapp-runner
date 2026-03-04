package cn.langlang.iapp.functions.java;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class JavanewFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "javanew";
    }
    
    @Override
    public int getMinParameters() {
        return 1;
    }
    
    @Override
    public int getMaxParameters() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Object classObj = arguments.get(0);
        
        Class<?> clazz;
        if (classObj instanceof Class) {
            clazz = (Class<?>) classObj;
        } else if (classObj instanceof String) {
            try {
                clazz = Class.forName((String) classObj);
            } catch (ClassNotFoundException e) {
                throw new FunctionException("Class not found: " + classObj, e);
            }
        } else {
            throw new FunctionException("Invalid class parameter");
        }
        
        List<Class<?>> paramTypes = new ArrayList<>();
        List<Object> paramValues = new ArrayList<>();
        
        for (int i = 1; i < arguments.size(); i += 2) {
            if (i + 1 < arguments.size()) {
                String typeName = toString(arguments.get(i));
                Object value = arguments.get(i + 1);
                
                Class<?> paramType = parseType(typeName);
                paramTypes.add(paramType);
                paramValues.add(value);
            }
        }
        
        try {
            Constructor<?> constructor = findConstructor(clazz, paramTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(paramValues.toArray());
        } catch (Exception e) {
            throw new FunctionException("Failed to create instance of: " + clazz.getName(), e);
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
    
    private Constructor<?> findConstructor(Class<?> clazz, List<Class<?>> paramTypes) throws NoSuchMethodException {
        try {
            return clazz.getConstructor(paramTypes.toArray(new Class<?>[0]));
        } catch (NoSuchMethodException e) {
            for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                if (constructor.getParameterCount() == paramTypes.size()) {
                    return constructor;
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
        return types(ParamType.OBJECT, ParamType.STRING, ParamType.OBJECT);
    }
}
