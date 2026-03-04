package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;

public abstract class Statement {
    private final int line;
    
    protected Statement(int line) {
        this.line = line;
    }
    
    public int getLine() {
        return line;
    }
    
    public abstract <T> T accept(StatementVisitor<T> visitor) throws InterpreterException;
    
    public interface StatementVisitor<T> {
        T visitVariableDeclaration(VariableDeclarationStatement stmt) throws InterpreterException;
        T visitAssignment(AssignmentStatement stmt) throws InterpreterException;
        T visitIf(IfStatement stmt) throws InterpreterException;
        T visitWhile(WhileStatement stmt) throws InterpreterException;
        T visitFor(ForStatement stmt) throws InterpreterException;
        T visitFunctionCall(FunctionCallStatement stmt) throws InterpreterException;
        T visitBreak(BreakStatement stmt) throws InterpreterException;
        T visitEndCode(EndCodeStatement stmt) throws InterpreterException;
        T visitFunctionDefinition(FunctionDefinitionStatement stmt) throws InterpreterException;
        T visitThread(ThreadStatement stmt) throws InterpreterException;
        T visitBlock(BlockStatement stmt) throws InterpreterException;
    }
}
