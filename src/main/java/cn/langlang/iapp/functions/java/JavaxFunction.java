package cn.langlang.iapp.functions.java;

import bsh.Interpreter;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class JavaxFunction implements IFunction {
    @Override
    public String getName() {
        return "javax";
    }
    
    @Override
    public int getMinParameters() {
        return 3;
    }
    
    @Override
    public int getMaxParameters() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Object resultVar = arguments.get(0);
        Object targetObj = arguments.get(1);
        String methodName = toString(arguments.get(2));
        
        try {
            Interpreter interpreter = context.getBeanShellInterpreter();
            
            Object[] args = null;
            if (arguments.size() > 3) {
                args = new Object[arguments.size() - 3];
                for (int i = 3; i < arguments.size(); i++) {
                    args[i - 3] = arguments.get(i);
                    interpreter.set("_arg" + (i - 3), arguments.get(i));
                }
            }
            
            StringBuilder call = new StringBuilder();
            if (targetObj != null) {
                interpreter.set("_target", targetObj);
                call.append("_target.");
            }
            call.append(methodName).append("(");
            
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    if (i > 0) call.append(", ");
                    call.append("_arg").append(i);
                }
            }
            call.append(")");
            
            return interpreter.eval(call.toString());
        } catch (Exception e) {
            throw new FunctionException("Javax function call failed: " + methodName, e);
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
