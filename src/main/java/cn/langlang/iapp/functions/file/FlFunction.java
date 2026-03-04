package cn.langlang.iapp.functions.file;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FlFunction extends AbstractFunction {
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
        String path = arguments.get(0) != null ? arguments.get(0).toString() : "";
        path = context.resolvePath(path);
        
        Boolean filterType = null;
        if (arguments.size() > 1 && arguments.get(1) instanceof Boolean) {
            filterType = (Boolean) arguments.get(1);
        }
        
        try {
            File file = new File(path);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files == null) {
                    return new Object[0];
                }
                
                if (filterType == null) {
                    return files;
                }
                
                List<File> filtered = new ArrayList<>();
                for (File f : files) {
                    if (filterType && f.isDirectory()) {
                        filtered.add(f);
                    } else if (!filterType && f.isFile()) {
                        filtered.add(f);
                    }
                }
                return filtered.toArray(new File[0]);
            }
            return new Object[0];
        } catch (Exception e) {
            return new Object[0];
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.OUTPUT);
    }
    
    @Override
    public List<List<ParamType>> getParamTypeLists() {
        return typeLists(
            types(ParamType.STRING, ParamType.OUTPUT),
            types(ParamType.STRING, ParamType.BOOLEAN, ParamType.OUTPUT)
        );
    }
}
