package cn.langlang.iapp.lsp.header;

import java.util.ArrayList;
import java.util.List;

public class HeaderFunctionInfo {
    private String name;
    private String category;
    private List<ParamInfo> params;
    private String returnType;
    private String description;
    private String example;
    private boolean yuWeb;

    public HeaderFunctionInfo() {
        this.params = new ArrayList<>();
        this.yuWeb = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<ParamInfo> getParams() {
        return params;
    }

    public void setParams(List<ParamInfo> params) {
        this.params = params != null ? params : new ArrayList<>();
    }

    public void addParam(ParamInfo param) {
        if (this.params == null) {
            this.params = new ArrayList<>();
        }
        this.params.add(param);
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public boolean isYuWeb() {
        return yuWeb;
    }

    public void setYuWeb(boolean yuWeb) {
        this.yuWeb = yuWeb;
    }

    public String getSignature() {
        StringBuilder sb = new StringBuilder(name);
        sb.append("(");
        if (params != null && !params.isEmpty()) {
            List<String> paramNames = new ArrayList<>();
            for (ParamInfo param : params) {
                paramNames.add(param.getName());
            }
            sb.append(String.join(", ", paramNames));
        }
        sb.append(")");
        return sb.toString();
    }

    public String getInsertText() {
        StringBuilder sb = new StringBuilder(name);
        sb.append("(");
        if (params != null && !params.isEmpty()) {
            List<String> paramPlaceholders = new ArrayList<>();
            int index = 1;
            for (ParamInfo param : params) {
                paramPlaceholders.add("${" + index + ":" + param.getName() + "}");
                index++;
            }
            sb.append(String.join(", ", paramPlaceholders));
        }
        sb.append(")$0");
        return sb.toString();
    }

    public String getFullDocumentation() {
        StringBuilder sb = new StringBuilder();
        
        if (category != null && !category.isEmpty()) {
            sb.append("**类别:** ").append(category).append("\n");
        }
        
        if (description != null && !description.isEmpty()) {
            sb.append("\n").append(description).append("\n");
        }
        
        if (params != null && !params.isEmpty()) {
            sb.append("\n**参数:**\n");
            for (ParamInfo param : params) {
                sb.append("- `").append(param.getName()).append("`");
                if (param.getType() != null && !param.getType().isEmpty()) {
                    sb.append(" `").append(param.getType()).append("`");
                }
                if (param.getDescription() != null && !param.getDescription().isEmpty()) {
                    sb.append(" — ").append(param.getDescription());
                }
                sb.append("\n");
            }
        }
        
        if (returnType != null && !returnType.isEmpty()) {
            sb.append("\n**返回:** ").append(returnType).append("\n");
        }
        
        if (example != null && !example.isEmpty()) {
            sb.append("\n**示例:**\n```iapp\n").append(example).append("\n```\n");
        }
        
        if (yuWeb) {
            sb.append("\n*需要 YuWeb 模块支持*");
        }
        
        return sb.toString();
    }

    public static class ParamInfo {
        private String name;
        private String type;
        private String description;
        private boolean optional;

        public ParamInfo() {
            this.optional = false;
        }

        public ParamInfo(String name) {
            this.name = name;
            this.optional = false;
        }

        public ParamInfo(String name, String type, String description) {
            this.name = name;
            this.type = type;
            this.description = description;
            this.optional = false;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isOptional() {
            return optional;
        }

        public void setOptional(boolean optional) {
            this.optional = optional;
        }
    }
}
