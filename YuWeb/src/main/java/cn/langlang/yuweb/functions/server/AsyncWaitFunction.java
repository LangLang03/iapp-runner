package cn.langlang.yuweb.functions.server;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class AsyncWaitFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "asyncwait";
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
    public List<ParamType> getParamTypes() {
        return types(ParamType.LONG, ParamType.LONG);
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        long taskId = toLong(arguments.get(0));
        long timeout = arguments.size() > 1 ? toLong(arguments.get(1)) : 30000;
        
        long startTime = System.currentTimeMillis();
        
        while (!AsyncFunction.isAsyncComplete(taskId)) {
            if (System.currentTimeMillis() - startTime > timeout) {
                AsyncFunction.cancelAsync(taskId);
                throw new FunctionException("Async task timeout: " + taskId);
            }
            
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new FunctionException("Async wait interrupted", e);
            }
        }
        
        try {
            return AsyncFunction.getAsyncResult(taskId);
        } catch (RuntimeException e) {
            throw new FunctionException(e.getMessage(), e);
        }
    }
    
    private long toLong(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
