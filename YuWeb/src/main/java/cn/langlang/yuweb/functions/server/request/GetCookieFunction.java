package cn.langlang.yuweb.functions.server.request;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.web.RequestContext;

import java.util.List;

public class GetCookieFunction extends AbstractFunction {
    
    @Override
    public String getName() {
        return "getcookie";
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
        RequestContext requestContext = context.getRequestContext();
        if (requestContext == null) {
            return null;
        }
        String name = arguments.get(0) != null ? arguments.get(0).toString() : "";
        return requestContext.getCookie(name);
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return null;
    }
}
