package cn.langlang.iapp.functions.file;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FlFunction implements IFunction {
    @Override
    public String getName() {
        return "fl";
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
        String path = toString(arguments.get(0));
        path = context.resolvePath(path);
        
        boolean includeHidden = false;
        if (arguments.size() > 1) {
            includeHidden = toBoolean(arguments.get(1));
        }
        
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            return new Object[0];
        }
        
        File[] files = dir.listFiles();
        if (files == null) {
            return new Object[0];
        }
        
        List<String> result = new ArrayList<>();
        for (File file : files) {
            if (!includeHidden && file.isHidden()) {
                continue;
            }
            result.add(file.getAbsolutePath());
        }
        
        return result.toArray();
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
