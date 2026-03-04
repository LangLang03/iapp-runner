package cn.langlang.iapp.functions.math;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.text.DecimalFormat;
import java.util.List;

public class S2Function implements IFunction {
    private ScriptEngine engine;
    private boolean engineAvailable = false;
    
    public S2Function() {
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
        return "s2";
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
        double value;
        
        if (arg instanceof Number) {
            value = ((Number) arg).doubleValue();
        } else {
            String expression = toString(arg);
            if (engineAvailable && engine != null) {
                try {
                    Object result = engine.eval(expression);
                    if (result instanceof Number) {
                        value = ((Number) result).doubleValue();
                    } else {
                        value = Double.parseDouble(toString(result));
                    }
                } catch (Exception e) {
                    value = Double.parseDouble(expression);
                }
            } else {
                value = Double.parseDouble(expression);
            }
        }
        
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(value);
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
