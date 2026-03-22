package cn.langlang.iapp.lsp.core.model;

import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.lsp.core.util.SignatureUtils;
import cn.langlang.iapp.lsp.registry.FunctionCategory;
import java.util.List;

public class FunctionInfo {
    private String name;
    private int minParameters;
    private int maxParameters;
    private List<ParamType> paramTypes;
    private List<List<ParamType>> paramTypeLists;
    private FunctionCategory category;
    private String documentation;
    private String insertText;
    private boolean hasOutput;

    public FunctionInfo() {
    }

    public FunctionInfo(String name, int minParameters, int maxParameters,
                        List<ParamType> paramTypes, List<List<ParamType>> paramTypeLists,
                        FunctionCategory category) {
        this.name = name;
        this.minParameters = minParameters;
        this.maxParameters = maxParameters;
        this.paramTypes = paramTypes;
        this.paramTypeLists = paramTypeLists;
        this.category = category;
        this.hasOutput = SignatureUtils.hasOutputParameter(paramTypes);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinParameters() {
        return minParameters;
    }

    public void setMinParameters(int minParameters) {
        this.minParameters = minParameters;
    }

    public int getMaxParameters() {
        return maxParameters;
    }

    public void setMaxParameters(int maxParameters) {
        this.maxParameters = maxParameters;
    }

    public List<ParamType> getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(List<ParamType> paramTypes) {
        this.paramTypes = paramTypes;
        this.hasOutput = SignatureUtils.hasOutputParameter(paramTypes);
    }

    public List<List<ParamType>> getParamTypeLists() {
        return paramTypeLists;
    }

    public void setParamTypeLists(List<List<ParamType>> paramTypeLists) {
        this.paramTypeLists = paramTypeLists;
    }

    public FunctionCategory getCategory() {
        return category;
    }

    public void setCategory(FunctionCategory category) {
        this.category = category;
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

    public boolean hasOutput() {
        return hasOutput;
    }

    public void setHasOutput(boolean hasOutput) {
        this.hasOutput = hasOutput;
    }

    public String getSignature() {
        return SignatureUtils.buildSignature(name, paramTypes);
    }
}
