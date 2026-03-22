package cn.langlang.iapp.lsp.core.provider;

import cn.langlang.iapp.lsp.core.LSContext;
import cn.langlang.iapp.lsp.core.model.FunctionInfo;
import cn.langlang.iapp.lsp.core.model.VariableInfo;
import cn.langlang.iapp.lsp.core.util.ScopeUtils;
import cn.langlang.iapp.lsp.registry.FunctionCategory;
import cn.langlang.iapp.lexer.TokenType;

import java.util.*;
import java.util.stream.Collectors;

public class CompletionProvider {
    private final LSContext context;
    private final FunctionProvider functionProvider;
    private final VariableProvider variableProvider;

    private static final Map<String, String> KEYWORDS = new LinkedHashMap<>();
    
    static {
        KEYWORDS.put("s", "声明局部变量");
        KEYWORDS.put("ss", "声明界面变量");
        KEYWORDS.put("sss", "声明全局变量");
        KEYWORDS.put("f", "条件语句 (if)");
        KEYWORDS.put("else", "否则分支");
        KEYWORDS.put("w", "循环语句 (while)");
        KEYWORDS.put("for", "for循环");
        KEYWORDS.put("break", "跳出循环");
        KEYWORDS.put("fn", "定义函数");
        KEYWORDS.put("end", "结束代码块");
        KEYWORDS.put("true", "布尔值 true");
        KEYWORDS.put("false", "布尔值 false");
        KEYWORDS.put("null", "空值");
        KEYWORDS.put("t", "线程执行");
        KEYWORDS.put("endcode", "结束代码执行");
    }

    public CompletionProvider(LSContext context) {
        this.context = context;
        this.functionProvider = new FunctionProvider(context);
        this.variableProvider = new VariableProvider(context);
    }

    public CompletionProvider(LSContext context, FunctionProvider functionProvider, VariableProvider variableProvider) {
        this.context = context;
        this.functionProvider = functionProvider != null ? functionProvider : new FunctionProvider(context);
        this.variableProvider = variableProvider != null ? variableProvider : new VariableProvider(context);
    }

    public List<CompletionItem> getCompletions(String prefix) {
        List<CompletionItem> items = new ArrayList<>();
        String lowerPrefix = prefix != null ? prefix.toLowerCase() : "";
        
        items.addAll(getKeywordCompletions(lowerPrefix));
        items.addAll(getFunctionCompletions(lowerPrefix));
        items.addAll(getVariableCompletions(lowerPrefix));
        
        return items;
    }

    public List<CompletionItem> getFunctionCompletions(String prefix) {
        List<CompletionItem> items = new ArrayList<>();
        String lowerPrefix = prefix != null ? prefix.toLowerCase() : "";
        
        List<FunctionInfo> functions = functionProvider.getAllFunctions();
        for (FunctionInfo func : functions) {
            if (func.getName().toLowerCase().startsWith(lowerPrefix)) {
                items.add(createFunctionCompletion(func));
            }
        }
        
        return items;
    }

    public List<CompletionItem> getFunctionCompletionsByCategory(FunctionCategory category, String prefix) {
        List<CompletionItem> items = new ArrayList<>();
        String lowerPrefix = prefix != null ? prefix.toLowerCase() : "";
        
        List<FunctionInfo> functions = functionProvider.getFunctionsByCategory(category);
        for (FunctionInfo func : functions) {
            if (func.getName().toLowerCase().startsWith(lowerPrefix)) {
                items.add(createFunctionCompletion(func));
            }
        }
        
        return items;
    }

    public List<CompletionItem> getVariableCompletions(String prefix) {
        List<CompletionItem> items = new ArrayList<>();
        String lowerPrefix = prefix != null ? prefix.toLowerCase() : "";
        
        for (VariableInfo var : variableProvider.getAllVariables()) {
            if (var.getName().toLowerCase().startsWith(lowerPrefix)) {
                items.add(createVariableCompletion(var));
            }
        }
        
        return items;
    }

