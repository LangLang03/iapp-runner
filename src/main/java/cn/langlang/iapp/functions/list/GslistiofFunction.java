package cn.langlang.iapp.functions.list;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class GslistiofFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "gslistiof";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return 2;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Object list = arguments.get(0);
        Object value = arguments.get(1);
        
        if (list instanceof List) {
            return (long) ((List<?>) list).indexOf(value);
        }
        return -1L;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OBJECT, ParamType.OBJECT);
    }
}
