package cn.langlang.iapp.functions.string;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class SjFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "sj";
    }
    
    @Override
    public int getMinParameters() {
        return 3;
    }
    
    @Override
    public int getMaxParameters() {
        return 4;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        if (arguments.size() < 3) {
            throw new FunctionException("sj function requires at least 3 arguments");
        }
        
        String source = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String prefix = arguments.get(1) != null ? arguments.get(1).toString() : null;
        String suffix = arguments.get(2) != null ? arguments.get(2).toString() : null;
        
        int startIndex = 0;
        int endIndex = source.length();
        
        if (prefix != null) {
            int pos = source.indexOf(prefix);
            if (pos == -1) {
                return "";
            }
            startIndex = pos + prefix.length();
        }
        
        if (suffix != null) {
            int pos = source.indexOf(suffix, startIndex);
            if (pos == -1) {
                return "";
            }
            endIndex = pos;
        }
        
        return source.substring(startIndex, endIndex);
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.STRING, ParamType.OUTPUT);
    }
}
