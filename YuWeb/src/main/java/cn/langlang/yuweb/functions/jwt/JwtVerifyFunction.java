package cn.langlang.yuweb.functions.jwt;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.jwt.JwtManager;

import java.util.List;
import java.util.Map;

public class JwtVerifyFunction extends AbstractFunction {
    
    @Override
    public String getName() {
        return "jwtVerify";
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
        String token = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String secret = arguments.get(1) != null ? arguments.get(1).toString() : "";
        
        Map<String, Object> payload = JwtManager.verify(token, secret);
        return payload;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING);
    }
}
