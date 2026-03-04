package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;

public abstract class Expression {
    private final int line;
    
    protected Expression(int line) {
        this.line = line;
    }
    
    public int getLine() {
        return line;
    }
    
    public abstract <T> T accept(ExpressionVisitor<T> visitor) throws InterpreterException;
    
    public interface ExpressionVisitor<T> {
        T visitNumberLiteral(NumberLiteralExpression expr) throws InterpreterException;
        T visitStringLiteral(StringLiteralExpression expr) throws InterpreterException;
        T visitBooleanLiteral(BooleanLiteralExpression expr) throws InterpreterException;
        T visitNullLiteral(NullLiteralExpression expr) throws InterpreterException;
        T visitVariable(VariableExpression expr) throws InterpreterException;
        T visitBinary(BinaryExpression expr) throws InterpreterException;
        T visitUnary(UnaryExpression expr) throws InterpreterException;
        T visitFunctionCall(FunctionCallExpression expr) throws InterpreterException;
        T visitArrayAccess(ArrayAccessExpression expr) throws InterpreterException;
        T visitMemberAccess(MemberAccessExpression expr) throws InterpreterException;
    }
}
