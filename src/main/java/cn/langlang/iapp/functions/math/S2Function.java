package cn.langlang.iapp.functions.math;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class S2Function extends AbstractFunction {
    @Override
    public String getName() {
        return "s2";
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
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Object value = arguments.get(0);
        double result;
        if (value instanceof Number) {
            result = ((Number) value).doubleValue();
        } else {
            try {
                result = Double.parseDouble(String.valueOf(value));
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return Math.round(result * 100.0) / 100.0;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OBJECT, ParamType.OUTPUT);
    }
}
