package cn.langlang.iapp.functions.time;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TimeFunction implements IFunction {
    @Override
    public String getName() {
        return "time";
    }
    
    @Override
    public int getMinParameters() {
        return 0;
    }
    
    @Override
    public int getMaxParameters() {
        return 2;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        long timestamp = System.currentTimeMillis();
        
        if (arguments.isEmpty()) {
            return timestamp;
        }
        
        String format = toString(arguments.get(0));
        long time = timestamp;
        
        if (arguments.size() > 1) {
            time = toLong(arguments.get(1));
        }
        
        if (format == null || format.isEmpty()) {
            return time;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(new Date(time));
        } catch (Exception e) {
            throw new FunctionException("Invalid time format: " + format, e);
        }
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
    }
    
    private long toLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
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
