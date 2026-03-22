package cn.langlang.yuweb.functions.jwt;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.jwt.JwtManager;

import java.util.List;
import java.util.Map;

public class JwtDecodeFunction extends AbstractFunction {
    
    @Override
    public String getName() {
        return "jwtDecode";
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
        
        Map<String, Object> payload = JwtManager.decode(token);
        return payload;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING);
    }
}
