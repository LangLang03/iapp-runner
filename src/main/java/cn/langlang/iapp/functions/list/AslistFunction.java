package cn.langlang.iapp.functions.list;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AslistFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "aslist";
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
        Object array = arguments.get(0);
        if (array instanceof Object[]) {
            return new ArrayList<>(Arrays.asList((Object[]) array));
        } else if (array instanceof List) {
            return new ArrayList<>((List<?>) array);
        }
        List<Object> result = new ArrayList<>();
        result.add(array);
        return result;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.ARRAY, ParamType.OUTPUT);
    }
}
