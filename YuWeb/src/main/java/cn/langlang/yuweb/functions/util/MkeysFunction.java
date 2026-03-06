package cn.langlang.yuweb.functions.util;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MkeysFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "mkeys";
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
        Object mapObj = arguments.get(0);
        
        if (mapObj instanceof Map) {
            Set<String> keys = ((Map<String, Object>) mapObj).keySet();
            return new ArrayList<>(keys);
        }
        return new ArrayList<>();
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return null;
    }
}
