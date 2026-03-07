package cn.langlang.yuweb.functions.server.request;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.web.RequestContext;

import java.util.List;

public class SetCookieFunction extends AbstractFunction {
    private RequestContext requestContext;
    
    public SetCookieFunction(RequestContext requestContext) {
        this.requestContext = requestContext;
    }
    
    @Override
    public String getName() {
        return "setCookie";
    }
    
    @Override
    public int getMinParameters() {
        return 3;
    }
    
    @Override
    public int getMaxParameters() {
        return 3;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        if (requestContext == null) {
            return null;
        }
        String name = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String value = arguments.get(1) != null ? arguments.get(1).toString() : "";
        int maxAge = 0;
        if (arguments.get(2) instanceof Number) {
            maxAge = ((Number) arguments.get(2)).intValue();
        }
        requestContext.setCookie(name, value, maxAge);
        return null;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return null;
    }
}
