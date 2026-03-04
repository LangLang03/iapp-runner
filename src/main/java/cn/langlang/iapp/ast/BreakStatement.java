package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;

public class BreakStatement extends Statement {
    public BreakStatement(int line) {
        super(line);
    }
    
    @Override
    public <T> T accept(StatementVisitor<T> visitor) throws InterpreterException {
        return visitor.visitBreak(this);
    }
}
