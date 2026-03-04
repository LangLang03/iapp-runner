package cn.langlang.iapp.functions.other;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class CallFunction implements IFunction {
    @Override
    public String getName() {
        return "call";
    }
    
    @Override
    public int getMinParameters() {
        return 3;
    }
    
    @Override
    public int getMaxParameters() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Object resultVar = arguments.get(0);
        String callType = toString(arguments.get(1));
        String target = toString(arguments.get(2));
        
        Object[] args = null;
        if (arguments.size() > 3) {
            args = new Object[arguments.size() - 3];
            for (int i = 3; i < arguments.size(); i++) {
                args[i - 3] = arguments.get(i);
            }
        }
        
        try {
            if ("mjava".equalsIgnoreCase(callType)) {
                if (target.contains(".")) {
                    String[] parts = target.split("\\.", 2);
                    String moduleName = parts[0];
                    String methodName = parts[1];
                    return context.executeMjavaMethod(moduleName, methodName, args);
                }
            }
            
            return null;
        } catch (Exception e) {
            throw new FunctionException("Call failed: " + target, e);
        }
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
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
