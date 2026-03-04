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
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return 2;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Object array = arguments.get(0);
        int index = toInt(arguments.get(1));
        
        if (array instanceof Object[]) {
            Object[] arr = (Object[]) array;
            if (index >= 0 && index < arr.length) {
                Object val = arr[index];
                if (val instanceof Number) {
                    return ((Number) val).longValue();
                }
                try {
                    return Long.parseLong(String.valueOf(val));
                } catch (NumberFormatException e) {
                    return 0L;
                }
            }
        } else if (array instanceof List) {
            List<?> list = (List<?>) array;
            if (index >= 0 && index < list.size()) {
                Object val = list.get(index);
                if (val instanceof Number) {
                    return ((Number) val).longValue();
                }
                try {
                    return Long.parseLong(String.valueOf(val));
                } catch (NumberFormatException e) {
                    return 0L;
                }
            }
        }
        return 0L;
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
        return types(ParamType.ARRAY, ParamType.INT);
    }
}
