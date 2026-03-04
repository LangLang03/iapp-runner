package cn.langlang.iapp.functions.math;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class SModFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "s%";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return 3;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Object a = arguments.get(0);
        Object b = arguments.get(1);
        
        if (a instanceof Number && b instanceof Number) {
            return ((Number) a).longValue() % ((Number) b).longValue();
        }
        
        return 0;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OBJECT, ParamType.OBJECT, ParamType.OUTPUT);
    }
}
