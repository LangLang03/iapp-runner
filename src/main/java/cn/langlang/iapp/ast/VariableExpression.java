package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;
import cn.langlang.iapp.lexer.TokenType;

public class VariableExpression extends Expression {
    private final String name;
    private final TokenType scope;
    
    public VariableExpression(int line, String name, TokenType scope) {
        super(line);
        this.name = name;
        this.scope = scope;
    }
    
    public String getName() {
        return name;
    }
    
    public TokenType getScope() {
        return scope;
    }
    
    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) throws InterpreterException {
        return visitor.visitVariable(this);
    }
}
