package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;

public class BooleanLiteralExpression extends Expression {
    private final boolean value;
    
    public BooleanLiteralExpression(int line, boolean value) {
        super(line);
        this.value = value;
    }
    
    public boolean getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) throws InterpreterException {
        return visitor.visitBooleanLiteral(this);
    }
}
