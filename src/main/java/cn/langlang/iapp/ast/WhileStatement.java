package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;

import java.util.List;

public class WhileStatement extends Statement {
    private final Expression condition;
    private final List<Statement> body;
    
    public WhileStatement(int line, Expression condition, List<Statement> body) {
        super(line);
        this.condition = condition;
        this.body = body;
    }
    
    public Expression getCondition() {
        return condition;
    }
    
    public List<Statement> getBody() {
        return body;
    }
    
    @Override
    public <T> T accept(StatementVisitor<T> visitor) throws InterpreterException {
        return visitor.visitWhile(this);
    }
}
