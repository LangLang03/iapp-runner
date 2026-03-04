package cn.langlang.iapp.functions.other;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class StopFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "stop";
    }
    
    @Override
    public int getMinParameters() {
        return 0;
    }
    
    @Override
    public int getMaxParameters() {
        return 1;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        if (arguments.isEmpty()) {
            context.requestEndCode();
        } else {
            Object arg = arguments.get(0);
            if (arg instanceof Number) {
                try {
                    Thread.sleep(((Number) arg).longValue());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                context.requestEndCode();
            }
        }
        return null;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.INT);
    }
}
