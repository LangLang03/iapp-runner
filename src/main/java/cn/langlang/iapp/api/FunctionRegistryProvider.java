package cn.langlang.iapp.api;

import cn.langlang.iapp.runtime.FunctionRegistry;

import java.util.Map;

public interface FunctionRegistryProvider {
    
    void registerFunctions(FunctionRegistry registry);
    
    Map<String, String> getFunctionCategories();
    
    default String getProviderName() {
        return this.getClass().getSimpleName();
    }
    
    default int getPriority() {
        return 100;
    }
    
    default boolean isAvailable() {
        return true;
    }
}
