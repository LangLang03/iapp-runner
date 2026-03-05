package cn.langlang.iapp.ast;

import cn.langlang.iapp.interpreter.InterpreterException;

import java.util.List;

public class FunctionDefinitionStatement extends Statement {
    private final String moduleName;
    private final String functionName;
    private final List<String> parameters;
    private final List<Statement> body;
    
    public FunctionDefinitionStatement(int line, String moduleName, String functionName, 
                                        List<String> parameters, List<Statement> body) {
        super(line);
        this.moduleName = moduleName;
        this.functionName = functionName;
        this.parameters = parameters;
        this.body = body;
    }
    
    public String getModuleName() {
        return moduleName;
    }
    
    public String getFunctionName() {
        return functionName;
    }
    
    public List<String> getParameters() {
        return parameters;
    }
    
    public List<Statement> getBody() {
        return body;
    }
    
    public String getFullName() {
        if (moduleName != null && !moduleName.isEmpty()) {
            return moduleName + "." + functionName;
        }
        return functionName;
    }
    
    @Override
    public <T> T accept(StatementVisitor<T> visitor) throws InterpreterException {
        return visitor.visitFunctionDefinition(this);
    }
}
