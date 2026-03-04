package cn.langlang.iapp.functions.java;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.lang.reflect.Field;
import java.util.List;

public class JavassFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "javass";
    }
    
    @Override
    public int getMinParameters() {
        return 4;
    }
    
    @Override
    public int getMaxParameters() {
        return 5;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Object instanceObj = arguments.get(0);
        Object classObj = arguments.get(1);
        String fieldName = toString(arguments.get(2));
        Object value = arguments.get(3);
        
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
        } else {
            throw new FunctionException("Invalid class parameter");
        }
        
        try {
            Field field = clazz.getField(fieldName);
            field.set(targetInstance, value);
            return value;
        } catch (NoSuchFieldException e) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(targetInstance, value);
                return value;
            } catch (Exception ex) {
                throw new FunctionException("Failed to set field: " + fieldName, ex);
            }
        } catch (Exception e) {
            throw new FunctionException("Failed to set field: " + fieldName, e);
        }
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OUTPUT, ParamType.OBJECT, ParamType.OBJECT, ParamType.STRING, ParamType.OBJECT);
    }
}
