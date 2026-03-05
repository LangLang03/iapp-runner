package cn.langlang.iapp.repl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputCompletenessChecker {
    
    public static class CheckResult {
        private final boolean isComplete;
        private final String message;
        
        public CheckResult(boolean isComplete, String message) {
            this.isComplete = isComplete;
            this.message = message;
        }
        
        public boolean isComplete() {
            return isComplete;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    private static final Pattern FN_PATTERN = Pattern.compile("\\bfn\\s+[a-zA-Z_][a-zA-Z0-9_]*\\s*\\(");
    private static final Pattern END_FN_PATTERN = Pattern.compile("\\bend\\s+fn\\b");
    private static final Pattern IF_PATTERN = Pattern.compile("\\bf\\s*\\([^)]*\\)\\s*$");
    private static final Pattern WHILE_PATTERN = Pattern.compile("\\bw\\s*\\([^)]*\\)\\s*$");
    private static final Pattern FOR_PATTERN = Pattern.compile("\\bfor\\s*\\([^)]*\\)\\s*$");
    private static final Pattern THREAD_PATTERN = Pattern.compile("\\bt\\s*\\(\\s*\\)\\s*$");
    private static final Pattern ELSE_PATTERN = Pattern.compile("\\belse\\s*$");
    
    public static CheckResult check(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new CheckResult(true, null);
        }
        
        String processedInput = removeComments(input);
        
        int braceCount = 0;
        int parenCount = 0;
        int bracketCount = 0;
        boolean inString = false;
        boolean escape = false;
        char stringChar = 0;
        
        for (int i = 0; i < processedInput.length(); i++) {
            char c = processedInput.charAt(i);
            
            if (inString) {
                if (escape) {
                    escape = false;
                } else if (c == '\\') {
                    escape = true;
                } else if (c == stringChar) {
                    inString = false;
                }
                continue;
            }
            
            switch (c) {
                case '"':
                    inString = true;
                    stringChar = '"';
                    break;
                case '\'':
                    inString = true;
                    stringChar = '\'';
                    break;
                case '{':
                    braceCount++;
                    break;
                case '}':
                    braceCount--;
                    break;
                case '(':
                    parenCount++;
                    break;
                case ')':
                    parenCount--;
                    break;
                case '[':
                    bracketCount++;
                    break;
                case ']':
                    bracketCount--;
                    break;
            }
        }
        
        if (parenCount > 0) {
            return new CheckResult(false, "缺少 " + parenCount + " 个 ')'");
        }
        
        if (bracketCount > 0) {
            return new CheckResult(false, "缺少 " + bracketCount + " 个 ']'");
        }
        
        if (braceCount > 0) {
            return new CheckResult(false, "缺少 " + braceCount + " 个 '}'");
        }
        
        if (braceCount < 0) {
            return new CheckResult(false, "多余的 '}'");
        }
        
        int fnCount = countMatches(FN_PATTERN, processedInput);
        int endFnCount = countMatches(END_FN_PATTERN, processedInput);
        
        if (fnCount > endFnCount) {
            return new CheckResult(false, "缺少 'end fn'");
        }
        
        if (fnCount < endFnCount) {
            return new CheckResult(false, "多余的 'end fn'");
        }
        
        if (inString) {
            return new CheckResult(false, "字符串未闭合");
        }
        
        if (braceCount == 0) {
            String[] lines = processedInput.split("\n");
            if (lines.length > 0) {
                String lastLine = lines[lines.length - 1].trim();
                
                if (IF_PATTERN.matcher(lastLine).find()) {
                    return new CheckResult(false, "缺少 '{'");
                }
                if (WHILE_PATTERN.matcher(lastLine).find()) {
                    return new CheckResult(false, "缺少 '{'");
                }
                if (FOR_PATTERN.matcher(lastLine).find()) {
                    return new CheckResult(false, "缺少 '{'");
                }
                if (THREAD_PATTERN.matcher(lastLine).find()) {
                    return new CheckResult(false, "缺少 '{'");
                }
                if (ELSE_PATTERN.matcher(lastLine).find()) {
                    return new CheckResult(false, "缺少 '{'");
                }
            }
        }
        
        return new CheckResult(true, null);
    }
    
    public static int getIndentLevel(String input) {
        if (input == null || input.trim().isEmpty()) {
            return 0;
        }
        
        String processedInput = removeComments(input);
        
        int braceCount = 0;
        boolean inString = false;
        boolean escape = false;
        char stringChar = 0;
        
        for (int i = 0; i < processedInput.length(); i++) {
            char c = processedInput.charAt(i);
            
            if (inString) {
                if (escape) {
                    escape = false;
                } else if (c == '\\') {
                    escape = true;
                } else if (c == stringChar) {
                    inString = false;
                }
                continue;
            }
            
            switch (c) {
                case '"':
                    inString = true;
                    stringChar = '"';
                    break;
                case '\'':
                    inString = true;
                    stringChar = '\'';
                    break;
                case '{':
                    braceCount++;
                    break;
                case '}':
                    braceCount--;
                    break;
            }
        }
        
        int indentLevel = Math.max(0, braceCount);
        
        int fnCount = countMatches(FN_PATTERN, processedInput);
        int endFnCount = countMatches(END_FN_PATTERN, processedInput);
        indentLevel += (fnCount - endFnCount);
        
        if (braceCount == 0) {
            String[] lines = processedInput.split("\n");
            if (lines.length > 0) {
                String lastLine = lines[lines.length - 1].trim();
                
                if (IF_PATTERN.matcher(lastLine).find() ||
                    WHILE_PATTERN.matcher(lastLine).find() ||
                    FOR_PATTERN.matcher(lastLine).find() ||
                    THREAD_PATTERN.matcher(lastLine).find() ||
                    ELSE_PATTERN.matcher(lastLine).find()) {
                    indentLevel++;
                }
            }
        }
        
        return Math.max(0, indentLevel);
    }
    
    private static String removeComments(String input) {
        String result = input;
        
        result = result.replaceAll("//.*", "");
        
        result = result.replaceAll("/\\..*?\\./", "");
        
        return result;
    }
    
    private static int countMatches(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
