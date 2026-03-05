package cn.langlang.iapp.functions.java;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class JavaFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "java";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Object targetObj = arguments.get(0);
        String methodPath = arguments.get(1) != null ? arguments.get(1).toString() : "";
        
        String className = null;
        String methodName = methodPath;
        
        int lastDot = methodPath.lastIndexOf('.');
        if (lastDot > 0) {
            className = methodPath.substring(0, lastDot);
            methodName = methodPath.substring(lastDot + 1);
        }
        
        Object[] args = null;
        if (arguments.size() > 2) {
            args = new Object[arguments.size() - 2];
            for (int i = 2; i < arguments.size(); i++) {
                args[i - 2] = arguments.get(i);
            }
        }
        
        try {
            if (className != null) {
                Class<?> clazz = Class.forName(className);
                return context.executeMjavaMethod("", className + "." + methodName, args);
            } else {
                return context.executeMjavaMethod("", methodName, args);
            }
        } catch (Exception e) {
            throw new FunctionException("Java 调用失败: " + methodPath, e);
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OBJECT, ParamType.STRING, ParamType.STRING, ParamType.OBJECT);
    }
}
