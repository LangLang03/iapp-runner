package cn.langlang.yuweb.functions.server.request;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.web.RequestContext;


import java.util.List;

public class PostFunction extends AbstractFunction {
    private RequestContext requestContext;
    
    public PostFunction(RequestContext requestContext) {
        this.requestContext = requestContext;
    }
    
    @Override
    public String getName() {
        return "post";
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
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        if (requestContext == null) {
            return null;
        }
        
        String name = arguments.get(0) != null ? arguments.get(0).toString() : "";
        
        if (arguments.size() > 1) {
            String defaultValue = arguments.get(1) != null ? arguments.get(1).toString() : "";
            return requestContext.post(name, defaultValue);
        }
        
        return requestContext.post(name);
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING);
    }
}
