package cn.langlang.iapp.functions.other;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

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
            throw new FunctionException("call 函数至少需要 2 个参数");
        }
        
        String language = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String fullMethodName = arguments.get(1) != null ? arguments.get(1).toString() : "";
        
        Object[] args = null;
        if (arguments.size() > 2) {
            args = new Object[arguments.size() - 2];
            for (int i = 2; i < arguments.size(); i++) {
                args[i - 2] = arguments.get(i);
            }
        }
        
        String moduleName = "";
        String methodName = fullMethodName;
        
        int dotIndex = fullMethodName.lastIndexOf('.');
        if (dotIndex > 0) {
            moduleName = fullMethodName.substring(0, dotIndex);
            methodName = fullMethodName.substring(dotIndex + 1);
        }
        
        try {
            return context.executeMjavaMethod(moduleName, methodName, args);
        } catch (Exception e) {
            throw new FunctionException("调用 " + language + "." + fullMethodName + " 失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OUTPUT, ParamType.STRING, ParamType.STRING, ParamType.OBJECT);
    }
}
