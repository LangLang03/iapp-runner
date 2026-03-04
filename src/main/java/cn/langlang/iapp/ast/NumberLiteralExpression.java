package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;

public class NumberLiteralExpression extends Expression {
    private final Number value;
    
    public NumberLiteralExpression(int line, Number value) {
        super(line);
        this.value = value;
    }
    
    public Number getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) throws InterpreterException {
        return visitor.visitNumberLiteral(this);
    }
}
