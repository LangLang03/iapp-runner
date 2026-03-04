package cn.langlang.iapp.runtime;

import java.util.List;

public interface IFunction {
    String getName();
    
    int getMinParameters();
    
    int getMaxParameters();
    
    Object call(RuntimeContext context, List<Object> arguments) throws FunctionException;
    
    boolean isSupported();
    
    String getUnsupportedReason();
    
    List<ParamType> getParamTypes();
}
