package cn.langlang.yuweb.functions.util;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;
import java.util.Map;

public class MhasFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "mhas";
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
        Object mapObj = arguments.get(0);
        String key = arguments.get(1) != null ? arguments.get(1).toString() : "";
        
        if (mapObj instanceof Map) {
            return ((Map<String, Object>) mapObj).containsKey(key);
        }
        return false;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return null;
    }
}
