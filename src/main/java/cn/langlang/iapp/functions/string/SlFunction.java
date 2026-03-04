package cn.langlang.iapp.functions.string;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;
import java.util.regex.Pattern;

public class SlFunction implements IFunction {
    @Override
    public String getName() {
        return "sl";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return 4;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String source = toString(arguments.get(0));
        String delimiter = toString(arguments.get(1));
        
        boolean useRegex = false;
        if (arguments.size() > 2) {
            useRegex = toBoolean(arguments.get(2));
        }
        
        if (useRegex) {
            return source.split(delimiter);
        } else {
            return source.split(Pattern.quote(delimiter));
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
