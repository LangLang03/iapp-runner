package cn.langlang.iapp.api;

public class IAppScriptException extends RuntimeException {
    
    public IAppScriptException(String message) {
        super(message);
    }
    
    public IAppScriptException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public IAppScriptException(Throwable cause) {
        super(cause);
    }
}
