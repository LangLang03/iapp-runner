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
        T visitNumberLiteral(NumberLiteralExpression expr);
        T visitStringLiteral(StringLiteralExpression expr);
        T visitBooleanLiteral(BooleanLiteralExpression expr);
        T visitNullLiteral(NullLiteralExpression expr);
        T visitVariable(VariableExpression expr);
        T visitBinary(BinaryExpression expr) throws InterpreterException;
        T visitUnary(UnaryExpression expr) throws InterpreterException;
        T visitFunctionCall(FunctionCallExpression expr) throws InterpreterException;
        T visitArrayAccess(ArrayAccessExpression expr) throws InterpreterException;
        T visitMemberAccess(MemberAccessExpression expr) throws InterpreterException;
    }
}
