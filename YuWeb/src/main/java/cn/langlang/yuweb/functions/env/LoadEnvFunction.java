package cn.langlang.yuweb.functions.env;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.env.EnvManager;

import java.util.List;

public class LoadEnvFunction extends AbstractFunction {
    
    @Override
    public String getName() {
        return "loadenv";
    }
    
    @Override
    public int getMinParameters() {
        return 0;
    }
    
    @Override
    public int getMaxParameters() {
        return 1;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        EnvManager envManager = EnvManager.getInstance();
        
        if (arguments.isEmpty() || arguments.get(0) == null) {
            return envManager.loadEnvFile();
        }
        
        String path = arguments.get(0).toString();
        return envManager.loadEnvFile(path);
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING);
    }
}
