package cn.langlang.iapp.runtime;

import cn.langlang.iapp.lexer.TokenType;

import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;

public class VariableManager {
    private final Deque<Map<String, Object>> scopeStack;
    private final ReentrantLock scopeLock;
    private final Map<String, Object> interfaceVariables;
    private final Map<String, Object> globalVariables;
    private final ReentrantLock globalLock;
    
    public VariableManager() {
        this.scopeStack = new ConcurrentLinkedDeque<>();
        this.scopeStack.push(new ConcurrentHashMap<>());
        this.scopeLock = new ReentrantLock();
        this.interfaceVariables = new ConcurrentHashMap<>();
        this.globalVariables = new ConcurrentHashMap<>();
        this.globalLock = new ReentrantLock();
    }
    
    public void setVariable(String name, Object value) {
        scopeLock.lock();
        try {
            Map<String, Object> currentScope = scopeStack.peek();
            if (currentScope != null) {
                currentScope.put(name, value);
            }
        } finally {
            scopeLock.unlock();
        }
    }
    
    public Object getVariable(String name) {
        if (name == null) {
            return null;
        }
        
        if (name.startsWith("sss.")) {
            return globalVariables.get(name.substring(4));
        } else if (name.startsWith("ss.")) {
            return interfaceVariables.get(name.substring(3));
        } else {
            scopeLock.lock();
            try {
                for (Map<String, Object> scope : scopeStack) {
                    if (scope.containsKey(name)) {
                        return scope.get(name);
                    }
                }
            } finally {
                scopeLock.unlock();
            }
            if (interfaceVariables.containsKey(name)) {
                return interfaceVariables.get(name);
            }
            if (globalVariables.containsKey(name)) {
                return globalVariables.get(name);
            }
            return null;
        }
    }
    
    public void setVariable(String name, Object value, TokenType scope) {
        switch (scope) {
            case KEYWORD_S:
                setVariable(name, value);
                break;
            case KEYWORD_SS:
                interfaceVariables.put(name, value);
                break;
            case KEYWORD_SSS:
                globalVariables.put(name, value);
                break;
            default:
                setVariable(name, value);
        }
    }
    
    public boolean hasVariable(String name) {
        if (name == null) {
            return false;
        }
        
        if (name.startsWith("sss.")) {
            return globalVariables.containsKey(name.substring(4));
        } else if (name.startsWith("ss.")) {
            return interfaceVariables.containsKey(name.substring(3));
        } else {
            scopeLock.lock();
            try {
                for (Map<String, Object> scope : scopeStack) {
                    if (scope.containsKey(name)) {
                        return true;
                    }
                }
            } finally {
                scopeLock.unlock();
            }
            return interfaceVariables.containsKey(name) || globalVariables.containsKey(name);
        }
    }
    
    public void pushScope() {
        scopeLock.lock();
        try {
            scopeStack.push(new ConcurrentHashMap<>());
        } finally {
            scopeLock.unlock();
        }
    }
    
    public void popScope() {
        scopeLock.lock();
        try {
            if (scopeStack.size() > 1) {
                scopeStack.pop();
            }
        } finally {
            scopeLock.unlock();
        }
    }
    
    public void popScopeAndPromoteVariables() {
        scopeLock.lock();
        try {
            if (scopeStack.size() > 1) {
                scopeStack.pop();
            }
        } finally {
            scopeLock.unlock();
        }
    }
    
    public void promoteVariableToParent(String name, Object value) {
        scopeLock.lock();
        try {
            if (scopeStack.size() > 1) {
                Map<String, Object>[] scopes = scopeStack.toArray(new Map[0]);
                if (scopes.length >= 2) {
                    scopes[scopes.length - 2].put(name, value);
                }
            }
        } finally {
            scopeLock.unlock();
        }
    }
    
    public void setVariableInParentScope(String name, Object value) {
        scopeLock.lock();
        try {
            if (scopeStack.size() > 1) {
                Map<String, Object>[] scopes = scopeStack.toArray(new Map[0]);
                if (scopes.length >= 2) {
                    scopes[scopes.length - 2].put(name, value);
                } else {
                    scopeStack.peek().put(name, value);
                }
            } else {
                scopeStack.peek().put(name, value);
            }
        } finally {
            scopeLock.unlock();
        }
    }
    
    public void clearLocalVariables() {
        scopeLock.lock();
        try {
            Map<String, Object> currentScope = scopeStack.peek();
            if (currentScope != null) {
                currentScope.clear();
            }
        } finally {
            scopeLock.unlock();
        }
    }
    
    public void clearInterfaceVariables() {
        interfaceVariables.clear();
    }
    
    public void clearGlobalVariables() {
        globalVariables.clear();
    }
    
    public Map<String, Object> getLocalVariables() {
        scopeLock.lock();
        try {
            Map<String, Object> currentScope = scopeStack.peek();
            if (currentScope != null) {
                return Collections.unmodifiableMap(new HashMap<>(currentScope));
            }
            return Collections.emptyMap();
        } finally {
            scopeLock.unlock();
        }
    }
    
    public Map<String, Object> getInterfaceVariables() {
        return Collections.unmodifiableMap(new HashMap<>(interfaceVariables));
    }
    
    public Map<String, Object> getGlobalVariables() {
        return Collections.unmodifiableMap(new HashMap<>(globalVariables));
    }
    
    public void clearAll() {
        scopeLock.lock();
        try {
            scopeStack.clear();
            scopeStack.push(new ConcurrentHashMap<>());
        } finally {
            scopeLock.unlock();
        }
        interfaceVariables.clear();
        globalVariables.clear();
    }
    
    public void resetForReuse() {
        scopeLock.lock();
        try {
            scopeStack.clear();
            scopeStack.push(new ConcurrentHashMap<>());
        } finally {
            scopeLock.unlock();
        }
        interfaceVariables.clear();
    }
}
