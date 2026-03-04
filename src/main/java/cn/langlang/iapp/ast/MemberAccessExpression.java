package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;

public class MemberAccessExpression extends Expression {
    private final Expression object;
    private final String memberName;
    
    public MemberAccessExpression(int line, Expression object, String memberName) {
        super(line);
        this.object = object;
        this.memberName = memberName;
    }
    
    public Expression getObject() {
        return object;
    }
    
    public String getMemberName() {
        return memberName;
    }
    
    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) throws InterpreterException {
        return visitor.visitMemberAccess(this);
    }
}
