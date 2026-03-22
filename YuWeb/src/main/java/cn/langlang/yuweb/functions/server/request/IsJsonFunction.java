package cn.langlang.yuweb.functions.server.request;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.web.RequestContext;

import java.util.List;

public class IsJsonFunction extends AbstractFunction {
    
    @Override
    public String getName() {
        return "isjson";
    }
    
    @Override
    public int getMinParameters() {
        return 0;
    }
    
    @Override
    public int getMaxParameters() {
        return 0;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        RequestContext requestContext = context.getRequestContext();
        if (requestContext == null) {
            return false;
        }
        return requestContext.isJson();
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return null;
    }
}
