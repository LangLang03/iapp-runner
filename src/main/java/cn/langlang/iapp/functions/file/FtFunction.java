package cn.langlang.iapp.functions.file;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.File;
import java.util.List;

public class FtFunction extends AbstractFunction {
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
    public Object call(RuntimeContext context, List<Object> arguments) {
        String sourcePath = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String destPath = arguments.get(1) != null ? arguments.get(1).toString() : "";
        sourcePath = context.resolvePath(sourcePath);
        destPath = context.resolvePath(destPath);
        
        try {
            File sourceFile = new File(sourcePath);
            File destFile = new File(destPath);
            
            if (!sourceFile.exists()) {
                return false;
            }
            
            return sourceFile.renameTo(destFile);
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.OUTPUT);
    }
}
