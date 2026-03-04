package cn.langlang.iapp.functions.java;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.lang.reflect.Field;
import java.util.List;

public class JavagsFunction implements IFunction {
    @Override
    public String getName() {
        return "javags";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return 2;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Object targetObj = arguments.get(0);
        String fieldName = toString(arguments.get(1));
        
        if (targetObj == null) {
            return null;
        }
        
        try {
            Class<?> clazz = targetObj.getClass();
            Field field = clazz.getField(fieldName);
            return field.get(targetObj);
        } catch (NoSuchFieldException e) {
            try {
                Field field = targetObj.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(targetObj);
            } catch (Exception ex) {
                throw new FunctionException("Failed to get field: " + fieldName, ex);
            }
        } catch (Exception e) {
            throw new FunctionException("Failed to get field: " + fieldName, e);
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
