package cn.langlang.iapp.functions.java;

import bsh.Interpreter;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class JavanewFunction implements IFunction {
    @Override
    public String getName() {
        return "javanew";
    }
    
    @Override
    public int getMinParameters() {
        return 1;
    }
    
    @Override
    public int getMaxParameters() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String className = toString(arguments.get(0));
        
        try {
            Interpreter interpreter = context.getBeanShellInterpreter();
            
            Class<?> clazz = Class.forName(className);
            
            Object[] args = null;
            if (arguments.size() > 1) {
                args = new Object[arguments.size() - 1];
                for (int i = 1; i < arguments.size(); i++) {
                    args[i - 1] = arguments.get(i);
                    interpreter.set("_arg" + (i - 1), arguments.get(i));
                }
            }
            
            StringBuilder call = new StringBuilder();
            call.append("new ").append(className).append("(");
            
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    if (i > 0) call.append(", ");
                    call.append("_arg").append(i);
                }
            }
            call.append(")");
            
            return interpreter.eval(call.toString());
        } catch (Exception e) {
            throw new FunctionException("Failed to create instance of: " + className, e);
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
