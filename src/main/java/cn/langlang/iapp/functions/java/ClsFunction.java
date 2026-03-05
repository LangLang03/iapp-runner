package cn.langlang.iapp.functions.java;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.Arrays;
import java.util.List;

public class ClsFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "cls";
    }
    
    @Override
    public int getMinParameters() {
        return 1;
    }
    
    @Override
    public int getMaxParameters() {
        return 2;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String className = toString(arguments.get(0));
        
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new FunctionException("类未找到: " + className, e);
        }
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return Arrays.asList(ParamType.STRING, ParamType.OUTPUT);
    }
}
