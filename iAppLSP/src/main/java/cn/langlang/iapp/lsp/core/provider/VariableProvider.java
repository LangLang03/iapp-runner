package cn.langlang.iapp.lsp.core.provider;

import cn.langlang.iapp.lsp.core.LSContext;
import cn.langlang.iapp.lsp.core.model.VariableInfo;
import cn.langlang.iapp.lexer.TokenType;

import java.util.*;

public class VariableProvider {
    private final LSContext context;
    private final Map<String, VariableInfo> localVariables;
    private final Map<String, VariableInfo> interfaceVariables;
    private final Map<String, VariableInfo> globalVariables;
    private final Map<String, VariableInfo> userFunctionParams;

    public VariableProvider(LSContext context) {
        this.context = context;
        this.localVariables = new LinkedHashMap<>();
        this.interfaceVariables = new LinkedHashMap<>();
        this.globalVariables = new LinkedHashMap<>();
        this.userFunctionParams = new LinkedHashMap<>();
    }

    public void addLocalVariable(String name, int line, int column) {
        VariableInfo info = new VariableInfo(name, TokenType.KEYWORD_S, line, column);
        localVariables.put(name, info);
    }

    public void addInterfaceVariable(String name, int line, int column) {
        VariableInfo info = new VariableInfo(name, TokenType.KEYWORD_SS, line, column);
        interfaceVariables.put(name, info);
    }

    public void addGlobalVariable(String name, int line, int column) {
        VariableInfo info = new VariableInfo(name, TokenType.KEYWORD_SSS, line, column);
        globalVariables.put(name, info);
    }

    public void addFunctionParameter(String name, int line, int column) {
        VariableInfo info = new VariableInfo(name, TokenType.KEYWORD_S, line, column);
        userFunctionParams.put(name, info);
    }

    public VariableInfo getVariable(String name) {
        VariableInfo info = localVariables.get(name);
        if (info != null) return info;
        
        info = interfaceVariables.get(name);
        if (info != null) return info;
        
        info = globalVariables.get(name);
        if (info != null) return info;
        
        return userFunctionParams.get(name);
    }

    public VariableInfo getVariableWithScope(String name, TokenType scope) {
        switch (scope) {
            case KEYWORD_SS:
                return interfaceVariables.get(name);
            case KEYWORD_SSS:
                return globalVariables.get(name);
            default:
                return localVariables.get(name);
        }
    }

    public List<VariableInfo> getAllVariables() {
        List<VariableInfo> all = new ArrayList<>();
        all.addAll(localVariables.values());
        all.addAll(interfaceVariables.values());
        all.addAll(globalVariables.values());
        all.addAll(userFunctionParams.values());
        return all;
    }

    public List<VariableInfo> getLocalVariables() {
        return new ArrayList<>(localVariables.values());
    }

    public List<VariableInfo> getInterfaceVariables() {
        return new ArrayList<>(interfaceVariables.values());
    }

    public List<VariableInfo> getGlobalVariables() {
        return new ArrayList<>(globalVariables.values());
    }

    public List<VariableInfo> getFunctionParameters() {
        return new ArrayList<>(userFunctionParams.values());
    }

    public List<VariableInfo> getVariablesByPrefix(String prefix) {
        List<VariableInfo> result = new ArrayList<>();
        String lowerPrefix = prefix.toLowerCase();
        
        for (VariableInfo info : getAllVariables()) {
            if (info.getName().toLowerCase().startsWith(lowerPrefix)) {
                result.add(info);
            }
        }
        
        return result;
    }

    public List<VariableInfo> getScopedVariablesByPrefix(String prefix, TokenType scope) {
        List<VariableInfo> result = new ArrayList<>();
        String lowerPrefix = prefix.toLowerCase();
        
        switch (scope) {
            case KEYWORD_SS:
                for (VariableInfo info : interfaceVariables.values()) {
                    if (info.getName().toLowerCase().startsWith(lowerPrefix)) {
                        result.add(info);
                    }
                }
                break;
            case KEYWORD_SSS:
                for (VariableInfo info : globalVariables.values()) {
                    if (info.getName().toLowerCase().startsWith(lowerPrefix)) {
                        result.add(info);
                    }
                }
                break;
            default:
                for (VariableInfo info : localVariables.values()) {
                    if (info.getName().toLowerCase().startsWith(lowerPrefix)) {
                        result.add(info);
                    }
                }
                for (VariableInfo info : userFunctionParams.values()) {
                    if (info.getName().toLowerCase().startsWith(lowerPrefix)) {
                        result.add(info);
                    }
                }
        }
        
        return result;
    }

    public boolean hasVariable(String name) {
        return localVariables.containsKey(name) ||
               interfaceVariables.containsKey(name) ||
               globalVariables.containsKey(name) ||
               userFunctionParams.containsKey(name);
    }

    public void clearLocalScope() {
        localVariables.clear();
        userFunctionParams.clear();
    }

    public void clearAll() {
        localVariables.clear();
        interfaceVariables.clear();
        globalVariables.clear();
        userFunctionParams.clear();
    }

    public void removeVariable(String name) {
        localVariables.remove(name);
        interfaceVariables.remove(name);
        globalVariables.remove(name);
        userFunctionParams.remove(name);
    }
}
