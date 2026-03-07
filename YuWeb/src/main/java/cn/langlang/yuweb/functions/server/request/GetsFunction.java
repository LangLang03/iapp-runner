package cn.langlang.yuweb.functions.server.request;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.web.RequestContext;

import java.util.List;

public class GetsFunction extends AbstractFunction {
    private RequestContext requestContext;
    
    public GetsFunction(RequestContext requestContext) {
        this.requestContext = requestContext;
    }
    
    @Override
    public String getName() {
        return "gets";
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
        if (requestContext == null) {
            return null;
        }
        return requestContext.gets();
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return null;
    }
}
