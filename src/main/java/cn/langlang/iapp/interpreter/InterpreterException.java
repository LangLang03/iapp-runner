package cn.langlang.iapp.interpreter;

public class InterpreterException extends Exception {
    private final int line;
    
    public InterpreterException(String message) {
        super(message);
        this.line = -1;
    }
    
    public InterpreterException(String message, int line) {
        super(message + " at line " + line);
        this.line = line;
    }
    
    public InterpreterException(String message, Throwable cause) {
        super(message, cause);
        this.line = -1;
    }
    
    public InterpreterException(String message, int line, Throwable cause) {
        super(message + " at line " + line, cause);
        this.line = line;
    }
    
    public int getLine() {
        return line;
    }
}
