package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;
import cn.langlang.iapp.lexer.TokenType;

import java.util.List;

public class FunctionCallStatement extends Statement {
    private final String functionName;
    private final List<Expression> arguments;
    private final List<String> outputVariables;
    private final TokenType resultScope;
    
    public FunctionCallStatement(int line, String functionName, List<Expression> arguments, 
                                  List<String> outputVariables, TokenType resultScope) {
        super(line);
        this.functionName = functionName;
        this.arguments = arguments;
        this.outputVariables = outputVariables;
        this.resultScope = resultScope;
    }
    
    public String getFunctionName() {
        return functionName;
    }
    
    public List<Expression> getArguments() {
        return arguments;
    }
    
    public List<String> getOutputVariables() {
        return outputVariables;
    }
    
    public TokenType getResultScope() {
        return resultScope;
    }
    
    public boolean hasOutputVariables() {
        return outputVariables != null && !outputVariables.isEmpty();
    }
    
    @Override
    public <T> T accept(StatementVisitor<T> visitor) throws InterpreterException {
        return visitor.visitFunctionCall(this);
    }
}
