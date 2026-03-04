package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;

public class NullLiteralExpression extends Expression {
    public NullLiteralExpression(int line) {
        super(line);
    }
    
    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) throws InterpreterException {
        return visitor.visitNullLiteral(this);
    }
}
