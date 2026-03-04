package cn.langlang.iapp.functions.file;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class FtFunction implements IFunction {
    @Override
    public String getName() {
        return "ft";
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
        String sourcePath = toString(arguments.get(0));
        String destPath = toString(arguments.get(1));
        
        sourcePath = context.resolvePath(sourcePath);
        destPath = context.resolvePath(destPath);
        
        File source = new File(sourcePath);
        File dest = new File(destPath);
        
        if (!source.exists()) {
            return false;
        }
        
        File parent = dest.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        
        try {
            Files.move(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            throw new FunctionException("Failed to move: " + sourcePath + " to " + destPath, e);
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
