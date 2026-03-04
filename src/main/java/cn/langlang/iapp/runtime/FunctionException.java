package cn.langlang.iapp.runtime;

public class FunctionException extends Exception {
    public FunctionException(String message) {
        super(message);
    }
    
    public FunctionException(String message, Throwable cause) {
        super(message, cause);
    }
}
