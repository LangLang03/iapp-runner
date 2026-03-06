package cn.langlang.yuweb.functions.server.request;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.RequestContext;

import java.util.List;

public class ClientIpFunction extends AbstractFunction {
    private RequestContext requestContext;
    
    public ClientIpFunction(RequestContext requestContext) {
        this.requestContext = requestContext;
    }
    
    @Override
    public String getName() {
        return "clientIp";
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
        return requestContext.clientIp();
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return null;
    }
}
