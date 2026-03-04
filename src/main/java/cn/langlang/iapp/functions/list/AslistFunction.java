package cn.langlang.iapp.functions.list;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.ArrayList;
import java.util.List;

public class AslistFunction implements IFunction {
    @Override
    public String getName() {
        return "aslist";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return 3;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Object listObj = arguments.get(0);
        Object data = arguments.get(1);
        int index = -1;
        
        if (arguments.size() >= 3) {
            index = toInt(arguments.get(2));
        }
        
        List<Object> list = toList(listObj);
        
        if (index >= 0 && index < list.size()) {
            list.add(index, data);
        } else {
            list.add(data);
        }
        
        return null;
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
    
    private int toInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
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
