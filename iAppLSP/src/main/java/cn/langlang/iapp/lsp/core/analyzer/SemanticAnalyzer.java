package cn.langlang.iapp.lsp.core.analyzer;

import cn.langlang.iapp.ast.*;
import cn.langlang.iapp.interpreter.InterpreterException;
import cn.langlang.iapp.lexer.Lexer;
import cn.langlang.iapp.lexer.Token;
import cn.langlang.iapp.lexer.TokenType;
import cn.langlang.iapp.lsp.core.LSContext;
import cn.langlang.iapp.lsp.core.model.DiagnosticInfo;
import cn.langlang.iapp.lsp.core.model.SymbolInfo;
import cn.langlang.iapp.lsp.core.model.VariableInfo;
import cn.langlang.iapp.lsp.core.provider.VariableProvider;
import cn.langlang.iapp.parser.Parser;

import java.util.*;

public class SemanticAnalyzer {
    private final LSContext context;
    private final VariableProvider variableProvider;
    private final List<DiagnosticInfo> diagnostics;
    private final Map<String, SymbolInfo> symbolTable;
    private final Stack<Set<String>> scopeStack;
    private final Map<String, FunctionDefinitionStatement> userFunctions;

    public SemanticAnalyzer(LSContext context) {
        this.context = context;
        this.variableProvider = new VariableProvider(context);
        this.diagnostics = new ArrayList<>();
        this.symbolTable = new LinkedHashMap<>();
        this.scopeStack = new Stack<>();
        this.userFunctions = new HashMap<>();
    }

    public AnalysisResult analyze(String text) {
        diagnostics.clear();
        symbolTable.clear();
        scopeStack.clear();
        userFunctions.clear();
        variableProvider.clearAll();
        
        try {
            Lexer lexer = new Lexer(text);
            List<Token> tokens = lexer.tokenizeInternal();
            
            Parser parser = new Parser(tokens);
            parser.setFunctionRegistry(context.getFunctionRegistry());
            Program program = parser.parse();
            
            // 第一遍：收集所有函数定义
            for (Statement stmt : program.getStatements()) {
                if (stmt instanceof FunctionDefinitionStatement) {
                    FunctionDefinitionStatement funcStmt = (FunctionDefinitionStatement) stmt;
                    userFunctions.put(funcStmt.getFunctionName(), funcStmt);
                    
                    SymbolInfo symbol = new SymbolInfo(funcStmt.getFunctionName(), SymbolInfo.SymbolType.USER_FUNCTION, funcStmt.getLine(), 0);
                    symbol.setEndLine(funcStmt.getLine());
                    symbol.setEndColumn(funcStmt.getFunctionName().length() + 3);
                    symbol.setDetail("用户定义函数");
                    symbolTable.put(funcStmt.getFunctionName(), symbol);
                }
            }
            
            pushScope();
            
            // 第二遍：分析所有语句
            for (Statement stmt : program.getStatements()) {
                analyzeStatement(stmt);
            }
            
            popScope();
        } catch (Exception e) {
            // Lexer/Parser errors handled elsewhere
        }
        
        return new AnalysisResult(diagnostics, symbolTable, variableProvider);
    }

    private void analyzeStatement(Statement stmt) {
        try {
            stmt.accept(new Statement.StatementVisitor<Void>() {
                @Override
                public Void visitVariableDeclaration(VariableDeclarationStatement stmt) {
                    analyzeVariableDeclaration(stmt);
                    return null;
                }

                @Override
                public Void visitAssignment(AssignmentStatement stmt) {
                    analyzeAssignment(stmt);
                    return null;
                }

                @Override
                public Void visitIf(IfStatement stmt) {
                    analyzeIf(stmt);
                    return null;
                }

                @Override
                public Void visitWhile(WhileStatement stmt) {
                    analyzeWhile(stmt);
                    return null;
                }

                @Override
                public Void visitFor(ForStatement stmt) {
                    analyzeFor(stmt);
                    return null;
                }

                @Override
                public Void visitFunctionCall(FunctionCallStatement stmt) {
                    analyzeFunctionCall(stmt);
                    return null;
                }

                @Override
                public Void visitBreak(BreakStatement stmt) {
                    return null;
                }

                @Override
                public Void visitEndCode(EndCodeStatement stmt) {
                    return null;
                }

                @Override
                public Void visitFunctionDefinition(FunctionDefinitionStatement stmt) {
                    analyzeFunctionDefinition(stmt);
                    return null;
                }

                @Override
                public Void visitThread(ThreadStatement stmt) {
                    analyzeThread(stmt);
                    return null;
                }

                @Override
                public Void visitBlock(BlockStatement stmt) {
                    analyzeBlock(stmt);
                    return null;
                }
            });
        } catch (InterpreterException e) {
            // Ignore
        }
    }

