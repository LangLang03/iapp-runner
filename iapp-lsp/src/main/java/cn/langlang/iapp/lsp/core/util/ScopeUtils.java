package cn.langlang.iapp.lsp.core.util;

import cn.langlang.iapp.lexer.TokenType;

public final class ScopeUtils {

    private ScopeUtils() {
    }

    public static String getScopeDisplayName(TokenType scope) {
        if (scope == null) {
            return "局部变量 (s)";
        }
        
        switch (scope) {
            case KEYWORD_SS:
                return "界面变量 (ss)";
            case KEYWORD_SSS:
                return "全局变量 (sss)";
            case KEYWORD_S:
            default:
                return "局部变量 (s)";
        }
    }

    public static String getScopePrefix(TokenType scope) {
        if (scope == null) {
            return "";
        }
        
        switch (scope) {
            case KEYWORD_SS:
                return "ss.";
            case KEYWORD_SSS:
                return "sss.";
            case KEYWORD_S:
            default:
                return "";
        }
    }

    public static TokenType parseScopeFromPrefix(String variableName) {
        if (variableName == null || variableName.isEmpty()) {
            return TokenType.KEYWORD_S;
        }
        
        if (variableName.startsWith("sss.")) {
            return TokenType.KEYWORD_SSS;
        } else if (variableName.startsWith("ss.")) {
            return TokenType.KEYWORD_SS;
        } else {
            return TokenType.KEYWORD_S;
        }
    }

    public static String stripScopePrefix(String variableName) {
        if (variableName == null || variableName.isEmpty()) {
            return variableName;
        }
        
        if (variableName.startsWith("sss.")) {
            return variableName.substring(4);
        } else if (variableName.startsWith("ss.")) {
            return variableName.substring(3);
        } else {
            return variableName;
        }
    }

    public static boolean isGlobalScope(TokenType scope) {
        return scope == TokenType.KEYWORD_SSS;
    }

    public static boolean isInterfaceScope(TokenType scope) {
        return scope == TokenType.KEYWORD_SS;
    }

    public static boolean isLocalScope(TokenType scope) {
        return scope == null || scope == TokenType.KEYWORD_S;
    }
}
