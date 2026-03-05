package cn.langlang.iapp.runtime;

import cn.langlang.iapp.lexer.TokenType;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class VariableManager {
    private final Stack<Map<String, Object>> scopeStack;
    private final Map<String, Object> interfaceVariables;
    private final Map<String, Object> globalVariables;
    
    public VariableManager() {
        this.scopeStack = new Stack<>();
        this.scopeStack.push(new HashMap<>());
        this.interfaceVariables = new HashMap<>();
        this.globalVariables = new HashMap<>();
    }
    
    public void setVariable(String name, Object value) {
        scopeStack.peek().put(name, value);
    }
    
    public Object getVariable(String name) {
        if (name.startsWith("sss.")) {
            return globalVariables.get(name.substring(4));
        } else if (name.startsWith("ss.")) {
            return interfaceVariables.get(name.substring(3));
        } else {
            for (int i = scopeStack.size() - 1; i >= 0; i--) {
                Map<String, Object> scope = scopeStack.get(i);
                if (scope.containsKey(name)) {
                    return scope.get(name);
                }
            }
            if (interfaceVariables.containsKey(name)) {
                return interfaceVariables.get(name);
            }
            return globalVariables.get(name);
        }
    }
    
    public void setVariable(String name, Object value, TokenType scope) {
        switch (scope) {
            case KEYWORD_S:
                scopeStack.peek().put(name, value);
                break;
            case KEYWORD_SS:
                interfaceVariables.put(name, value);
                break;
            case KEYWORD_SSS:
                globalVariables.put(name, value);
                break;
            default:
                scopeStack.peek().put(name, value);
        }
    }
    
    public boolean hasVariable(String name) {
        if (name.startsWith("sss.")) {
            return globalVariables.containsKey(name.substring(4));
        } else if (name.startsWith("ss.")) {
            return interfaceVariables.containsKey(name.substring(3));
        } else {
            for (int i = scopeStack.size() - 1; i >= 0; i--) {
                if (scopeStack.get(i).containsKey(name)) {
                    return true;
                }
            }
            return interfaceVariables.containsKey(name) || globalVariables.containsKey(name);
        }
    }
    
    public void pushScope() {
        scopeStack.push(new HashMap<>());
    }
    
    public void popScope() {
        if (scopeStack.size() > 1) {
            scopeStack.pop();
        }
    }
    
    public void clearLocalVariables() {
        scopeStack.peek().clear();
    }
    
    public void clearInterfaceVariables() {
        interfaceVariables.clear();
    }
    
    public void clearGlobalVariables() {
        globalVariables.clear();
    }
    
    public Map<String, Object> getLocalVariables() {
        return scopeStack.peek();
    }
    
    public Map<String, Object> getInterfaceVariables() {
        return interfaceVariables;
    }
    
    public Map<String, Object> getGlobalVariables() {
        return globalVariables;
    }
}
