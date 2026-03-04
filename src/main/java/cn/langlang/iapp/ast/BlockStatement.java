package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;

import java.util.List;

public class BlockStatement extends Statement {
    private final List<Statement> statements;
    
    public BlockStatement(int line, List<Statement> statements) {
        super(line);
        this.statements = statements;
    }
    
    public List<Statement> getStatements() {
        return statements;
    }
    
    @Override
    public <T> T accept(StatementVisitor<T> visitor) throws InterpreterException {
        return visitor.visitBlock(this);
    }
}
