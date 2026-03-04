package cn.langlang.iapp.functions.java;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.lang.reflect.Field;
import java.util.List;

public class JavassFunction implements IFunction {
    @Override
    public String getName() {
        return "javass";
    }
    
    @Override
    public int getMinParameters() {
        return 3;
    }
    
    @Override
    public int getMaxParameters() {
        return 3;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Object targetObj = arguments.get(0);
        String fieldName = toString(arguments.get(1));
        Object value = arguments.get(2);
        
        if (targetObj == null) {
            return null;
        }
        
        try {
            Class<?> clazz = targetObj.getClass();
            Field field = clazz.getField(fieldName);
            field.set(targetObj, value);
            return true;
        } catch (NoSuchFieldException e) {
            try {
                Field field = targetObj.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(targetObj, value);
                return true;
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
    public boolean isSupported() {
        return true;
    }
    
    @Override
    public String getUnsupportedReason() {
        return null;
    }
}
