package cn.langlang.yuweb.functions.server.response;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.web.RequestContext;

import java.util.List;

public class ErrorFunction extends AbstractFunction {
    
    @Override
    public String getName() {
        return "error";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return 2;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        RequestContext requestContext = context.getRequestContext();
        if (requestContext == null) {
            return null;
        }
        int code = 500;
        if (arguments.get(0) instanceof Number) {
            code = ((Number) arguments.get(0)).intValue();
        }
        String message = arguments.get(1) != null ? arguments.get(1).toString() : "";
        requestContext.error(code, message);
        return null;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.INT, ParamType.STRING);
    }
}
