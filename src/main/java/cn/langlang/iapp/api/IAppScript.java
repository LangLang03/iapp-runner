package cn.langlang.iapp.api;

import cn.langlang.iapp.ast.FunctionDefinitionStatement;
import cn.langlang.iapp.ast.Program;
import cn.langlang.iapp.interpreter.Interpreter;
import cn.langlang.iapp.interpreter.InterpreterException;
import cn.langlang.iapp.lexer.Lexer;
import cn.langlang.iapp.lexer.LexerException;
import cn.langlang.iapp.lexer.Token;
import cn.langlang.iapp.lexer.TokenType;
import cn.langlang.iapp.parser.Parser;
import cn.langlang.iapp.parser.ParserException;
import cn.langlang.iapp.runtime.FunctionRegistry;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IAppScript {
    
    private final RuntimeContext context;
    private final Interpreter interpreter;
    private final Lexer lexer;
    private Program loadedProgram;
    private String loadedSource;
    
    private IAppScript(RuntimeContext context) {
        this.context = context != null ? context : new RuntimeContext();
        this.interpreter = new Interpreter(this.context);
        this.lexer = new Lexer("");
    }
    
    public static IAppScript create() {
        return new IAppScript(null);
    }
    
    public static IAppScript create(RuntimeContext context) {
        return new IAppScript(context);
    }
    
    public IAppScript loadString(String source) {
        if (source == null) {
            throw new IAppScriptException("源码不能为空");
        }
        this.loadedSource = source;
        this.loadedProgram = null;
        return this;
    }
    
    public IAppScript loadFile(String path) {
        return loadFile(new File(path));
    }
    
    public IAppScript loadFile(File file) {
        if (file == null || !file.exists()) {
            throw new IAppScriptException("文件未找到: " + (file != null ? file.getPath() : "null"));
        }
        
        String content = readFileContent(file);
        if (content == null) {
            throw new IAppScriptException("读取文件失败: " + file.getPath());
        }
        
        this.loadedSource = content;
        this.loadedProgram = null;
        
        if (context.getCurrentDirectory() == null || context.getCurrentDirectory().isEmpty()) {
            context.setCurrentDirectory(file.getParent());
        }
        
        return this;
    }
    
    public IAppScript loadMjava(String directory) {
        if (directory == null) {
            throw new IAppScriptException("目录不能为空");
        }
        
        File dir = new File(directory);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IAppScriptException("目录未找到: " + directory);
        }
        
        context.loadMjavaModules(dir.getAbsolutePath());
        return this;
    }
    
    public IAppScript loadMjavaFile(String path) {
        if (path == null) {
            throw new IAppScriptException("路径不能为空");
        }
        
        File file = new File(path);
        if (!file.exists()) {
            throw new IAppScriptException("文件未找到: " + path);
        }
        
        context.getMjavaModuleLoader().loadModule(file, context.getBeanShellInterpreter());
        return this;
    }
    
    public Object eval() {
        if (loadedSource == null) {
            throw new IAppScriptException("未加载脚本, 请先使用 loadString() 或 loadFile()");
        }
        
        return executeSource(loadedSource);
    }
    
    public Object eval(String source) {
        if (source == null) {
            throw new IAppScriptException("源码不能为空");
        }
        return executeSource(source);
    }
    
    public Object evalFile(String path) {
        return loadFile(path).eval();
    }
    
    private Object executeSource(String source) {
        try {
            Lexer localLexer = new Lexer(source);
            List<Token> tokens = localLexer.tokenizeInternal();
            
            Parser parser = new Parser(tokens);
            parser.setFunctionRegistry(context.getFunctionRegistry());
            Program program = parser.parse();
            
            context.resetEndCodeRequest();
            
            return interpreter.execute(program, context);
            
        } catch (LexerException e) {
            throw new IAppScriptException("词法分析错误: " + e.getMessage(), e);
        } catch (ParserException e) {
            throw new IAppScriptException("语法分析错误: " + e.getMessage(), e);
        } catch (InterpreterException e) {
            throw new IAppScriptException("运行时错误: " + e.getMessage(), e);
        }
    }
    
    public IAppFunction getFunction(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        
        IFunction function = context.getFunctionRegistry().getFunction(name);
        if (function != null) {
            return IAppFunction.wrap(function);
        }
        
        return null;
    }
    
    public boolean hasFunction(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return context.getFunctionRegistry().hasFunction(name);
    }
    
    public IAppScript registerFunction(IAppFunction function) {
        if (function == null) {
            throw new IAppScriptException("函数不能为空");
        }
        
        IFunction wrappedFunction = new IAppFunctionAdapter(function, context);
        context.getFunctionRegistry().registerFunction(wrappedFunction);
        return this;
    }
    
    public IAppScript registerFunction(String name, IAppFunctionHandler handler) {
        if (name == null || name.isEmpty()) {
            throw new IAppScriptException("函数名不能为空");
        }
        if (handler == null) {
            throw new IAppScriptException("处理器不能为空");
        }
        
        IAppFunction function = IAppFunction.create(name, handler);
        return registerFunction(function);
    }
    
    public Set<String> getFunctionNames() {
        return new HashSet<>(context.getFunctionRegistry().getFunctionNames());
    }
    
    public IAppVariable getVariable(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        
        Object value = context.getVariable(name);
        IAppVariable.VariableScope scope = determineVariableScope(name);
        
        return new IAppVariable(name, value, scope);
    }
    
    public IAppScript setVariable(String name, Object value) {
        return setVariable(name, value, IAppVariable.VariableScope.LOCAL);
    }
    
    public IAppScript setVariable(String name, Object value, IAppVariable.VariableScope scope) {
        if (name == null || name.isEmpty()) {
            throw new IAppScriptException("变量名不能为空");
        }
        
        TokenType tokenScope;
        switch (scope) {
            case INTERFACE:
                tokenScope = TokenType.KEYWORD_SS;
                break;
            case GLOBAL:
                tokenScope = TokenType.KEYWORD_SSS;
                break;
            default:
                tokenScope = TokenType.KEYWORD_S;
        }
        
        context.setVariable(name, value, tokenScope);
        return this;
    }
    
    public boolean hasVariable(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return context.hasVariable(name);
    }
    
    public IAppScript removeVariable(String name) {
        if (name == null || name.isEmpty()) {
            return this;
        }
        
        context.getVariableManager().setVariable(name, null);
        return this;
    }
    
    public Set<String> getVariableNames() {
        Set<String> names = new HashSet<>();
        
        for (String name : context.getVariableManager().getLocalVariables().keySet()) {
            names.add(name);
        }
        
        for (String name : context.getVariableManager().getInterfaceVariables().keySet()) {
            names.add("ss." + name);
        }
        
        for (String name : context.getVariableManager().getGlobalVariables().keySet()) {
            names.add("sss." + name);
        }
        
        return names;
    }
    
    public IAppFunction getUserFunction(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        
        FunctionDefinitionStatement funcDef = context.getUserFunction(name);
        if (funcDef != null) {
            return IAppFunction.fromUserFunction(funcDef);
        }
        
        return null;
    }
    
    public boolean hasUserFunction(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return context.hasUserFunction(name);
    }
    
    public Set<String> getUserFunctionNames() {
        return new HashSet<>(context.getUserFunctions().keySet());
    }
    
    public RuntimeContext getContext() {
        return context;
    }
    
    public IAppScript setCurrentDirectory(String dir) {
        if (dir != null) {
            context.setCurrentDirectory(dir);
        }
        return this;
    }
    
    public String getCurrentDirectory() {
        return context.getCurrentDirectory();
    }
    
    public IAppValue valueOf(Object value) {
        return IAppValue.valueOf(value);
    }
    
    public IAppValue nil() {
        return IAppValue.nil();
    }
    
    public IAppScript reset() {
        context.resetEndCodeRequest();
        context.getVariableManager().clearLocalVariables();
        loadedProgram = null;
        loadedSource = null;
        return this;
    }
    
    private String readFileContent(File file) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            return null;
        }
    }
    
    private IAppVariable.VariableScope determineVariableScope(String name) {
        if (name.startsWith("sss.")) {
            return IAppVariable.VariableScope.GLOBAL;
        } else if (name.startsWith("ss.")) {
            return IAppVariable.VariableScope.INTERFACE;
        }
        return IAppVariable.VariableScope.LOCAL;
    }
    
    private static class IAppFunctionAdapter implements IFunction {
        private final IAppFunction function;
        private final RuntimeContext context;
        
        IAppFunctionAdapter(IAppFunction function, RuntimeContext context) {
            this.function = function;
            this.context = context;
        }
        
        @Override
        public String getName() {
            return function.getName();
        }
        
        @Override
        public int getMinParameters() {
            return function.getMinParameters();
        }
        
        @Override
        public int getMaxParameters() {
            return function.getMaxParameters();
        }
        
        @Override
        public Object call(RuntimeContext context, List<Object> arguments) {
            Object[] args = arguments != null ? arguments.toArray() : new Object[0];
            return function.call(args);
        }
        
        @Override
        public boolean isSupported() {
            return function.isSupported();
        }
        
        @Override
        public String getUnsupportedReason() {
            return function.getUnsupportedReason();
        }
        
        @Override
        public List<ParamType> getParamTypes() {
            return function.getParamTypes();
        }
        
        @Override
        public List<List<ParamType>> getParamTypeLists() {
            return function.getParamTypeLists();
        }
    }
}
