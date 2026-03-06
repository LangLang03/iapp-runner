package cn.langlang.yuweb.functions.auth;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerifyFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "verify";
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
        String token = arguments.get(0) != null ? arguments.get(0).toString() : "";
        
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> tokenData = LoginFunction.getTokenData(token);
        
        if (tokenData == null) {
            result.put("valid", false);
            result.put("msg", "Token 无效");
            return result;
        }
        
        result.put("valid", true);
        result.put("userId", tokenData.get("userId"));
        result.put("username", tokenData.get("username"));
        
        return result;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING);
    }
}
