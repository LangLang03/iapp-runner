package cn.langlang.iapp.functions.string;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class SsgFunction implements IFunction {
    @Override
    public String getName() {
        return "ssg";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return 3;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String source = toString(arguments.get(0));
        int start = toInt(arguments.get(1));
        
        if (arguments.size() > 2) {
            int end = toInt(arguments.get(2));
            if (start >= 0 && end <= source.length() && start <= end) {
                return source.substring(start, end);
            }
        } else {
            if (start >= 0 && start <= source.length()) {
                return source.substring(start);
            }
        }
        return null;
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
    }
    
    private int toInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
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
