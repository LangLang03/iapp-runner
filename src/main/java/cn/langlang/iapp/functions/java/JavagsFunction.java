package cn.langlang.iapp.functions.java;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.lang.reflect.Field;
import java.util.List;

public class JavagsFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "javags";
    }
    
    @Override
    public int getMinParameters() {
        return 3;
    }
    
    @Override
    public int getMaxParameters() {
        return 4;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Object instanceObj = arguments.get(0);
        Object classObj = arguments.get(1);
        String fieldName = toString(arguments.get(2));
        
        Class<?> clazz;
        Object targetInstance;
        
        if (classObj == null) {
            throw new FunctionException("类参数不能为空");
        } else if (classObj instanceof Class) {
            clazz = (Class<?>) classObj;
            targetInstance = instanceObj;
        } else if (classObj instanceof String) {
            try {
                clazz = Class.forName((String) classObj);
                targetInstance = instanceObj;
            } catch (ClassNotFoundException e) {
                throw new FunctionException("类未找到: " + classObj, e);
            }
        } else {
            throw new FunctionException("无效的类参数: " + classObj.getClass().getName());
        }
        
        try {
            Field field = clazz.getField(fieldName);
            return field.get(targetInstance);
        } catch (NoSuchFieldException e) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(targetInstance);
            } catch (Exception ex) {
                throw new FunctionException("获取字段失败: " + fieldName, ex);
            }
        } catch (Exception e) {
            throw new FunctionException("获取字段失败: " + fieldName, e);
        }
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OUTPUT, ParamType.OBJECT, ParamType.OBJECT, ParamType.STRING);
    }
}
