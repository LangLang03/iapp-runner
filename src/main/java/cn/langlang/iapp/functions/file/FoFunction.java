package cn.langlang.iapp.functions.file;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class FoFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "fo";
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
        String source = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String dest = arguments.get(1) != null ? arguments.get(1).toString() : "";
        source = context.resolvePath(source);
        dest = context.resolvePath(dest);
        
        try {
            File srcFile = new File(source);
            File destFile = new File(dest);
            destFile.getParentFile().mkdirs();
            Files.move(srcFile.toPath(), destFile.toPath());
            return true;
        } catch (Exception e) {
            throw new FunctionException("移动文件失败: " + source + " -> " + dest, e);
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.OUTPUT);
    }
}
