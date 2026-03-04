package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;

import java.util.List;

public class ForStatement extends Statement {
    private final Expression start;
    private final Expression end;
    private final Expression step;
    private final String variableName;
    private final List<Statement> body;
    private final ForType forType;
    
    private final Statement initStatement;
    private final Expression condition;
    private final Statement updateStatement;
    
    public enum ForType {
        RANGE,
        ARRAY_ITERATION,
        C_STYLE
    }
    
    public ForStatement(int line, Expression start, Expression end, Expression step, List<Statement> body) {
        super(line);
        this.start = start;
        this.end = end;
        this.step = step;
        this.variableName = null;
        this.body = body;
        this.forType = ForType.RANGE;
        this.initStatement = null;
        this.condition = null;
        this.updateStatement = null;
    }
    
    public ForStatement(int line, String variableName, Expression array, List<Statement> body) {
        super(line);
        this.start = null;
        this.end = array;
        this.step = null;
        this.variableName = variableName;
        this.body = body;
        this.forType = ForType.ARRAY_ITERATION;
        this.initStatement = null;
        this.condition = null;
        this.updateStatement = null;
    }
    
    public ForStatement(int line, Statement initStatement, Expression condition, 
                        Statement updateStatement, List<Statement> body) {
        super(line);
        this.start = null;
        this.end = null;
        this.step = null;
        this.variableName = null;
        this.body = body;
        this.forType = ForType.C_STYLE;
        this.initStatement = initStatement;
        this.condition = condition;
        this.updateStatement = updateStatement;
    }
    
    public Expression getStart() {
        return start;
    }
    
    public Expression getEnd() {
        return end;
    }
    
    public Expression getStep() {
        return step;
    }
    
    public String getVariableName() {
        return variableName;
    }
    
    public List<Statement> getBody() {
        return body;
    }
    
    public ForType getForType() {
        return forType;
    }
    
    public Statement getInitStatement() {
        return initStatement;
    }
    
    public Expression getCondition() {
        return condition;
    }
    
    public Statement getUpdateStatement() {
        return updateStatement;
    }
    
    @Override
    public <T> T accept(StatementVisitor<T> visitor) throws InterpreterException {
        return visitor.visitFor(this);
    }
}
