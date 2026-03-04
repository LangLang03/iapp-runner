package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;
import cn.langlang.iapp.lexer.TokenType;

public class AssignmentStatement extends Statement {
    private final String variableName;
    private final Expression value;
    private final TokenType scope;
    private final Expression index;
    
    public AssignmentStatement(int line, String variableName, Expression value, TokenType scope) {
        super(line);
        this.variableName = variableName;
        this.value = value;
        this.scope = scope;
        this.index = null;
    }
    
    public AssignmentStatement(int line, String variableName, Expression index, Expression value, TokenType scope) {
        super(line);
        this.variableName = variableName;
        this.value = value;
        this.scope = scope;
        this.index = index;
    }
    
    public String getVariableName() {
        return variableName;
    }
    
    public Expression getValue() {
        return value;
    }
    
    public TokenType getScope() {
        return scope;
    }
    
    public Expression getIndex() {
        return index;
    }
    
    public boolean isArrayAssignment() {
        return index != null;
    }
    
    @Override
    public <T> T accept(StatementVisitor<T> visitor) throws InterpreterException {
        return visitor.visitAssignment(this);
    }
}
