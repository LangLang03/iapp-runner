package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;
import cn.langlang.iapp.lexer.TokenType;

import java.util.List;

public class FunctionCallStatement extends Statement {
    private final String functionName;
    private final List<Expression> arguments;
    private final String resultVariable;
    private final TokenType resultScope;
    private final String potentialOutputVariable;
    
    public FunctionCallStatement(int line, String functionName, List<Expression> arguments, 
                                  String resultVariable, TokenType resultScope) {
        super(line);
        this.functionName = functionName;
        this.arguments = arguments;
        this.resultVariable = resultVariable;
        this.resultScope = resultScope;
        this.potentialOutputVariable = null;
    }
    
    public FunctionCallStatement(int line, String functionName, List<Expression> arguments, 
                                  String resultVariable, TokenType resultScope, 
                                  String potentialOutputVariable) {
        super(line);
        this.functionName = functionName;
        this.arguments = arguments;
        this.resultVariable = resultVariable;
        this.resultScope = resultScope;
        this.potentialOutputVariable = potentialOutputVariable;
    }
    
    public String getFunctionName() {
        return functionName;
    }
    
    public List<Expression> getArguments() {
        return arguments;
    }
    
    public String getResultVariable() {
        return resultVariable;
    }
    
    public TokenType getResultScope() {
        return resultScope;
    }
    
    public String getPotentialOutputVariable() {
        return potentialOutputVariable;
    }
    
    public boolean hasResultVariable() {
        return resultVariable != null && !resultVariable.isEmpty();
    }
    
    public boolean hasPotentialOutputVariable() {
        return potentialOutputVariable != null && !potentialOutputVariable.isEmpty();
    }
    
    @Override
    public <T> T accept(StatementVisitor<T> visitor) throws InterpreterException {
        return visitor.visitFunctionCall(this);
    }
}
