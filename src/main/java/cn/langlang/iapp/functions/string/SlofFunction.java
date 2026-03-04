package cn.langlang.iapp.functions.string;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class SlofFunction implements IFunction {
    @Override
    public String getName() {
        return "slof";
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
        String search = toString(arguments.get(1));
        int fromIndex = source.length();
        
        if (arguments.size() >= 3) {
            fromIndex = toInt(arguments.get(2));
        }
        
        int index = source.lastIndexOf(search, fromIndex);
        return (long) index;
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
    }
    
    private int toInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
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
