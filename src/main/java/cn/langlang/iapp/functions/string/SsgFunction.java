package cn.langlang.iapp.functions.string;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class SsgFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "ssg";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return 4;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) {
        String str = arguments.get(0) != null ? arguments.get(0).toString() : "";
        int start = toInt(arguments.get(1));
        int end = str.length();
        if (arguments.size() > 2) {
            end = toInt(arguments.get(2));
        }
        return str.substring(start, Math.min(end, str.length()));
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
        return types(ParamType.STRING, ParamType.INT, ParamType.INT, ParamType.OUTPUT);
    }
}
