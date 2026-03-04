package cn.langlang.iapp.functions.string;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.net.URLDecoder;
import java.util.List;

public class Sutf8toFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "sutf8to";
    }
    
    @Override
    public int getMinParameters() {
        return 1;
    }
    
    @Override
    public int getMaxParameters() {
        return 4;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String str = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String charsetName = "UTF-8";
        
        if (arguments.size() > 1 && arguments.get(1) != null) {
            Object arg1 = arguments.get(1);
            if (arg1 instanceof String && !(arg1 instanceof Boolean)) {
                charsetName = arg1.toString();
            }
        }
        
        try {
            return URLDecoder.decode(str, charsetName);
        } catch (Exception e) {
            throw new FunctionException("URL decoding failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.BOOLEAN, ParamType.OUTPUT);
    }
}