    private void analyzeVariableDeclaration(VariableDeclarationStatement stmt) {
        String varName = stmt.getVariableName();
        TokenType scope = stmt.getScope();
        int line = stmt.getLine();
        
        addVariable(varName, scope, line, 0);
        
        if (stmt.getInitialValue() != null) {
            analyzeExpression(stmt.getInitialValue());
        }
    }

    private void analyzeAssignment(AssignmentStatement stmt) {
        String varName = stmt.getVariableName();
        TokenType scope = stmt.getScope();
        int line = stmt.getLine();
        
        if (!isVariableDefined(varName, scope)) {
            diagnostics.add(new DiagnosticInfo(
                "未定义的变量: " + varName,
                DiagnosticInfo.Severity.WARNING,
                line,
                0
            ));
        }
        
        if (stmt.getValue() != null) {
            analyzeExpression(stmt.getValue());
        }
    }

    private void analyzeIf(IfStatement stmt) {
        analyzeExpression(stmt.getCondition());
        
        pushScope();
        analyzeStatementList(stmt.getThenStatements());
        popScope();
        
        for (IfStatement.ElseIfClause elseIf : stmt.getElseIfClauses()) {
            pushScope();
            analyzeExpression(elseIf.condition());
            analyzeStatementList(elseIf.statements());
            popScope();
        }
        
        if (stmt.getElseStatements() != null && !stmt.getElseStatements().isEmpty()) {
            pushScope();
            analyzeStatementList(stmt.getElseStatements());
            popScope();
        }
    }

    private void analyzeWhile(WhileStatement stmt) {
        analyzeExpression(stmt.getCondition());
        
        pushScope();
        analyzeStatementList(stmt.getBody());
        popScope();
    }

    private void analyzeFor(ForStatement stmt) {
        pushScope();
        
        if (stmt.getInitStatement() != null) {
            analyzeStatement(stmt.getInitStatement());
        }
        
        if (stmt.getCondition() != null) {
            analyzeExpression(stmt.getCondition());
        }
        
        if (stmt.getUpdateStatement() != null) {
            analyzeStatement(stmt.getUpdateStatement());
        }
        
        analyzeStatementList(stmt.getBody());
        
        popScope();
    }

    private void analyzeFunctionCall(FunctionCallStatement stmt) {
        String funcName = stmt.getFunctionName();
        int line = stmt.getLine();
        
        if (!context.hasFunction(funcName) && !userFunctions.containsKey(funcName)) {
            diagnostics.add(new DiagnosticInfo(
                "未定义的函数: " + funcName,
                DiagnosticInfo.Severity.WARNING,
                line,
                0
            ));
        }
        
        for (Expression arg : stmt.getArguments()) {
            analyzeExpression(arg);
        }
        
        if (stmt.hasOutputVariables()) {
            for (String outVar : stmt.getOutputVariables()) {
                addVariable(outVar, stmt.getResultScope(), line, 0);
            }
        }
    }

    private void analyzeFunctionDefinition(FunctionDefinitionStatement stmt) {
        String funcName = stmt.getFunctionName();
        int line = stmt.getLine();
        
        // 函数定义已在第一遍扫描中收集，这里只分析函数体
        
        pushScope();
        
        for (String param : stmt.getParameters()) {
            addVariable(param, TokenType.KEYWORD_S, line, 0);
            variableProvider.addFunctionParameter(param, line, 0);
        }
        
        analyzeStatementList(stmt.getBody());
        
        popScope();
    }

    private void analyzeThread(ThreadStatement stmt) {
        pushScope();
        analyzeStatementList(stmt.getBody());
        popScope();
    }

    private void analyzeBlock(BlockStatement block) {
        if (block == null) return;
        
        for (Statement stmt : block.getStatements()) {
            analyzeStatement(stmt);
        }
    }

    private void analyzeStatementList(List<Statement> statements) {
        if (statements == null) return;
        
        for (Statement stmt : statements) {
            analyzeStatement(stmt);
        }
    }

