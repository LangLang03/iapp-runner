package cn.langlang.iapp.functions.list;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class GslistszFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "gslistsz";
    }
    
    @Override
    public int getMinParameters() {
        return 1;
    }
    
    @Override
    public int getMaxParameters() {
        return 1;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Object list = arguments.get(0);
        if (list instanceof List) {
            return ((List<?>) list).toArray();
        }
        return new Object[0];
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OBJECT);
    }
}
