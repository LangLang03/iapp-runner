package cn.langlang.iapp.functions.file;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.File;
import java.util.List;

public class FdirFunction implements IFunction {
    @Override
    public String getName() {
        return "fdir";
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
        if (arguments.isEmpty()) {
            return context.getCurrentDirectory();
        }
        
        String type = toString(arguments.get(0));
        switch (type.toLowerCase()) {
            case "app":
                return context.getCurrentDirectory();
            case "data":
                return new File(context.getCurrentDirectory(), "data").getAbsolutePath();
            case "cache":
                return new File(context.getCurrentDirectory(), "cache").getAbsolutePath();
            case "files":
                return new File(context.getCurrentDirectory(), "files").getAbsolutePath();
            case "external":
                return System.getProperty("user.dir");
            default:
                return context.getCurrentDirectory();
        }
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
