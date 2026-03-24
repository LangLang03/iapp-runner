package cn.langlang.yuweb.functions.util;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

public class LengthFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "length";
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
        Object obj = arguments.get(0);
        
        if (obj == null) {
            return 0;
        }
        
        if (obj instanceof String) {
            return ((String) obj).length();
        }
        
        if (obj instanceof List) {
            return ((List<?>) obj).size();
        }
        
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).size();
        }
        
        if (obj.getClass().isArray()) {
            return Array.getLength(obj);
        }
        
        return 0;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return null;
    }
}
