package cn.langlang.iapp.functions.file;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.File;
import java.util.List;

public class FwFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "fw";
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
        String path = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String content = arguments.get(1) != null ? arguments.get(1).toString() : "";
        path = context.resolvePath(path);
        
        try {
            File file = new File(path);
            file.getParentFile().mkdirs();
            java.io.FileWriter writer = new java.io.FileWriter(file);
            writer.write(content);
            writer.close();
            return true;
        } catch (Exception e) {
            throw new FunctionException("Failed to write file: " + path, e);
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.OUTPUT);
    }
}
