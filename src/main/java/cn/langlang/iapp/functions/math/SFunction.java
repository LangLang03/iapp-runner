package cn.langlang.iapp.functions.math;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.Arrays;
import java.util.List;

public class SFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "s";
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
        Object arg = arguments.get(0);
        
        if (arg instanceof Boolean) {
            return arg;
        }
        
        if (arg instanceof Number) {
            return ((Number) arg).longValue();
        }
        
        String expression = toString(arg);
        
        if (expression.equals("true")) {
            return true;
        }
        if (expression.equals("false")) {
            return false;
        }
        
        try {
            return Long.parseLong(expression);
        } catch (NumberFormatException ex) {
            try {
                return (long) Double.parseDouble(expression);
            } catch (NumberFormatException ex2) {
                throw new FunctionException("无法解析表达式: " + expression);
            }
        }
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OBJECT, ParamType.OUTPUT);
    }
}
