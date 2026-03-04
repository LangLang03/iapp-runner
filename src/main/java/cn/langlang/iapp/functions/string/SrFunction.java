package cn.langlang.iapp.functions.string;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class SrFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "sr";
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
        String source = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String target = arguments.get(1) != null ? arguments.get(1).toString() : "";
        String replacement = arguments.get(2) != null ? arguments.get(2).toString() : "";
        return source.replace(target, replacement);
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.STRING, ParamType.OUTPUT);
    }
}
