package cn.langlang.iapp.functions.array;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class SgszlFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "sgszl";
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
        Object array = arguments.get(0);
        
        if (array instanceof Object[]) {
            Object[] arr = (Object[]) array;
            return (long) arr.length;
        } else if (array instanceof List) {
            List<?> list = (List<?>) array;
            return (long) list.size();
        }
        return 0L;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.ARRAY, ParamType.OUTPUT);
    }
}
