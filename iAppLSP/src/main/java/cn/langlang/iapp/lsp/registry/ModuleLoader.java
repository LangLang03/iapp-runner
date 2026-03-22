package cn.langlang.iapp.lsp.registry;

import cn.langlang.iapp.lsp.core.LSContext;
import cn.langlang.iapp.lsp.registry.FunctionCategory;

import java.util.Map;

public interface ModuleLoader {
    
    String getName();
    
    void load(LSContext context);
    
    Map<String, FunctionCategory> getFunctionCategories();
    
    default int getPriority() {
        return 100;
    }
    
    default boolean isAvailable() {
        return true;
    }
}
