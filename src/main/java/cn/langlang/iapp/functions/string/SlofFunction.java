package cn.langlang.iapp.functions.string;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class SlofFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "slof";
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
        String source = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String target = arguments.get(1) != null ? arguments.get(1).toString() : "";
        int fromIndex = source.length() - 1;
        if (arguments.size() > 2 && arguments.get(2) instanceof Number) {
            fromIndex = ((Number) arguments.get(2)).intValue();
        }
        return (long) source.lastIndexOf(target, fromIndex);
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.OUTPUT);
    }
    
    @Override
    public List<List<ParamType>> getParamTypeLists() {
        return typeLists(
            types(ParamType.STRING, ParamType.STRING, ParamType.OUTPUT),
            types(ParamType.STRING, ParamType.STRING, ParamType.INT, ParamType.OUTPUT)
        );
    }
}
