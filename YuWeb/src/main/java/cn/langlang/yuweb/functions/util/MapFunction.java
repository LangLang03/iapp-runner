package cn.langlang.yuweb.functions.util;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "map";
    }
    
    @Override
    public int getMinParameters() {
        return 0;
    }
    
    @Override
    public int getMaxParameters() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Map<String, Object> map = new HashMap<>();
        if (arguments == null || arguments.isEmpty()) {
            return map;
        }
        for (int i = 0; i + 1 < arguments.size(); i += 2) {
            Object keyObj = arguments.get(i);
            Object valueObj = arguments.get(i + 1);
            if (keyObj != null) {
                String key = keyObj.toString();
                map.put(key, valueObj);
            }
        }
        return map;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return null;
    }
}
