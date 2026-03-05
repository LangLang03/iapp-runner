package cn.langlang.iapp.functions.list;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class DslistFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "dslist";
    }
    
    @Override
    public int getMinParameters() {
        return 1;
    }
    
    @Override
    public int getMaxParameters() {
        return 2;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) {
        Object list = arguments.get(0);
        if (list instanceof List<?> lst) {
            if (arguments.size() > 1) {
                int index = toInt(arguments.get(1));
                if (index >= 0 && index < lst.size()) {
                    lst.remove(index);
                }
            } else {
                lst.clear();
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
        return types(ParamType.OBJECT, ParamType.INT);
    }
}