    public List<CompletionItem> getScopedVariableCompletions(String prefix, TokenType scope) {
        List<CompletionItem> items = new ArrayList<>();
        String lowerPrefix = prefix != null ? prefix.toLowerCase() : "";
        
        List<VariableInfo> vars = variableProvider.getScopedVariablesByPrefix(lowerPrefix, scope);
        for (VariableInfo var : vars) {
            items.add(createVariableCompletion(var));
        }
        
        return items;
    }

    public List<CompletionItem> getKeywordCompletions(String prefix) {
        List<CompletionItem> items = new ArrayList<>();
        String lowerPrefix = prefix != null ? prefix.toLowerCase() : "";
        
        for (Map.Entry<String, String> entry : KEYWORDS.entrySet()) {
            if (entry.getKey().startsWith(lowerPrefix)) {
                items.add(createKeywordCompletion(entry.getKey(), entry.getValue()));
            }
        }
        
        return items;
    }

    public List<CompletionItem> getScopePrefixCompletions(String prefix) {
        List<CompletionItem> items = new ArrayList<>();
        String lowerPrefix = prefix != null ? prefix.toLowerCase() : "";
        
        if ("ss".startsWith(lowerPrefix)) {
            items.add(createScopePrefixCompletion("ss.", "界面变量前缀"));
        }
        if ("sss".startsWith(lowerPrefix)) {
            items.add(createScopePrefixCompletion("sss.", "全局变量前缀"));
        }
        
        return items;
    }

    private CompletionItem createFunctionCompletion(FunctionInfo func) {
        CompletionItem item = new CompletionItem();
        item.setLabel(func.getName());
        item.setKind(CompletionItemKind.FUNCTION);
        item.setDetail(func.getCategory() != null ? func.getCategory().getDisplayName() : "函数");
        item.setDocumentation(func.getDocumentation());
        item.setInsertText(func.getInsertText());
        item.setSortText("1" + func.getName());
        return item;
    }

    private CompletionItem createVariableCompletion(VariableInfo var) {
        CompletionItem item = new CompletionItem();
        item.setLabel(var.getDisplayName());
        item.setKind(CompletionItemKind.VARIABLE);
        item.setDetail(ScopeUtils.getScopeDisplayName(var.getScope()));
        item.setInsertText(var.getName());
        item.setSortText("2" + var.getName());
        return item;
    }

    private CompletionItem createKeywordCompletion(String keyword, String description) {
        CompletionItem item = new CompletionItem();
        item.setLabel(keyword);
        item.setKind(CompletionItemKind.KEYWORD);
        item.setDetail(description);
        item.setInsertText(keyword);
        item.setSortText("0" + keyword);
        return item;
    }

    private CompletionItem createScopePrefixCompletion(String prefix, String description) {
        CompletionItem item = new CompletionItem();
        item.setLabel(prefix);
        item.setKind(CompletionItemKind.KEYWORD);
        item.setDetail(description);
        item.setInsertText(prefix);
        item.setSortText("0" + prefix);
        return item;
    }

    public FunctionProvider getFunctionProvider() {
        return functionProvider;
    }

    public VariableProvider getVariableProvider() {
        return variableProvider;
    }

    public static class CompletionItem {
        private String label;
        private CompletionItemKind kind;
        private String detail;
        private String documentation;
        private String insertText;
        private String sortText;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public CompletionItemKind getKind() {
            return kind;
        }

        public void setKind(CompletionItemKind kind) {
            this.kind = kind;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getDocumentation() {
            return documentation;
        }

        public void setDocumentation(String documentation) {
            this.documentation = documentation;
        }

        public String getInsertText() {
            return insertText;
        }

        public void setInsertText(String insertText) {
            this.insertText = insertText;
        }

        public String getSortText() {
            return sortText;
        }

        public void setSortText(String sortText) {
            this.sortText = sortText;
        }
    }

    public enum CompletionItemKind {
        TEXT,
        METHOD,
        FUNCTION,
        CONSTRUCTOR,
        FIELD,
        VARIABLE,
        CLASS,
        INTERFACE,
        MODULE,
        PROPERTY,
        KEYWORD,
        SNIPPET,
        ENUM,
        ENUM_MEMBER,
        CONSTANT
    }
}
