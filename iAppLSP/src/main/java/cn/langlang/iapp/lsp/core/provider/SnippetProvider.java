package cn.langlang.iapp.lsp.core.provider;

import cn.langlang.iapp.lsp.header.HeaderFile;
import cn.langlang.iapp.lsp.header.HeaderLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SnippetProvider {
    private final HeaderLoader headerLoader;
    private final Map<String, HeaderFile.SnippetInfo> builtInSnippets;
    
    public SnippetProvider() {
        this.headerLoader = null;
        this.builtInSnippets = new LinkedHashMap<>();
        initBuiltInSnippets();
    }
    
    public SnippetProvider(HeaderLoader headerLoader) {
        this.headerLoader = headerLoader;
        this.builtInSnippets = new LinkedHashMap<>();
        initBuiltInSnippets();
    }
    
    private void initBuiltInSnippets() {
        addBuiltInSnippet("fn", "fn", 
            "fn ${1:functionName}(${2:params}) {\n\t${3:// code}\n}",
            "函数定义");
        
        addBuiltInSnippet("f", "f",
            "f (${1:condition}) {\n\t${2:// code}\n}",
            "条件语句");
        
        addBuiltInSnippet("f-else", "f",
            "f (${1:condition}) {\n\t${2:// code}\n} else {\n\t${3:// code}\n}",
            "条件语句");
        
        addBuiltInSnippet("w", "w",
            "w (${1:condition}) {\n\t${2:// code}\n}",
            "循环语句");
        
        addBuiltInSnippet("for", "for",
            "for (${1:i}=0; ${1:i}<${2:n}; ${1:i}++) {\n\t${3:// code}\n}",
            "for循环");
        
        addBuiltInSnippet("for-each", "for",
            "for (${1:item} in ${2:list}) {\n\t${3:// code}\n}",
            "for-each循环");
        
        addBuiltInSnippet("t", "t",
            "t {\n\t${1:// code}\n}",
            "线程执行");
        
        addBuiltInSnippet("s", "s",
            "s ${1:varName} = ${2:value}",
            "局部变量声明");
        
        addBuiltInSnippet("ss", "ss",
            "ss ${1:varName} = ${2:value}",
            "界面变量声明");
        
        addBuiltInSnippet("sss", "sss",
            "sss ${1:varName} = ${2:value}",
            "全局变量声明");
        
        addBuiltInSnippet("syso", "syso",
            "syso(${1:value})",
            "控制台输出");
        
        addBuiltInSnippet("tw", "tw",
            "tw(${1:message})",
            "提示消息");
        
    }
    
    private void addBuiltInSnippet(String key, String prefix, String body, String description) {
        HeaderFile.SnippetInfo snippet = new HeaderFile.SnippetInfo();
        snippet.setLabel(key);
        snippet.setPrefix(prefix);
        snippet.setBody(body);
        snippet.setDescription(description);
        builtInSnippets.put(key, snippet);
    }
    
    public List<HeaderFile.SnippetInfo> getAllSnippets() {
        List<HeaderFile.SnippetInfo> result = new ArrayList<>(builtInSnippets.values());
        
        if (headerLoader != null) {
            result.addAll(headerLoader.getAllSnippets());
        }
        
        return result;
    }
    
    public List<HeaderFile.SnippetInfo> getSnippetsByPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return getAllSnippets();
        }
        
        String lowerPrefix = prefix.toLowerCase();
        List<HeaderFile.SnippetInfo> result = new ArrayList<>();
        
        for (HeaderFile.SnippetInfo snippet : builtInSnippets.values()) {
            if (snippet.getPrefix() != null && snippet.getPrefix().toLowerCase().startsWith(lowerPrefix)) {
                result.add(snippet);
            }
        }
        
        if (headerLoader != null) {
            for (HeaderFile.SnippetInfo snippet : headerLoader.getSnippetsByPrefix(prefix)) {
                if (!result.contains(snippet)) {
                    result.add(snippet);
                }
            }
        }
        
        return result;
    }
    
    public HeaderFile.SnippetInfo getSnippetByKey(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        
        HeaderFile.SnippetInfo snippet = builtInSnippets.get(key);
        
        if (snippet == null && headerLoader != null) {
            for (HeaderFile.SnippetInfo s : headerLoader.getAllSnippets()) {
                if (key.equals(s.getLabel()) || key.equals(s.getPrefix())) {
                    return s;
                }
            }
        }
        
        return snippet;
    }
    
    public CompletionItem toCompletionItem(HeaderFile.SnippetInfo snippet) {
        CompletionItem item = new CompletionItem();
        item.setLabel(snippet.getLabel());
        item.setKind(CompletionItemKind.SNIPPET);
        item.setDetail(snippet.getDescription());
        item.setDocumentation(snippet.getDocumentation());
        item.setInsertText(snippet.getBody());
        item.setSortText("0" + snippet.getPrefix());
        return item;
    }
    
    public List<CompletionItem> getSnippetCompletions(String prefix) {
        List<CompletionItem> items = new ArrayList<>();
        
        for (HeaderFile.SnippetInfo snippet : getSnippetsByPrefix(prefix)) {
            items.add(toCompletionItem(snippet));
        }
        
        return items;
    }
}
