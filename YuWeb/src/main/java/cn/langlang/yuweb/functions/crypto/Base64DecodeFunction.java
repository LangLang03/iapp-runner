package cn.langlang.yuweb.functions.crypto;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class Base64DecodeFunction extends AbstractFunction {
    
    @Override
    public String getName() {
        return "base64Decode";
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
        String data = arguments.get(0) != null ? arguments.get(0).toString() : "";
        
        try {
            byte[] decoded = Base64.getDecoder().decode(data);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new FunctionException("Invalid Base64 string: " + e.getMessage());
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING);
    }
}
