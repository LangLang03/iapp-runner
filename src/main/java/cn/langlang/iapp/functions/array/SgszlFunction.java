package cn.langlang.iapp.functions.array;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class SgszlFunction implements IFunction {
    @Override
    public String getName() {
        return "sgszl";
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
            return (long) ((Object[]) array).length;
        } else if (array instanceof List) {
            return (long) ((List<?>) array).size();
        }
        return 0L;
    }
    
    @Override
    public boolean isSupported() {
        return true;
    }
    
    @Override
    public String getUnsupportedReason() {
        return null;
    }
}
