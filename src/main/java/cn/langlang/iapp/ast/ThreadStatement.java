package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;

import java.util.List;

public class ThreadStatement extends Statement {
    private final List<Statement> body;
    
    public ThreadStatement(int line, List<Statement> body) {
        super(line);
        this.body = body;
    }
    
    public List<Statement> getBody() {
        return body;
    }
    
    @Override
    public <T> T accept(StatementVisitor<T> visitor) throws InterpreterException {
        return visitor.visitThread(this);
    }
}
