package cn.langlang.iapp.functions.other;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.Arrays;
import java.util.List;

public class CallFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "call";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        if (arguments.size() < 2) {
            throw new FunctionException("call function requires at least 2 arguments");
        }
        
        String module = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String method = arguments.get(1) != null ? arguments.get(1).toString() : "";
        
        Object[] args = null;
        if (arguments.size() > 2) {
            args = new Object[arguments.size() - 2];
            for (int i = 2; i < arguments.size(); i++) {
                args[i - 2] = arguments.get(i);
            }
        }
        
        try {
            return context.executeMjavaMethod(module, method, args);
        } catch (Exception e) {
            throw new FunctionException("Failed to call " + module + "." + method + ": " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.OBJECT);
    }
}
