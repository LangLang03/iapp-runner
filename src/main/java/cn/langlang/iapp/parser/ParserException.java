package cn.langlang.iapp.parser;

public class ParserException extends Exception {
    private final int line;
    private final int column;
    
    public ParserException(String message, int line, int column) {
        super(message + " at line " + line + ", column " + column);
        this.line = line;
        this.column = column;
    }
    
    public int getLine() {
        return line;
    }
    
    public int getColumn() {
        return column;
    }
}
