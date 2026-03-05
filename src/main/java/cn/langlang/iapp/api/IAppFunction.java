package cn.langlang.iapp.api;

import cn.langlang.iapp.ast.FunctionDefinitionStatement;
import cn.langlang.iapp.interpreter.Interpreter;
import cn.langlang.iapp.interpreter.InterpreterException;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IAppFunction {
    
    private final String name;
    private final int minParameters;
    private final int maxParameters;
    private final List<ParamType> paramTypes;
    private final List<List<ParamType>> paramTypeLists;
    private final IAppFunctionHandler handler;
    private final IFunction wrappedFunction;
    private final boolean isUserFunction;
    private final FunctionDefinitionStatement userFunctionDef;
    private final boolean supported;
    private final String unsupportedReason;
    
    private IAppFunction(Builder builder) {
        this.name = builder.name;
        this.minParameters = builder.minParameters;
        this.maxParameters = builder.maxParameters;
        this.paramTypes = builder.paramTypes != null ? builder.paramTypes : Collections.emptyList();
        this.paramTypeLists = builder.paramTypeLists != null ? builder.paramTypeLists : Collections.emptyList();
        this.handler = builder.handler;
        this.wrappedFunction = builder.wrappedFunction;
        this.isUserFunction = builder.isUserFunction;
        this.userFunctionDef = builder.userFunctionDef;
        this.supported = builder.supported;
        this.unsupportedReason = builder.unsupportedReason;
    }
    
    public String getName() {
        return name;
    }
    
    public int getMinParameters() {
        return minParameters;
    }
    
    public int getMaxParameters() {
        return maxParameters;
    }
    
    public List<ParamType> getParamTypes() {
        return paramTypes;
    }
    
    public List<List<ParamType>> getParamTypeLists() {
        return paramTypeLists;
    }
    
    public String getParamTypeInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("(");
        if (!paramTypes.isEmpty()) {
            for (int i = 0; i < paramTypes.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(paramTypes.get(i).name());
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
    public Object call(Object... args) {
        return call(null, args);
    }
    
    public Object call(IAppScript script, Object... args) {
        if (!supported) {
            throw new IAppScriptException("函数 '" + name + "' 不支持: " + unsupportedReason);
        }
        
        if (args == null) args = new Object[0];
        
        if (args.length < minParameters) {
            throw new IAppScriptException("函数 '" + name + "' 至少需要 " + minParameters + " 个参数, 但提供了 " + args.length + " 个");
        }
        
        if (maxParameters >= 0 && args.length > maxParameters) {
            throw new IAppScriptException("函数 '" + name + "' 最多接受 " + maxParameters + " 个参数, 但提供了 " + args.length + " 个");
        }
        
        try {
            if (handler != null) {
                return handler.call(script, args);
            }
            
            if (wrappedFunction != null) {
                RuntimeContext context = script != null ? script.getContext() : new RuntimeContext();
                List<Object> argList = Arrays.asList(args);
                return wrappedFunction.call(context, argList);
            }
            
            if (isUserFunction && userFunctionDef != null && script != null) {
                return executeUserFunction(script, args);
            }
            
            throw new IAppScriptException("函数 '" + name + "' 没有实现");
        } catch (IAppScriptException e) {
            throw e;
        } catch (FunctionException e) {
            throw new IAppScriptException("函数 '" + name + "' 执行错误: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new IAppScriptException("函数 '" + name + "' 执行错误: " + e.getMessage(), e);
        }
    }
    
    private Object executeUserFunction(IAppScript script, Object[] args) throws InterpreterException {
        RuntimeContext context = script.getContext();
        context.getVariableManager().pushScope();
        
        try {
            List<String> parameters = userFunctionDef.getParameters();
            for (int i = 0; i < parameters.size(); i++) {
                String paramName = parameters.get(i);
                Object argValue = i < args.length ? args[i] : null;
                context.setVariable(paramName, argValue, cn.langlang.iapp.lexer.TokenType.KEYWORD_S);
            }
            
            Interpreter interpreter = new Interpreter(context);
            Object result = null;
            
            for (cn.langlang.iapp.ast.Statement stmt : userFunctionDef.getBody()) {
                if (context.isEndCodeRequested()) break;
                result = interpreter.executeStatement(stmt, context);
            }
            
            return result;
        } finally {
            context.getVariableManager().popScope();
        }
    }
    
    public boolean isSupported() {
        return supported;
    }
    
    public String getUnsupportedReason() {
        return unsupportedReason;
    }
    
    public boolean isUserFunction() {
        return isUserFunction;
    }
    
    public static IAppFunction create(String name, IAppFunctionHandler handler) {
        return create(name, 0, Integer.MAX_VALUE, handler);
    }
    
    public static IAppFunction create(String name, int minParams, int maxParams, IAppFunctionHandler handler) {
        return new Builder()
                .name(name)
                .minParameters(minParams)
                .maxParameters(maxParams)
                .handler(handler)
                .supported(true)
                .build();
    }
    
    public static IAppFunction wrap(IFunction function) {
        if (function == null) return null;
        
        return new Builder()
                .name(function.getName())
                .minParameters(function.getMinParameters())
                .maxParameters(function.getMaxParameters())
                .paramTypes(function.getParamTypes())
                .paramTypeLists(function.getParamTypeLists())
                .wrappedFunction(function)
                .supported(function.isSupported())
                .unsupportedReason(function.getUnsupportedReason())
                .build();
    }
    
    public static IAppFunction fromUserFunction(FunctionDefinitionStatement funcDef) {
        if (funcDef == null) return null;
        
        return new Builder()
                .name(funcDef.getFullName())
                .minParameters(0)
                .maxParameters(funcDef.getParameters().size())
                .userFunction(true)
                .userFunctionDef(funcDef)
                .supported(true)
                .build();
    }
    
    public static class Builder {
        private String name;
        private int minParameters = 0;
        private int maxParameters = Integer.MAX_VALUE;
        private List<ParamType> paramTypes;
        private List<List<ParamType>> paramTypeLists;
        private IAppFunctionHandler handler;
        private IFunction wrappedFunction;
        private boolean isUserFunction = false;
        private FunctionDefinitionStatement userFunctionDef;
        private boolean supported = true;
        private String unsupportedReason;
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder minParameters(int minParameters) {
            this.minParameters = minParameters;
            return this;
        }
        
        public Builder maxParameters(int maxParameters) {
            this.maxParameters = maxParameters;
            return this;
        }
        
        public Builder paramTypes(List<ParamType> paramTypes) {
            this.paramTypes = paramTypes;
            return this;
        }
        
        public Builder paramTypeLists(List<List<ParamType>> paramTypeLists) {
            this.paramTypeLists = paramTypeLists;
            return this;
        }
        
        public Builder handler(IAppFunctionHandler handler) {
            this.handler = handler;
            return this;
        }
        
        public Builder wrappedFunction(IFunction wrappedFunction) {
            this.wrappedFunction = wrappedFunction;
            return this;
        }
        
        public Builder userFunction(boolean isUserFunction) {
            this.isUserFunction = isUserFunction;
            return this;
        }
        
        public Builder userFunctionDef(FunctionDefinitionStatement userFunctionDef) {
            this.userFunctionDef = userFunctionDef;
            return this;
        }
        
        public Builder supported(boolean supported) {
            this.supported = supported;
            return this;
        }
        
        public Builder unsupportedReason(String unsupportedReason) {
            this.unsupportedReason = unsupportedReason;
            return this;
        }
        
        public IAppFunction build() {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("函数名不能为空");
            }
            return new IAppFunction(this);
        }
    }
    
    @Override
    public String toString() {
        return "IAppFunction{" +
                "name='" + name + '\'' +
                ", minParameters=" + minParameters +
                ", maxParameters=" + maxParameters +
                ", supported=" + supported +
                '}';
    }
}
