package cn.langlang.iapp.functions.string;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;
import java.util.regex.Pattern;

public class SrFunction implements IFunction {
    @Override
    public String getName() {
        return "sr";
    }
    
    @Override
    public int getMinParameters() {
        return 3;
    }
    
    @Override
    public int getMaxParameters() {
        return 6;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String source = toString(arguments.get(0));
        String target = toString(arguments.get(1));
        String replacement = toString(arguments.get(2));
        
        boolean useRegex = false;
        if (arguments.size() > 3) {
            useRegex = toBoolean(arguments.get(3));
        }
        
        if (useRegex) {
            return source.replaceAll(target, replacement);
        } else {
            return source.replace(target, replacement);
        }
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
    }
    
    private boolean toBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(String.valueOf(value));
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
