package cn.langlang.iapp.functions.string;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class StobmFunction implements IFunction {
    @Override
    public String getName() {
        return "stobm";
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
        String text = toString(arguments.get(0));
        String encoding = toString(arguments.get(1));
        boolean forUrl = false;
        
        if (arguments.size() >= 3) {
            forUrl = toBoolean(arguments.get(2));
        }
        
        try {
            if (forUrl) {
                return URLEncoder.encode(text, encoding);
            } else {
                StringBuilder result = new StringBuilder();
                byte[] bytes = text.getBytes(encoding);
                for (byte b : bytes) {
                    result.append(String.format("%%%02X", b & 0xFF));
                }
                return result.toString();
            }
        } catch (UnsupportedEncodingException e) {
            throw new FunctionException("Unsupported encoding: " + encoding);
        }
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
    }
    
    private boolean toBoolean(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        return Boolean.parseBoolean(value.toString());
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
