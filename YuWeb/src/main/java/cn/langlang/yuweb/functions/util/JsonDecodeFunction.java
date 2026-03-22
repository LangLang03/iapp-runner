package cn.langlang.yuweb.functions.util;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class JsonDecodeFunction extends AbstractFunction {
    private static final Gson gson = new Gson();
    
    @Override
    public String getName() {
        return "jsondecode";
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
        String json = arguments.get(0) != null ? arguments.get(0).toString() : "";
        
        try {
            return gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return null;
    }
}
