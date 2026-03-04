package cn.langlang.iapp.functions.math;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.List;

public class SFunction implements IFunction {
    private ScriptEngine engine;
    private boolean engineAvailable = false;
    
    public SFunction() {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            this.engine = manager.getEngineByName("js");
            this.engineAvailable = (engine != null);
        } catch (Exception e) {
            this.engineAvailable = false;
        }
    }
    
    @Override
    public String getName() {
        return "s";
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
        Object arg = arguments.get(0);
        
        if (arg instanceof Number) {
            return ((Number) arg).longValue();
        }
        
        String expression = toString(arg);
        
        if (engineAvailable && engine != null) {
            try {
                Object result = engine.eval(expression);
                if (result instanceof Number) {
                    return ((Number) result).longValue();
                }
                return result;
            } catch (Exception e) {
                try {
                    return Long.parseLong(expression);
                } catch (NumberFormatException ex) {
                    try {
                        return (long) Double.parseDouble(expression);
                    } catch (NumberFormatException ex2) {
                        throw new FunctionException("Failed to evaluate expression: " + expression);
                    }
                }
            }
        } else {
            try {
                return Long.parseLong(expression);
            } catch (NumberFormatException ex) {
                try {
                    return (long) Double.parseDouble(expression);
                } catch (NumberFormatException ex2) {
                    throw new FunctionException("Failed to evaluate expression: " + expression);
                }
            }
        }
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
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
