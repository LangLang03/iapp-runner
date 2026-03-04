package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;

import java.util.ArrayList;
import java.util.List;

public class IfStatement extends Statement {
    private final Expression condition;
    private final List<Statement> thenStatements;
    private final List<ElseIfClause> elseIfClauses;
    private final List<Statement> elseStatements;
    
    public IfStatement(int line, Expression condition, List<Statement> thenStatements) {
        super(line);
        this.condition = condition;
        this.thenStatements = thenStatements;
        this.elseIfClauses = new ArrayList<>();
        this.elseStatements = new ArrayList<>();
    }
    
    public Expression getCondition() {
        return condition;
    }
    
    public List<Statement> getThenStatements() {
        return thenStatements;
    }
    
    public List<ElseIfClause> getElseIfClauses() {
        return elseIfClauses;
    }
    
    public List<Statement> getElseStatements() {
        return elseStatements;
    }
    
    public void addElseIfClause(ElseIfClause clause) {
        elseIfClauses.add(clause);
    }
    
    public void setElseStatements(List<Statement> statements) {
        elseStatements.clear();
        elseStatements.addAll(statements);
    }
    
    @Override
    public <T> T accept(StatementVisitor<T> visitor) throws InterpreterException {
        return visitor.visitIf(this);
    }

    public record ElseIfClause(Expression condition, List<Statement> statements) {
    }
}
