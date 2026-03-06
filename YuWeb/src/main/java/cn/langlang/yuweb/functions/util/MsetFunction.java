package cn.langlang.yuweb.functions.util;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;
import java.util.Map;

public class MsetFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "mset";
    }
    
    @Override
    public int getMinParameters() {
        return 3;
    }
    
    @Override
    public int getMaxParameters() {
        return 3;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Object mapObj = arguments.get(0);
        String key = arguments.get(1) != null ? arguments.get(1).toString() : "";
        Object value = arguments.get(2);
        
        if (mapObj instanceof Map) {
            ((Map<String, Object>) mapObj).put(key, value);
        }
        return null;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return null;
    }
}
