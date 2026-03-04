package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;

public class StringLiteralExpression extends Expression {
    private final String value;
    
    public StringLiteralExpression(int line, String value) {
        super(line);
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) throws InterpreterException {
        return visitor.visitStringLiteral(this);
    }
}
