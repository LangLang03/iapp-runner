package cn.langlang.iapp.functions.file;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
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
        return 4;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String path = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String content = arguments.get(1) != null ? arguments.get(1).toString() : "";
        path = context.resolvePath(path);
        
        String charset = "UTF-8";
        if (arguments.size() > 2 && arguments.get(2) != null) {
            charset = arguments.get(2).toString();
        }
        
        try {
            File file = new File(path);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(file), charset)) {
                writer.write(content);
            }
            return true;
        } catch (Exception e) {
            throw new FunctionException("写入文件失败: " + path + " - " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.OUTPUT);
    }
    
    @Override
    public List<List<ParamType>> getParamTypeLists() {
        return typeLists(
            types(ParamType.STRING, ParamType.STRING, ParamType.OUTPUT),
            types(ParamType.STRING, ParamType.STRING, ParamType.STRING, ParamType.OUTPUT)
        );
    }
}
