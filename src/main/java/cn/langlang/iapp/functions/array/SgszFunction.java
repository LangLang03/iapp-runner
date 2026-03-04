package cn.langlang.iapp.functions.array;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class SgszFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "sgsz";
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
        Object array = arguments.get(0);
        int index = toInt(arguments.get(1));
        
        if (array instanceof Object[]) {
            Object[] arr = (Object[]) array;
            if (index >= 0 && index < arr.length) {
                return arr[index];
            }
        } else if (array instanceof List) {
            List<?> list = (List<?>) array;
            if (index >= 0 && index < list.size()) {
                return list.get(index);
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
        return types(ParamType.ARRAY, ParamType.INT, ParamType.OUTPUT);
    }
}
