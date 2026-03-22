package cn.langlang.yuweb.functions.util;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import com.google.gson.Gson;

import java.util.List;

public class JsonEncodeFunction extends AbstractFunction {
    private static final Gson gson = new Gson();
    
    @Override
    public String getName() {
        return "jsonencode";
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
        Object obj = arguments.get(0);
        return gson.toJson(obj);
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return null;
    }
}
