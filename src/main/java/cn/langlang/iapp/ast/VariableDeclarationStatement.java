package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;
import cn.langlang.iapp.lexer.TokenType;

public class VariableDeclarationStatement extends Statement {
    private final TokenType scope;
    private final String variableName;
    private final Expression initialValue;
    
    public VariableDeclarationStatement(int line, TokenType scope, String variableName, Expression initialValue) {
        super(line);
        this.scope = scope;
        this.variableName = variableName;
        this.initialValue = initialValue;
    }
    
    public TokenType getScope() {
        return scope;
    }
    
    public String getVariableName() {
        return variableName;
    }
    
    public Expression getInitialValue() {
        return initialValue;
    }
    
    @Override
    public <T> T accept(StatementVisitor<T> visitor) throws InterpreterException {
        return visitor.visitVariableDeclaration(this);
    }
}
