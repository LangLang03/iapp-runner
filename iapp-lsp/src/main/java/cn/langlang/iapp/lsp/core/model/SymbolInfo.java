package cn.langlang.iapp.lsp.core.model;

public class SymbolInfo {
    public enum SymbolType {
        FUNCTION,
        VARIABLE,
        PARAMETER,
        USER_FUNCTION
    }

    private String name;
    private SymbolType type;
    private int line;
    private int column;
    private int endLine;
    private int endColumn;
    private String detail;
    private VariableInfo variableInfo;
    private FunctionInfo functionInfo;

    public SymbolInfo() {
    }

    public SymbolInfo(String name, SymbolType type, int line, int column) {
        this.name = name;
        this.type = type;
        this.line = line;
        this.column = column;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SymbolType getType() {
        return type;
    }

    public void setType(SymbolType type) {
        this.type = type;
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

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public int getEndColumn() {
        return endColumn;
    }

    public void setEndColumn(int endColumn) {
        this.endColumn = endColumn;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public VariableInfo getVariableInfo() {
        return variableInfo;
    }

    public void setVariableInfo(VariableInfo variableInfo) {
        this.variableInfo = variableInfo;
    }

    public FunctionInfo getFunctionInfo() {
        return functionInfo;
    }

    public void setFunctionInfo(FunctionInfo functionInfo) {
        this.functionInfo = functionInfo;
    }
}
