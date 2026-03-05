package cn.langlang.iapp.functions.list;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class SslistFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "sslist";
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
        Object list = arguments.get(0);
        int index = toInt(arguments.get(1));
        Object value = arguments.size() > 2 ? arguments.get(2) : null;
        
        if (list instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> lst = (List<Object>) list;
            if (index >= 0 && index < lst.size()) {
                lst.set(index, value);
            }
        }
        return null;
    }
    
    private int toInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OBJECT, ParamType.INT, ParamType.OBJECT);
    }
}
