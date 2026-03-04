package cn.langlang.iapp.functions.list;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class GslistlFunction implements IFunction {
    @Override
    public String getName() {
        return "gslistl";
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
        Object listObj = arguments.get(0);
        
        List<Object> list = toList(listObj);
        
        return (long) list.size();
    }
    
    @SuppressWarnings("unchecked")
    private List<Object> toList(Object obj) throws FunctionException {
        if (obj == null) {
            throw new FunctionException("List object is null");
        }
        if (obj instanceof List) {
            return (List<Object>) obj;
        }
        throw new FunctionException("Object is not a list: " + obj.getClass().getName());
    }
    
    @Override
    public boolean isSupported() {
        return true;
    }
    
    @Override
    public String getUnsupportedReason() {
        return null;
    }
}
