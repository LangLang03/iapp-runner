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
        registerBuiltinFunctions();
        initializeBeanShell();
    }
    
    private void registerBuiltinFunctions() {
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.output.SysoFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.output.TwFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.string.SsFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.string.SrFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.string.SjFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.string.SlFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.string.SsgFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.string.SlgFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.string.StrimFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.string.SlowerFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.string.SupperFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.string.SiofFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.string.SlofFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.string.StobmFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.string.Sutf8toFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.math.SAddFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.math.SSubFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.math.SMulFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.math.SDivFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.math.SModFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.math.SFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.math.S2Function());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.math.SnFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.math.SranFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.array.NszFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.array.SgszFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.array.SsszFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.array.SgszlFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.file.FdFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.file.FeFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.file.FsFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.file.FrFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.file.FwFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.file.FcFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.file.FlFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.file.FtFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.file.FdirFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.file.FuzFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.file.FuzsFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.file.FjFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.file.FoFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.file.FiFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.net.HsFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.net.HdFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.net.HdflFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.net.HufFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.net.HwFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.net.HwsFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.time.TimeFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.other.StopFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.java.JavaFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.java.JavaxFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.java.JavanewFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.java.JavagsFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.java.JavassFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.java.ClsFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.other.CallFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.list.AslistFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.list.SslistFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.list.GslistFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.list.GslistlFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.list.DslistFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.list.GslistszFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.list.GslistisFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.list.GslistiofFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.list.GslistlofFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.clipboard.SxbFunction());
        functionRegistry.registerFunction(new cn.langlang.iapp.functions.clipboard.ShbFunction());
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
        variableManager.setVariable(name, value);
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
