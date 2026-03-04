package cn.langlang.iapp.ast;

import java.util.ArrayList;
import java.util.List;

public class Program {
    private final List<Statement> statements;
    
    public Program() {
        this.statements = new ArrayList<>();
    }
    
    public void addStatement(Statement statement) {
        statements.add(statement);
    }
    
    public List<Statement> getStatements() {
        return statements;
    }
}
