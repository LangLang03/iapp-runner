package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;

public class EndCodeStatement extends Statement {
    public EndCodeStatement(int line) {
        super(line);
    }
    
    @Override
    public <T> T accept(StatementVisitor<T> visitor) throws InterpreterException {
        return visitor.visitEndCode(this);
    }
}
