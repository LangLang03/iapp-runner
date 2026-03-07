package cn.langlang.yuweb.functions.server.response;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.web.RequestContext;

import java.util.List;

public class JsonFunction extends AbstractFunction {
    private RequestContext requestContext;
    
    public JsonFunction(RequestContext requestContext) {
        this.requestContext = requestContext;
    }
    
    @Override
    public String getName() {
        return "json";
    }
    
    @Override
    public int getMinParameters() {
        return 0;
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
        
        if (arguments.isEmpty()) {
            return requestContext.json();
        }
        
        Object data = arguments.get(0);
        requestContext.json(data);
        return null;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return null;
    }
}
