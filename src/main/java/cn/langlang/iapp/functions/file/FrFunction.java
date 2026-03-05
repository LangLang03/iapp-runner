package cn.langlang.iapp.functions.file;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.File;
import java.util.List;

public class FrFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "fr";
    }
    
    @Override
    public int getMinParameters() {
        return 1;
    }
    
    @Override
    public int getMaxParameters() {
        return 2;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String path = arguments.get(0) != null ? arguments.get(0).toString() : "";
        path = context.resolvePath(path);
        
        try {
            File file = new File(path);
            if (!file.exists()) {
                return "";
            }
            return new String(java.nio.file.Files.readAllBytes(file.toPath()));
        } catch (Exception e) {
            throw new FunctionException("读取文件失败: " + path, e);
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.OUTPUT);
    }
}