    private void analyzeExpression(Expression expr) {
        if (expr == null) return;
        
        try {
            expr.accept(new Expression.ExpressionVisitor<Void>() {
                @Override
                public Void visitNumberLiteral(NumberLiteralExpression expr) {
                    return null;
                }

                @Override
                public Void visitStringLiteral(StringLiteralExpression expr) {
                    return null;
                }

                @Override
                public Void visitBooleanLiteral(BooleanLiteralExpression expr) {
                    return null;
                }

                @Override
                public Void visitNullLiteral(NullLiteralExpression expr) {
                    return null;
                }

                @Override
                public Void visitVariable(VariableExpression expr) {
                    analyzeVariableExpression(expr);
                    return null;
                }

                @Override
                public Void visitBinary(BinaryExpression expr) {
                    analyzeExpression(expr.getLeft());
                    analyzeExpression(expr.getRight());
                    return null;
                }

                @Override
                public Void visitUnary(UnaryExpression expr) {
                    analyzeExpression(expr.getOperand());
                    return null;
                }

                @Override
                public Void visitFunctionCall(FunctionCallExpression expr) {
                    analyzeFunctionCallExpression(expr);
                    return null;
                }

                @Override
                public Void visitArrayAccess(ArrayAccessExpression expr) {
                    analyzeExpression(expr.getArray());
                    analyzeExpression(expr.getIndex());
                    return null;
                }

                @Override
                public Void visitMemberAccess(MemberAccessExpression expr) {
                    analyzeExpression(expr.getObject());
                    return null;
                }
            });
        } catch (InterpreterException e) {
            // Ignore
        }
    }

    private void analyzeVariableExpression(VariableExpression expr) {
        String varName = expr.getName();
        int line = expr.getLine();
        
        if (!isVariableDefined(varName, TokenType.KEYWORD_S) && 
            !context.hasFunction(varName)) {
            diagnostics.add(new DiagnosticInfo(
                "未定义的变量: " + varName,
                DiagnosticInfo.Severity.HINT,
                line,
                0
            ));
        }
    }

    private void analyzeFunctionCallExpression(FunctionCallExpression expr) {
        String funcName = expr.getFunctionName();
        
        if (!context.hasFunction(funcName) && !userFunctions.containsKey(funcName)) {
            diagnostics.add(new DiagnosticInfo(
                "未定义的函数: " + funcName,
                DiagnosticInfo.Severity.WARNING,
                expr.getLine(),
                0
            ));
        }
        
        for (Expression arg : expr.getArguments()) {
            analyzeExpression(arg);
        }
    }

    private void addVariable(String name, TokenType scope, int line, int column) {
        VariableInfo info = new VariableInfo(name, scope, line, column);
        
        switch (scope) {
            case KEYWORD_SS:
                variableProvider.addInterfaceVariable(name, line, column);
                break;
            case KEYWORD_SSS:
                variableProvider.addGlobalVariable(name, line, column);
                break;
            default:
                variableProvider.addLocalVariable(name, line, column);
                if (!scopeStack.isEmpty()) {
                    scopeStack.peek().add(name);
                }
        }
        
        SymbolInfo symbol = new SymbolInfo(name, SymbolInfo.SymbolType.VARIABLE, line, column);
        symbol.setEndLine(line);
        symbol.setEndColumn(column + name.length());
        symbol.setVariableInfo(info);
        symbolTable.put(name, symbol);
    }

    private boolean isVariableDefined(String name, TokenType scope) {
        switch (scope) {
            case KEYWORD_SS:
                return variableProvider.getInterfaceVariables().stream()
                        .anyMatch(v -> v.getName().equals(name));
            case KEYWORD_SSS:
                return variableProvider.getGlobalVariables().stream()
                        .anyMatch(v -> v.getName().equals(name));
            default:
                for (Set<String> scopeVars : scopeStack) {
                    if (scopeVars.contains(name)) {
                        return true;
                    }
                }
                return variableProvider.getLocalVariables().stream()
                        .anyMatch(v -> v.getName().equals(name)) ||
                       variableProvider.getFunctionParameters().stream()
                        .anyMatch(v -> v.getName().equals(name));
        }
    }

    private void pushScope() {
        scopeStack.push(new HashSet<>());
    }

    private void popScope() {
        if (!scopeStack.isEmpty()) {
            Set<String> scope = scopeStack.pop();
            for (String var : scope) {
                variableProvider.removeVariable(var);
            }
        }
    }

    public static class AnalysisResult {
        private final List<DiagnosticInfo> diagnostics;
        private final Map<String, SymbolInfo> symbolTable;
        private final VariableProvider variableProvider;

        public AnalysisResult(List<DiagnosticInfo> diagnostics, 
                             Map<String, SymbolInfo> symbolTable,
                             VariableProvider variableProvider) {
            this.diagnostics = diagnostics;
            this.symbolTable = symbolTable;
            this.variableProvider = variableProvider;
        }

        public List<DiagnosticInfo> getDiagnostics() {
            return diagnostics;
        }

        public Map<String, SymbolInfo> getSymbolTable() {
            return symbolTable;
        }

        public VariableProvider getVariableProvider() {
            return variableProvider;
        }
    }
}
