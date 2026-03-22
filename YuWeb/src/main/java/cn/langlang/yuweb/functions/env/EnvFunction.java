package cn.langlang.yuweb.functions.env;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.env.EnvManager;

import java.util.List;

public class EnvFunction extends AbstractFunction {
    
    @Override
    public String getName() {
        return "env";
    }
    
    @Override
    public int getMinParameters() {
        return 1;
    }
    
    @Override
    public int getMaxParameters() {
        return 2;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String key = arguments.get(0) != null ? arguments.get(0).toString() : "";
        
        if (arguments.size() > 1) {
            String defaultValue = arguments.get(1) != null ? arguments.get(1).toString() : "";
            return EnvManager.getInstance().get(key, defaultValue);
        }
        
        return EnvManager.getInstance().get(key);
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING);
    }
}
