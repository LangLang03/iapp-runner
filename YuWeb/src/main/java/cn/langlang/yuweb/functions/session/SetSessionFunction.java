package cn.langlang.yuweb.functions.session;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.session.SessionManager;
import cn.langlang.yuweb.web.RequestContext;

import java.util.List;

public class SetSessionFunction extends AbstractFunction {
    
    @Override
    public String getName() {
        return "setSession";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return 3;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        RequestContext requestContext = context.getRequestContext();
        if (requestContext == null) {
            return false;
        }
        
        String key = arguments.get(0) != null ? arguments.get(0).toString() : "";
        Object value = arguments.get(1);
        
        String sessionId = requestContext.getSessionId();
        SessionManager sessionManager = SessionManager.getInstance();
        
        // Create session if not exists
        if (sessionId == null || sessionId.isEmpty() || !sessionManager.exists(sessionId)) {
            if (arguments.size() > 2 && arguments.get(2) != null) {
                long ttl = toLong(arguments.get(2));
                sessionId = sessionManager.createSession(ttl);
            } else {
                sessionId = sessionManager.createSession();
            }
            requestContext.setSessionId(sessionId);
        }
        
        if (arguments.size() > 2 && arguments.get(2) != null) {
            long ttl = toLong(arguments.get(2));
            sessionManager.set(sessionId, key, value, ttl);
        } else {
            sessionManager.set(sessionId, key, value);
        }
        
        return true;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.OBJECT, ParamType.LONG);
    }
    
    private long toLong(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
