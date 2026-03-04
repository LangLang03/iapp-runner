package cn.langlang.iapp.functions.string;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class SjFunction implements IFunction {
    @Override
    public String getName() {
        return "sj";
    }
    
    @Override
    public int getMinParameters() {
        return 3;
    }
    
    @Override
    public int getMaxParameters() {
        return 5;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String source = toString(arguments.get(0));
        String start = arguments.get(1) != null ? toString(arguments.get(1)) : null;
        String end = arguments.get(2) != null ? toString(arguments.get(2)) : null;
        
        int startIndex = 0;
        int endIndex = source.length();
        
        if (start != null) {
            int pos = source.indexOf(start);
            if (pos >= 0) {
                startIndex = pos + start.length();
            }
        }
        
        if (end != null) {
            int pos = source.indexOf(end, startIndex);
            if (pos >= 0) {
                endIndex = pos;
            }
        }
        
        if (startIndex < endIndex && startIndex < source.length()) {
            return source.substring(startIndex, endIndex);
        }
        return "";
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
