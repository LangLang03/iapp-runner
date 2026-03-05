package cn.langlang.iapp.functions.output;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class SysoFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "syso";
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
    public Object call(RuntimeContext context, List<Object> arguments) {
        Object value = arguments.get(0);
        System.out.println(value != null ? value.toString() : "null");
        return null;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OBJECT);
    }
}
