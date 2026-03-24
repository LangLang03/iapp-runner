package cn.langlang.iapp.lsp.core.model;

import cn.langlang.iapp.lexer.TokenType;

public class VariableInfo {
    private String name;
    private TokenType scope;
    private String inferredType;
    private int line;
    private int column;
    private String documentation;

    public VariableInfo() {
    }

    public VariableInfo(String name, TokenType scope, int line, int column) {
        this.name = name;
        this.scope = scope;
        this.line = line;
        this.column = column;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TokenType getScope() {
        return scope;
    }

    public void setScope(TokenType scope) {
        this.scope = scope;
    }

    public String getInferredType() {
        return inferredType;
    }

    public void setInferredType(String inferredType) {
        this.inferredType = inferredType;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public String getDisplayName() {
        if (scope == TokenType.KEYWORD_SS) {
            return "ss." + name;
        } else if (scope == TokenType.KEYWORD_SSS) {
            return "sss." + name;
        }
        return name;
    }

    public String getScopePrefix() {
        if (scope == TokenType.KEYWORD_SS) {
            return "ss.";
        } else if (scope == TokenType.KEYWORD_SSS) {
            return "sss.";
        }
        return "";
    }
}
