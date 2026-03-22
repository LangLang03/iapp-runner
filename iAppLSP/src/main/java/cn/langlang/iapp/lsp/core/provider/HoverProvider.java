package cn.langlang.iapp.lsp.core.provider;

import cn.langlang.iapp.lsp.core.LSContext;
import cn.langlang.iapp.lsp.core.model.FunctionInfo;
import cn.langlang.iapp.lsp.core.model.VariableInfo;
import cn.langlang.iapp.lsp.core.util.ScopeUtils;
import cn.langlang.iapp.lsp.header.HeaderFunctionInfo;
import cn.langlang.iapp.runtime.IFunction;

public class HoverProvider {
    private final LSContext context;
    private final FunctionProvider functionProvider;
    private final VariableProvider variableProvider;

    public HoverProvider(LSContext context) {
        this.context = context;
        this.functionProvider = new FunctionProvider(context);
        this.variableProvider = new VariableProvider(context);
    }

    public HoverProvider(LSContext context, FunctionProvider functionProvider, VariableProvider variableProvider) {
        this.context = context;
        this.functionProvider = functionProvider != null ? functionProvider : new FunctionProvider(context);
        this.variableProvider = variableProvider != null ? variableProvider : new VariableProvider(context);
    }

    public String getHoverText(String word) {
        if (word == null || word.isEmpty()) {
            return null;
        }
        
        HeaderFunctionInfo headerInfo = context.getHeaderFunctionInfo(word);
        if (headerInfo != null) {
            return getHeaderFunctionHoverText(headerInfo);
        }
        
        IFunction function = context.getFunction(word);
        if (function != null) {
            return getFunctionHoverText(function);
        }
        
        VariableInfo variable = variableProvider.getVariable(word);
        if (variable != null) {
            return getVariableHoverText(variable);
        }
        
        return null;
    }
    
    public String getHeaderFunctionHoverText(HeaderFunctionInfo headerInfo) {
        if (headerInfo == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("```iapp\n");
        sb.append(headerInfo.getSignature());
        sb.append("\n```\n\n");
        
        sb.append("**").append(headerInfo.getName()).append("**\n\n");
        
        if (headerInfo.getCategory() != null && !headerInfo.getCategory().isEmpty()) {
            sb.append("类别: ").append(headerInfo.getCategory()).append("\n\n");
        }
        
        if (headerInfo.getDescription() != null && !headerInfo.getDescription().isEmpty()) {
            sb.append(headerInfo.getDescription()).append("\n\n");
        }
        
        if (headerInfo.getParams() != null && !headerInfo.getParams().isEmpty()) {
            sb.append("**参数:**\n");
            for (HeaderFunctionInfo.ParamInfo param : headerInfo.getParams()) {
                sb.append("- `").append(param.getName()).append("`");
                if (param.getType() != null && !param.getType().isEmpty()) {
                    sb.append(" (").append(param.getType()).append(")");
                }
                if (param.getDescription() != null && !param.getDescription().isEmpty()) {
                    sb.append(" - ").append(param.getDescription());
                }
                sb.append("\n");
            }
            sb.append("\n");
        }
        
        if (headerInfo.getReturnType() != null && !headerInfo.getReturnType().isEmpty()) {
            sb.append("**返回:** ").append(headerInfo.getReturnType()).append("\n\n");
        }
        
        if (headerInfo.getExample() != null && !headerInfo.getExample().isEmpty()) {
            sb.append("**示例:**\n```\n").append(headerInfo.getExample()).append("\n```\n");
        }
        
        if (headerInfo.isYuWeb()) {
            sb.append("\n*需要 YuWeb 模块支持*");
        }
        
        return sb.toString();
    }

    public String getFunctionHoverText(IFunction function) {
        if (function == null) {
            return null;
        }
        
        HeaderFunctionInfo headerInfo = context.getHeaderFunctionInfo(function.getName());
        if (headerInfo != null) {
            return getHeaderFunctionHoverText(headerInfo);
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("```iapp\n");
        sb.append(functionProvider.generateSignature(function));
        sb.append("\n```\n\n");
        
        sb.append("**").append(function.getName()).append("**\n\n");
        
        FunctionInfo info = functionProvider.getFunction(function.getName());
        if (info != null && info.getCategory() != null) {
            sb.append("类别: ").append(info.getCategory().getDisplayName()).append("\n\n");
        }
        
        sb.append("参数数量: ");
        if (function.getMinParameters() == function.getMaxParameters()) {
            sb.append(function.getMinParameters());
        } else if (function.getMaxParameters() == Integer.MAX_VALUE) {
            sb.append(function.getMinParameters()).append("+");
        } else {
            sb.append(function.getMinParameters()).append(" - ").append(function.getMaxParameters());
        }
        sb.append("\n");
        
        if (!function.isSupported()) {
            sb.append("\n⚠️ ").append(function.getUnsupportedReason());
        }
        
        return sb.toString();
    }

    public String getVariableHoverText(VariableInfo variable) {
        if (variable == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("```iapp\n");
        sb.append(variable.getDisplayName());
        sb.append("\n```\n\n");
        
        sb.append("**变量**\n\n");
        sb.append("作用域: ").append(ScopeUtils.getScopeDisplayName(variable.getScope())).append("\n");
        
        if (variable.getInferredType() != null) {
            sb.append("类型: ").append(variable.getInferredType()).append("\n");
        }
        
        sb.append("定义位置: 第 ").append(variable.getLine()).append(" 行\n");
        
        return sb.toString();
    }

    public FunctionProvider getFunctionProvider() {
        return functionProvider;
    }

    public VariableProvider getVariableProvider() {
        return variableProvider;
    }
}
