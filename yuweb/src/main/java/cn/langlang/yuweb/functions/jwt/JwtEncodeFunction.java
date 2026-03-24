package cn.langlang.yuweb.functions.jwt;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.jwt.JwtManager;

import java.util.List;
import java.util.Map;

public class JwtEncodeFunction extends AbstractFunction {
    
    @Override
    public String getName() {
        return "jwtencode";
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
        Object payloadObj = arguments.get(0);
        String secret = arguments.get(1) != null ? arguments.get(1).toString() : "";
        
        if (!(payloadObj instanceof Map)) {
            throw new FunctionException("jwtEncode() requires a map payload");
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) payloadObj;
        
        return JwtManager.encode(payload, secret);
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OBJECT, ParamType.STRING);
    }
}
