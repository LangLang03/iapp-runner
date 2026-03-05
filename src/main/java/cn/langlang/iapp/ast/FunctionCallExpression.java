package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;

import java.util.List;

public class FunctionCallExpression extends Expression {
    private final String functionName;
    private final List<Expression> arguments;
    
    public FunctionCallExpression(int line, String functionName, List<Expression> arguments) {
        super(line);
        this.functionName = functionName;
        this.arguments = arguments;
    }
    
    public String getFunctionName() {
        return functionName;
    }
    
    public List<Expression> getArguments() {
        return arguments;
    }
    
    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) throws InterpreterException {
        return visitor.visitFunctionCall(this);
    }
}
