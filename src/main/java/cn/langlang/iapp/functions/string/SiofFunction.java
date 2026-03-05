package cn.langlang.iapp.functions.string;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class SiofFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "siof";
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
    public Object call(RuntimeContext context, List<Object> arguments) {
        String source = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String target = arguments.get(1) != null ? arguments.get(1).toString() : "";
        int fromIndex = 0;
        if (arguments.size() > 2 && arguments.get(2) instanceof Number) {
            fromIndex = ((Number) arguments.get(2)).intValue();
        }
        return (long) source.indexOf(target, fromIndex);
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.INT, ParamType.OUTPUT);
    }
}
