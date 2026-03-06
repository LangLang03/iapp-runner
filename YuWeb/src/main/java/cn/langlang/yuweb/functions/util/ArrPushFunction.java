package cn.langlang.yuweb.functions.util;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.ArrayList;
import java.util.List;

public class ArrPushFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "arrpush";
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
        Object arrObj = arguments.get(0);
        Object value = arguments.get(1);
        
        if (arrObj instanceof List) {
            List<Object> list = new ArrayList<>((List<Object>) arrObj);
            list.add(value);
            return list;
        }
        
        List<Object> list = new ArrayList<>();
        list.add(value);
        return list;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return null;
    }
}
