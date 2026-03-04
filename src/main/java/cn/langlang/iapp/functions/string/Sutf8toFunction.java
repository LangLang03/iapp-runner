package cn.langlang.iapp.functions.string;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sutf8toFunction implements IFunction {
    @Override
    public String getName() {
        return "sutf8to";
    }
    
    @Override
    public int getMinParameters() {
        return 1;
    }
    
    @Override
    public int getMaxParameters() {
        return 3;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String text = toString(arguments.get(0));
        String encoding = "UTF-8";
        boolean forUrl = false;
        
        if (arguments.size() >= 2) {
            String enc = toString(arguments.get(1));
            if (enc != null && !enc.isEmpty()) {
                encoding = enc;
            }
        }
        if (arguments.size() >= 3) {
            forUrl = toBoolean(arguments.get(2));
        }
        
        try {
            if (forUrl) {
                return URLDecoder.decode(text, encoding);
            } else {
                Pattern pattern = Pattern.compile("%([0-9A-Fa-f]{2})");
                Matcher matcher = pattern.matcher(text);
                StringBuffer result = new StringBuffer();
                java.util.List<Byte> bytes = new java.util.ArrayList<>();
                
                int lastEnd = 0;
                while (matcher.find()) {
                    result.append(text, lastEnd, matcher.start());
                    String hexStr = matcher.group(1);
                    bytes.add((byte) Integer.parseInt(hexStr, 16));
                    lastEnd = matcher.end();
                }
                result.append(text.substring(lastEnd));
                
                if (bytes.isEmpty()) {
                    return text;
                }
                
                byte[] byteArray = new byte[bytes.size()];
                for (int i = 0; i < bytes.size(); i++) {
                    byteArray[i] = bytes.get(i);
                }
                
                return new String(byteArray, encoding);
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
