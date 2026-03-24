package cn.langlang.iapp.lsp.core.provider;

import cn.langlang.iapp.lexer.Lexer;
import cn.langlang.iapp.lexer.LexerException;
import cn.langlang.iapp.lexer.Token;
import cn.langlang.iapp.lsp.core.LSContext;
import cn.langlang.iapp.lsp.core.model.DiagnosticInfo;
import cn.langlang.iapp.parser.Parser;
import cn.langlang.iapp.parser.ParserException;
import cn.langlang.iapp.ast.Program;
import cn.langlang.iapp.ast.Statement;
import cn.langlang.iapp.ast.FunctionCallStatement;
import cn.langlang.iapp.runtime.IFunction;

import java.util.*;

public class DiagnosticProvider {
    private final LSContext context;
    private final VariableProvider variableProvider;

    public DiagnosticProvider(LSContext context) {
        this.context = context;
        this.variableProvider = new VariableProvider(context);
    }

    public DiagnosticProvider(LSContext context, VariableProvider variableProvider) {
        this.context = context;
        this.variableProvider = variableProvider != null ? variableProvider : new VariableProvider(context);
    }

    public List<DiagnosticInfo> getDiagnostics(String text) {
        List<DiagnosticInfo> diagnostics = new ArrayList<>();
        
        if (text == null || text.isEmpty()) {
            return diagnostics;
        }
        
        diagnostics.addAll(checkLexerErrors(text));
        
        if (diagnostics.isEmpty()) {
            diagnostics.addAll(checkParserErrors(text));
        }
        
        if (diagnostics.isEmpty()) {
            diagnostics.addAll(checkSemanticErrors(text));
        }
        
        return diagnostics;
    }

    private List<DiagnosticInfo> checkLexerErrors(String text) {
        List<DiagnosticInfo> diagnostics = new ArrayList<>();
        
        try {
            Lexer lexer = new Lexer(text);
            lexer.tokenizeInternal();
        } catch (LexerException e) {
            diagnostics.add(new DiagnosticInfo(
                e.getMessage(),
                DiagnosticInfo.Severity.ERROR,
                e.getLine(),
                e.getColumn()
            ));
        }
        
        return diagnostics;
    }

    private List<DiagnosticInfo> checkParserErrors(String text) {
        List<DiagnosticInfo> diagnostics = new ArrayList<>();
        
        try {
            Lexer lexer = new Lexer(text);
            List<Token> tokens = lexer.tokenizeInternal();
            Parser parser = new Parser(tokens);
            parser.setFunctionRegistry(context.getFunctionRegistry());
            parser.parse();
        } catch (ParserException e) {
            diagnostics.add(new DiagnosticInfo(
                e.getMessage(),
                DiagnosticInfo.Severity.ERROR,
                e.getLine(),
                e.getColumn()
            ));
        } catch (LexerException e) {
            diagnostics.add(new DiagnosticInfo(
                e.getMessage(),
                DiagnosticInfo.Severity.ERROR,
                e.getLine(),
                e.getColumn()
            ));
        }
        
        return diagnostics;
    }

    private List<DiagnosticInfo> checkSemanticErrors(String text) {
        List<DiagnosticInfo> diagnostics = new ArrayList<>();
        
        try {
            Lexer lexer = new Lexer(text);
            List<Token> tokens = lexer.tokenizeInternal();
            Parser parser = new Parser(tokens);
            parser.setFunctionRegistry(context.getFunctionRegistry());
            Program program = parser.parse();
            
            Set<String> definedVariables = new HashSet<>();
            Set<String> interfaceVariables = new HashSet<>();
            Set<String> globalVariables = new HashSet<>();
            
            for (Statement stmt : program.getStatements()) {
                checkStatement(stmt, diagnostics, definedVariables, interfaceVariables, globalVariables);
            }
        } catch (Exception e) {
            // Parser errors already handled
        }
        
        return diagnostics;
    }

    private void checkStatement(Statement stmt, List<DiagnosticInfo> diagnostics,
                                Set<String> definedVariables, Set<String> interfaceVariables,
                                Set<String> globalVariables) {
        if (stmt instanceof FunctionCallStatement) {
            checkFunctionCall((FunctionCallStatement) stmt, diagnostics);
        }
    }

    private void checkFunctionCall(FunctionCallStatement stmt, List<DiagnosticInfo> diagnostics) {
        String funcName = stmt.getFunctionName();
        if (funcName == null || funcName.isEmpty()) {
            return;
        }
        
        IFunction function = context.getFunction(funcName);
        
        if (function == null) {
            diagnostics.add(new DiagnosticInfo(
                "未定义的函数: " + funcName,
                DiagnosticInfo.Severity.WARNING,
                stmt.getLine(),
                1
            ));
            return;
        }
        
        int argCount = stmt.getArguments() != null ? stmt.getArguments().size() : 0;
        int minParams = function.getMinParameters();
        int maxParams = function.getMaxParameters();
        
        if (argCount < minParams) {
            diagnostics.add(new DiagnosticInfo(
                "函数 " + funcName + " 至少需要 " + minParams + " 个参数，但提供了 " + argCount + " 个",
                DiagnosticInfo.Severity.ERROR,
                stmt.getLine(),
                1
            ));
        } else if (maxParams != Integer.MAX_VALUE && argCount > maxParams) {
            diagnostics.add(new DiagnosticInfo(
                "函数 " + funcName + " 最多接受 " + maxParams + " 个参数，但提供了 " + argCount + " 个",
                DiagnosticInfo.Severity.ERROR,
                stmt.getLine(),
                1
            ));
        }
    }

    public List<DiagnosticInfo> checkUndefinedVariable(String varName, int line, int column,
                                                       Set<String> definedVariables) {
        List<DiagnosticInfo> diagnostics = new ArrayList<>();
        
        if (varName == null || varName.isEmpty()) {
            return diagnostics;
        }
        
        if (!definedVariables.contains(varName) && !context.hasFunction(varName)) {
            diagnostics.add(new DiagnosticInfo(
                "未定义的变量: " + varName,
                DiagnosticInfo.Severity.WARNING,
                line,
                column
            ));
        }
        
        return diagnostics;
    }

    public VariableProvider getVariableProvider() {
        return variableProvider;
    }
}
