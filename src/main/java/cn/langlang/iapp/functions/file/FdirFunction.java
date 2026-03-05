package cn.langlang.iapp.functions.file;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class FdirFunction extends AbstractFunction {
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
    public Object call(RuntimeContext context, List<Object> arguments) {
        if (arguments.isEmpty()) {
            return context.getCurrentDirectory();
        }
        
        String type = toString(arguments.get(0));
        return switch (type.toLowerCase()) {
            case "app" -> context.getCurrentDirectory();
            case "data" -> context.getCurrentDirectory() + "/data";
            case "cache" -> context.getCurrentDirectory() + "/cache";
            case "files" -> context.getCurrentDirectory() + "/files";
            case "external" -> System.getProperty("user.dir");
            default -> context.resolvePath(type);
        };
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OUTPUT);
    }
    
    @Override
    public List<List<ParamType>> getParamTypeLists() {
        return typeLists(
            types(ParamType.OUTPUT),
            types(ParamType.STRING, ParamType.OUTPUT)
        );
    }
}
