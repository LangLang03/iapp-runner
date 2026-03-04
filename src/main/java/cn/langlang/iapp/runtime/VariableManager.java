package cn.langlang.iapp.runtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class VariableManager {
    private final Map<String, Object> localVariables;
    private final Map<String, Object> interfaceVariables;
    private final Map<String, Object> globalVariables;
    private final Stack<Set<String>> declaredVariablesStack;
    private final Set<String> declaredVariables;
    
    public VariableManager() {
        this.localVariables = new HashMap<>();
        this.interfaceVariables = new HashMap<>();
        this.globalVariables = new HashMap<>();
        this.declaredVariablesStack = new Stack<>();
        this.declaredVariables = new HashSet<>();
    }
    
    public void setLocalVariable(String name, Object value) {
        localVariables.put(name, value);
    }
    
    public Object getLocalVariable(String name) {
        return localVariables.get(name);
    }
    
    public void setInterfaceVariable(String name, Object value) {
        interfaceVariables.put(name, value);
    }
    
    public Object getInterfaceVariable(String name) {
        return interfaceVariables.get(name);
    }
    
    public void setGlobalVariable(String name, Object value) {
        globalVariables.put(name, value);
    }
    
    public Object getGlobalVariable(String name) {
        return globalVariables.get(name);
    }
    
    public Object getVariable(String name) {
        if (name.startsWith("sss.")) {
            return globalVariables.get(name.substring(4));
        } else if (name.startsWith("ss.")) {
            return interfaceVariables.get(name.substring(3));
        } else {
            Object value = localVariables.get(name);
            if (value == null) {
                value = interfaceVariables.get(name);
            }
            if (value == null) {
                value = globalVariables.get(name);
            }
            return value;
        }
    }
    
    public void setVariable(String name, Object value, cn.langlang.iapp.lexer.TokenType scope) {
        switch (scope) {
            case KEYWORD_S:
                localVariables.put(name, value);
                break;
            case KEYWORD_SS:
                interfaceVariables.put(name, value);
                break;
            case KEYWORD_SSS:
                globalVariables.put(name, value);
                break;
            default:
                localVariables.put(name, value);
        }
    }
    
    public boolean hasVariable(String name) {
        if (name.startsWith("sss.")) {
            return globalVariables.containsKey(name.substring(4));
        } else if (name.startsWith("ss.")) {
            return interfaceVariables.containsKey(name.substring(3));
        } else {
            return localVariables.containsKey(name) || 
                   interfaceVariables.containsKey(name) || 
                   globalVariables.containsKey(name);
        }
    }
    
    public void clearLocalVariables() {
        localVariables.clear();
    }
    
    public void clearInterfaceVariables() {
        interfaceVariables.clear();
    }
    
    public void clearGlobalVariables() {
        globalVariables.clear();
    }
    
    public void pushScope() {
        Set<String> currentDeclared = new HashSet<>(declaredVariables);
        declaredVariablesStack.push(currentDeclared);
    }
    
    public void popScope() {
        localVariables.clear();
        if (!declaredVariablesStack.isEmpty()) {
            Set<String> previousDeclared = declaredVariablesStack.pop();
            declaredVariables.clear();
            declaredVariables.addAll(previousDeclared);
        }
    }
    
    public void declareVariable(String name) {
        declaredVariables.add(name);
    }
    
    public boolean isVariableDeclared(String name) {
        return declaredVariables.contains(name);
    }
}
