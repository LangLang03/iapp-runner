package cn.langlang.iapp.runtime;

import bsh.Interpreter;
import cn.langlang.iapp.ast.FunctionDefinitionStatement;
import cn.langlang.iapp.lexer.TokenType;
import cn.langlang.iapp.module.MjavaModuleLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class RuntimeContext {
    private final VariableManager variableManager;
    private final FunctionRegistry functionRegistry;
    private final Interpreter beanShellInterpreter;
    private final MjavaModuleLoader mjavaModuleLoader;
    private final Stack<BreakContext> breakContextStack;
    private final Map<String, Object> javaObjects;
    private final Map<String, FunctionDefinitionStatement> userFunctions;
    private String currentDirectory;
    private boolean endCodeRequested;
    private Thread currentThread;
    
    public RuntimeContext() {
        this.variableManager = new VariableManager();
        this.functionRegistry = new FunctionRegistry();
        this.beanShellInterpreter = new Interpreter();
        this.mjavaModuleLoader = new MjavaModuleLoader();
        this.breakContextStack = new Stack<>();
        this.javaObjects = new HashMap<>();
        this.userFunctions = new HashMap<>();
        this.currentDirectory = System.getProperty("user.dir");
        this.endCodeRequested = false;
        initializeBeanShell();
    }
    
    private void initializeBeanShell() {
        try {
            beanShellInterpreter.set("context", this);
            beanShellInterpreter.set("variableManager", variableManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public VariableManager getVariableManager() {
        return variableManager;
    }
    
    public FunctionRegistry getFunctionRegistry() {
        return functionRegistry;
    }
    
    public Interpreter getBeanShellInterpreter() {
        return beanShellInterpreter;
    }
    
    public MjavaModuleLoader getMjavaModuleLoader() {
        return mjavaModuleLoader;
    }
    
    public void setVariable(String name, Object value) {
        variableManager.setLocalVariable(name, value);
    }
    
    public void setVariable(String name, Object value, TokenType scope) {
        variableManager.setVariable(name, value, scope);
    }
    
    public Object getVariable(String name) {
        return variableManager.getVariable(name);
    }
    
    public boolean hasVariable(String name) {
        return variableManager.hasVariable(name);
    }
    
    public void pushBreakContext(BreakContext context) {
        breakContextStack.push(context);
    }
    
    public BreakContext popBreakContext() {
        if (!breakContextStack.isEmpty()) {
            return breakContextStack.pop();
        }
        return null;
    }
    
    public BreakContext getCurrentBreakContext() {
        if (!breakContextStack.isEmpty()) {
            return breakContextStack.peek();
        }
        return null;
    }
    
    public void registerJavaObject(String name, Object object) {
        javaObjects.put(name, object);
    }
    
    public Object getJavaObject(String name) {
        return javaObjects.get(name);
    }
    
    public void removeJavaObject(String name) {
        javaObjects.remove(name);
    }
    
    public String getCurrentDirectory() {
        return currentDirectory;
    }
    
    public void setCurrentDirectory(String directory) {
        this.currentDirectory = directory;
    }
    
    public String resolvePath(String path) {
        if (path.startsWith("%")) {
            return new File(currentDirectory, path.substring(1)).getAbsolutePath();
        } else if (path.startsWith("@")) {
            return new File(currentDirectory, "assets/" + path.substring(1)).getAbsolutePath();
        }
        return path;
    }
    
    public void requestEndCode() {
        this.endCodeRequested = true;
    }
    
    public boolean isEndCodeRequested() {
        return endCodeRequested;
    }
    
    public void resetEndCodeRequest() {
        this.endCodeRequested = false;
    }
    
    public Thread getCurrentThread() {
        return currentThread;
    }
    
    public void setCurrentThread(Thread thread) {
        this.currentThread = thread;
    }
    
    public void loadMjavaModules(String directory) {
        mjavaModuleLoader.loadModules(directory, beanShellInterpreter);
    }
    
    public Object executeMjavaMethod(String moduleName, String methodName, Object[] args) throws Exception {
        return mjavaModuleLoader.executeMethod(moduleName, methodName, args, beanShellInterpreter);
    }
    
    public void registerUserFunction(String name, FunctionDefinitionStatement func) {
        userFunctions.put(name, func);
    }
    
    public FunctionDefinitionStatement getUserFunction(String name) {
        return userFunctions.get(name);
    }
    
    public boolean hasUserFunction(String name) {
        return userFunctions.containsKey(name);
    }
    
    public void declareVariable(String name) {
        variableManager.declareVariable(name);
    }
    
    public boolean isVariableDeclared(String name) {
        return variableManager.isVariableDeclared(name);
    }
    
    public static class BreakContext {
        private final String type;
        private boolean shouldBreak;
        
        public BreakContext(String type) {
            this.type = type;
            this.shouldBreak = false;
        }
        
        public String getType() {
            return type;
        }
        
        public boolean shouldBreak() {
            return shouldBreak;
        }
        
        public void setShouldBreak(boolean shouldBreak) {
            this.shouldBreak = shouldBreak;
        }
    }
}
