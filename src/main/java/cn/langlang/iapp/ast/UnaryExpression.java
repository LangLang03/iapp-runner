package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;
import cn.langlang.iapp.lexer.TokenType;

public class UnaryExpression extends Expression {
    private final TokenType operator;
    private final Expression operand;
    private final boolean prefix;
    
    public UnaryExpression(int line, TokenType operator, Expression operand, boolean prefix) {
        super(line);
        this.operator = operator;
        this.operand = operand;
        this.prefix = prefix;
    }
    
    public TokenType getOperator() {
        return operator;
    }
    
    public Expression getOperand() {
        return operand;
    }
    
    public boolean isPrefix() {
        return prefix;
    }
    
    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) throws InterpreterException {
        return visitor.visitUnary(this);
    }
}
