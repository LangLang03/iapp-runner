package cn.langlang.yuweb.functions.session;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.session.SessionManager;
import cn.langlang.yuweb.web.RequestContext;

import java.util.List;

public class HasSessionFunction extends AbstractFunction {
    
    @Override
    public String getName() {
        return "hasSession";
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
            return false;
        }
        
        String key = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String sessionId = requestContext.getSessionId();
        
        if (sessionId == null || sessionId.isEmpty()) {
            return false;
        }
        
        return SessionManager.getInstance().has(sessionId, key);
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING);
    }
}
