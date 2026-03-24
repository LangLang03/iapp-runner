package cn.langlang.iapp.lsp.core.provider;

import cn.langlang.iapp.lsp.core.LSContext;
import cn.langlang.iapp.lsp.core.model.FunctionInfo;
import cn.langlang.iapp.lsp.core.util.SignatureUtils;
import cn.langlang.iapp.lsp.header.HeaderFunctionInfo;
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
        buildCacheIfNeeded();
        return functionInfoCache.values().stream()
                .filter(f -> !isYuWebFunction(f.getName()))
                .collect(Collectors.toList());
    }

    public List<FunctionInfo> getYuWebFunctions() {
        buildCacheIfNeeded();
        return functionInfoCache.values().stream()
                .filter(f -> isYuWebFunction(f.getName()))
                .collect(Collectors.toList());
    }
    
    private boolean isYuWebFunction(String name) {
        HeaderFunctionInfo headerInfo = context.getHeaderFunctionInfo(name);
        if (headerInfo != null) {
            return headerInfo.isYuWeb();
        }
        FunctionCategory category = context.getFunctionCategory(name);
        return category != null && category.isYuWebCategory();
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
        
        for (HeaderFunctionInfo headerFunc : context.getHeaderLoader().getAllFunctions()) {
            String funcName = headerFunc.getName();
            if (funcName != null) {
                String key = funcName.toLowerCase();
                if (functionInfoCache.containsKey(key)) {
                    FunctionInfo existing = functionInfoCache.get(key);
                    mergeHeaderInfo(existing, headerFunc);
                } else {
                    FunctionInfo info = createFunctionInfoFromHeader(headerFunc);
                    functionInfoCache.put(key, info);
                }
            }
        }
    }
    
    private void mergeHeaderInfo(FunctionInfo info, HeaderFunctionInfo headerInfo) {
        if (headerInfo.getDescription() != null && !headerInfo.getDescription().isEmpty()) {
            info.setDocumentation(headerInfo.getFullDocumentation());
        }
        if (headerInfo.getInsertText() != null && !headerInfo.getInsertText().isEmpty()) {
            info.setInsertText(headerInfo.getInsertText());
        }
    }
    
    private FunctionInfo createFunctionInfoFromHeader(HeaderFunctionInfo headerInfo) {
        FunctionInfo info = new FunctionInfo();
        info.setName(headerInfo.getName());
        
        List<ParamType> paramTypes = convertParamTypes(headerInfo.getParams());
        info.setParamTypes(paramTypes);
        info.setMinParameters(paramTypes.size());
        info.setMaxParameters(paramTypes.size());
        
        String categoryStr = headerInfo.getCategory();
        if (categoryStr != null) {
            FunctionCategory category = FunctionCategory.fromString(categoryStr);
            if (category != null) {
                info.setCategory(category);
            }
        }
        if (headerInfo.isYuWeb() && info.getCategory() == null) {
            info.setCategory(FunctionCategory.WEB_REQUEST);
        }
        
        info.setDocumentation(headerInfo.getFullDocumentation());
        info.setInsertText(headerInfo.getInsertText());
        
        return info;
    }
    
    private List<ParamType> convertParamTypes(List<HeaderFunctionInfo.ParamInfo> params) {
        List<ParamType> result = new ArrayList<>();
        if (params != null) {
            for (HeaderFunctionInfo.ParamInfo param : params) {
                ParamType type = convertToParamType(param.getType());
                result.add(type);
            }
        }
        return result;
    }
    
    private ParamType convertToParamType(String typeName) {
        if (typeName == null || typeName.isEmpty()) {
            return ParamType.OBJECT;
        }
        
        switch (typeName.toLowerCase()) {
            case "string":
            case "str":
                return ParamType.STRING;
            case "int":
            case "integer":
                return ParamType.INT;
            case "long":
                return ParamType.LONG;
            case "double":
            case "float":
            case "number":
                return ParamType.DOUBLE;
            case "boolean":
            case "bool":
                return ParamType.BOOLEAN;
            case "array":
            case "list":
                return ParamType.ARRAY;
            case "output":
            case "out":
                return ParamType.OUTPUT;
            default:
                return ParamType.OBJECT;
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
        
        HeaderFunctionInfo headerInfo = context.getHeaderFunctionInfo(func.getName());
        if (headerInfo != null) {
            info.setDocumentation(headerInfo.getFullDocumentation());
            info.setInsertText(headerInfo.getInsertText());
        } else {
            info.setDocumentation(generateDocumentation(func));
            info.setInsertText(SignatureUtils.buildInsertText(func.getName(), func.getParamTypes(), func.getMinParameters()));
        }
        
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
