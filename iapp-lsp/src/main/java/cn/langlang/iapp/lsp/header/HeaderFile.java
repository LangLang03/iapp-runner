package cn.langlang.iapp.lsp.header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeaderFile {
    private String filePath;
    private String fileName;
    private String category;
    private boolean yuWeb;
    private List<HeaderFunctionInfo> functions;
    private List<SnippetInfo> snippets;
    private Map<String, HeaderFunctionInfo> functionMap;

    public HeaderFile() {
        this.functions = new ArrayList<>();
        this.snippets = new ArrayList<>();
        this.functionMap = new HashMap<>();
        this.yuWeb = false;
    }

    public HeaderFile(String filePath) {
        this();
        this.filePath = filePath;
        if (filePath != null) {
            int lastSep = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
            this.fileName = lastSep >= 0 ? filePath.substring(lastSep + 1) : filePath;
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        if (filePath != null) {
            int lastSep = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
            this.fileName = lastSep >= 0 ? filePath.substring(lastSep + 1) : filePath;
        }
    }

    public String getFileName() {
        return fileName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isYuWeb() {
        return yuWeb;
    }

    public void setYuWeb(boolean yuWeb) {
        this.yuWeb = yuWeb;
    }

    public List<HeaderFunctionInfo> getFunctions() {
        return functions;
    }

    public void setFunctions(List<HeaderFunctionInfo> functions) {
        this.functions = functions != null ? functions : new ArrayList<>();
        rebuildFunctionMap();
    }

    public void addFunction(HeaderFunctionInfo function) {
        if (function != null) {
            this.functions.add(function);
            if (function.getName() != null) {
                this.functionMap.put(function.getName().toLowerCase(), function);
            }
        }
    }

    public HeaderFunctionInfo getFunction(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return functionMap.get(name.toLowerCase());
    }

    public boolean hasFunction(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return functionMap.containsKey(name.toLowerCase());
    }

    private void rebuildFunctionMap() {
        this.functionMap.clear();
        for (HeaderFunctionInfo func : this.functions) {
            if (func.getName() != null) {
                this.functionMap.put(func.getName().toLowerCase(), func);
            }
        }
    }

    public List<SnippetInfo> getSnippets() {
        return snippets;
    }

    public void setSnippets(List<SnippetInfo> snippets) {
        this.snippets = snippets != null ? snippets : new ArrayList<>();
    }

    public void addSnippet(SnippetInfo snippet) {
        if (snippet != null) {
            this.snippets.add(snippet);
        }
    }

    public int getFunctionCount() {
        return functions.size();
    }

    public int getSnippetCount() {
        return snippets.size();
    }

    public static class SnippetInfo {
        private String label;
        private String prefix;
        private String body;
        private String description;
        private String documentation;

        public SnippetInfo() {
        }

        public SnippetInfo(String label, String prefix, String body, String description) {
            this.label = label;
            this.prefix = prefix;
            this.body = body;
            this.description = description;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDocumentation() {
            return documentation;
        }

        public void setDocumentation(String documentation) {
            this.documentation = documentation;
        }
    }
}
