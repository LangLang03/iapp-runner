package cn.langlang.yuweb.functions.server.response;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.RequestContext;

import java.util.List;

public class StatusFunction extends AbstractFunction {
    private RequestContext requestContext;
    
    public StatusFunction(RequestContext requestContext) {
        this.requestContext = requestContext;
    }
    
    @Override
    public String getName() {
        return "status";
    }
    
    @Override
    public int getMinParameters() {
        return 1;
    }
    
    @Override
    public int getMaxParameters() {
        return 1;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        if (requestContext == null) {
            return null;
        }
        int code = 200;
        if (arguments.get(0) instanceof Number) {
            code = ((Number) arguments.get(0)).intValue();
        }
        requestContext.status(code);
        return null;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.INT);
    }
}
