package cn.langlang.iapp.lsp.core.provider;

import cn.langlang.iapp.lsp.core.LSContext;
import cn.langlang.iapp.lsp.core.model.FunctionInfo;
import cn.langlang.iapp.lsp.core.util.SignatureUtils;
import cn.langlang.iapp.lsp.registry.FunctionCategory;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.ParamType;

import java.util.*;
import java.util.stream.Collectors;

public class FunctionProvider {
    private final LSContext context;
    private Map<String, FunctionInfo> functionInfoCache;

    public FunctionProvider(LSContext context) {
        this.context = context;
        this.functionInfoCache = new HashMap<>();
    }

    public List<FunctionInfo> getAllFunctions() {
        buildCacheIfNeeded();
        return new ArrayList<>(functionInfoCache.values());
    }

    public FunctionInfo getFunction(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        buildCacheIfNeeded();
        return functionInfoCache.get(name.toLowerCase());
    }

    public List<FunctionInfo> getFunctionsByCategory(FunctionCategory category) {
        if (category == null) {
            return Collections.emptyList();
        }
        buildCacheIfNeeded();
        return functionInfoCache.values().stream()
                .filter(f -> f.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<FunctionInfo> getFunctionsByPrefix(String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        buildCacheIfNeeded();
        String lowerPrefix = prefix.toLowerCase();
        return functionInfoCache.values().stream()
                .filter(f -> f.getName().toLowerCase().startsWith(lowerPrefix))
                .collect(Collectors.toList());
    }

    public List<FunctionInfo> getCoreFunctions() {
        return getAllFunctions().stream()
                .filter(f -> !f.getCategory().isYuWebCategory())
                .collect(Collectors.toList());
    }

    public List<FunctionInfo> getYuWebFunctions() {
        if (!context.isYuWebAvailable()) {
            return Collections.emptyList();
        }
        return getAllFunctions().stream()
                .filter(f -> f.getCategory().isYuWebCategory())
                .collect(Collectors.toList());
    }

    private synchronized void buildCacheIfNeeded() {
        if (!functionInfoCache.isEmpty()) {
            return;
        }
        
        for (String name : context.getFunctionNames()) {
            IFunction func = context.getFunction(name);
            if (func != null) {
                FunctionInfo info = createFunctionInfo(func);
                functionInfoCache.put(name.toLowerCase(), info);
            }
        }
    }

    private FunctionInfo createFunctionInfo(IFunction func) {
        FunctionInfo info = new FunctionInfo();
        info.setName(func.getName());
        info.setMinParameters(func.getMinParameters());
        info.setMaxParameters(func.getMaxParameters());
        info.setParamTypes(func.getParamTypes());
        info.setParamTypeLists(func.getParamTypeLists());
        info.setCategory(context.getFunctionCategory(func.getName()));
        info.setDocumentation(generateDocumentation(func));
        info.setInsertText(SignatureUtils.buildInsertText(func.getName(), func.getParamTypes(), func.getMinParameters()));
        return info;
    }

    public String generateDocumentation(IFunction func) {
        if (func == null) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("**").append(func.getName()).append("**\n\n");
        
        FunctionCategory category = context.getFunctionCategory(func.getName());
        if (category != null) {
            sb.append("类别: ").append(category.getDisplayName()).append("\n\n");
        }
        
        sb.append("参数数量: ");
        if (func.getMinParameters() == func.getMaxParameters()) {
            sb.append(func.getMinParameters());
        } else if (func.getMaxParameters() == Integer.MAX_VALUE) {
            sb.append(func.getMinParameters()).append("+");
        } else {
            sb.append(func.getMinParameters()).append("-").append(func.getMaxParameters());
        }
        sb.append("\n\n");
        
        List<ParamType> paramTypes = func.getParamTypes();
        if (paramTypes != null && !paramTypes.isEmpty()) {
            sb.append("参数类型: ");
            List<String> typeNames = paramTypes.stream()
                    .map(type -> type.name() + (type == ParamType.OUTPUT ? " (输出)" : ""))
                    .collect(Collectors.toList());
            sb.append(String.join(", ", typeNames));
            sb.append("\n");
        }
        
        return sb.toString();
    }

    public String generateSignature(IFunction func) {
        if (func == null) {
            return "";
        }
        return SignatureUtils.buildSignature(func.getName(), func.getParamTypes());
    }

    public List<String> getSignatures(IFunction func) {
        if (func == null) {
            return Collections.emptyList();
        }
        
        List<String> signatures = new ArrayList<>();
        List<List<ParamType>> paramTypeLists = func.getParamTypeLists();
        
        if (paramTypeLists != null && !paramTypeLists.isEmpty()) {
            for (List<ParamType> types : paramTypeLists) {
                signatures.add(SignatureUtils.buildSignature(func.getName(), types));
            }
        } else {
            signatures.add(generateSignature(func));
        }
        
        return signatures;
    }

    public synchronized void refreshCache() {
        functionInfoCache.clear();
    }
}
