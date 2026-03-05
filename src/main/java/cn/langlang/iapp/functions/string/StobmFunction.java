package cn.langlang.iapp.functions.string;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.net.URLEncoder;
import java.util.List;

public class StobmFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "stobm";
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
        boolean forUrl = false;
        
        if (arguments.size() > 1 && arguments.get(1) != null) {
            Object arg1 = arguments.get(1);
            if (arg1 instanceof Boolean) {
                forUrl = (Boolean) arg1;
            } else {
                charsetName = arg1.toString();
            }
        }
        
        if (arguments.size() > 2) {
            forUrl = toBoolean(arguments.get(2));
        }
        
        try {
            String encoded = URLEncoder.encode(str, charsetName);
            if (forUrl) {
                return encoded;
            }
            return encoded;
        } catch (Exception e) {
            throw new FunctionException("URL 编码失败: " + e.getMessage(), e);
        }
    }
    
    private boolean toBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.OUTPUT);
    }
    
    @Override
    public List<List<ParamType>> getParamTypeLists() {
        return typeLists(
            types(ParamType.STRING, ParamType.STRING, ParamType.OUTPUT),
            types(ParamType.STRING, ParamType.STRING, ParamType.BOOLEAN, ParamType.OUTPUT)
        );
    }
}
