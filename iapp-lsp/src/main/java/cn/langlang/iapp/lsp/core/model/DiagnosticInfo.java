package cn.langlang.iapp.lsp.core.model;

public class DiagnosticInfo {
    public enum Severity {
        ERROR,
        WARNING,
        INFORMATION,
        HINT
    }

    private String message;
    private Severity severity;
    private int line;
    private int column;
    private int endLine;
    private int endColumn;
    private String source;
    private String code;

    public DiagnosticInfo() {
    }

    public DiagnosticInfo(String message, Severity severity, int line, int column) {
        this.message = message;
        this.severity = severity;
        this.line = line;
        this.column = column;
        this.endLine = line;
        this.endColumn = column;
    }

    public DiagnosticInfo(String message, Severity severity, int line, int column, int endLine, int endColumn) {
        this.message = message;
        this.severity = severity;
        this.line = line;
        this.column = column;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
