package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;

public class ArrayAccessExpression extends Expression {
    private final Expression array;
    private final Expression index;
    
    public ArrayAccessExpression(int line, Expression array, Expression index) {
        super(line);
        this.array = array;
        this.index = index;
    }
    
    public Expression getArray() {
        return array;
    }
    
    public Expression getIndex() {
        return index;
    }
    
    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) throws InterpreterException {
        return visitor.visitArrayAccess(this);
    }
}
