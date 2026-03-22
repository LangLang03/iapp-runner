package cn.langlang.iapp.lsp.registry;

import cn.langlang.iapp.lsp.core.LSContext;
import cn.langlang.iapp.runtime.FunctionRegistry;
import cn.langlang.iapp.runtime.IFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

public class ModuleRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ModuleRegistry.class);
    
    private final LSContext context;
    private final Map<String, ModuleLoader> loaders;

    public ModuleRegistry(LSContext context) {
        this.context = context;
        this.loaders = new HashMap<>();
        discoverModuleLoaders();
    }

    private void discoverModuleLoaders() {
        try {
            ServiceLoader<ModuleLoader> serviceLoader = ServiceLoader.load(ModuleLoader.class);
            for (ModuleLoader loader : serviceLoader) {
                registerLoader(loader);
            }
        } catch (Exception e) {
            logger.debug("No module loaders found via SPI: {}", e.getMessage());
        }
    }

    public void registerLoader(ModuleLoader loader) {
        if (loader == null) {
            return;
        }
        
        String name = loader.getName().toLowerCase();
        if (!loaders.containsKey(name) || loader.getPriority() < loaders.get(name).getPriority()) {
            loaders.put(name, loader);
            context.registerModuleLoader(loader);
            logger.debug("Registered module loader: {} (priority: {})", loader.getName(), loader.getPriority());
        }
    }

    public void registerModule(String moduleName) {
        if (moduleName == null || moduleName.isEmpty()) {
            return;
        }
        
        String normalizedName = moduleName.toLowerCase();
        
        ModuleLoader loader = loaders.get(normalizedName);
        if (loader != null) {
            if (loader.isAvailable()) {
                loader.load(context);
                Map<String, FunctionCategory> categories = loader.getFunctionCategories();
                if (categories != null) {
                    for (Map.Entry<String, FunctionCategory> entry : categories.entrySet()) {
                        context.setFunctionCategory(entry.getKey(), entry.getValue());
                    }
                }
                logger.info("Module '{}' loaded via ModuleLoader", moduleName);
            } else {
                logger.warn("Module '{}' is not available", moduleName);
            }
            return;
        }
        
        switch (normalizedName) {
            case "core":
                context.registerCoreFunctions();
                break;
            case "yuweb":
                context.registerYuWebFunctions();
                break;
            default:
                throw new IllegalArgumentException("Unknown module: " + moduleName);
        }
    }

    public void registerAllAvailable() {
        for (ModuleLoader loader : loaders.values()) {
            if (loader.isAvailable()) {
                try {
                    loader.load(context);
                    logger.info("Module '{}' loaded automatically", loader.getName());
                } catch (Exception e) {
                    logger.warn("Failed to load module '{}': {}", loader.getName(), e.getMessage());
                }
            }
        }
        
        if (!context.getLoadedModules().contains("core")) {
            context.registerCoreFunctions();
        }
        
        context.registerYuWebFunctions();
    }

    public void autoDiscover() {
        context.registerCoreFunctions();
        
        try {
            Class.forName("cn.langlang.yuweb.functions.SharedFunctionRegistry");
            context.registerYuWebFunctions();
        } catch (ClassNotFoundException e) {
            logger.debug("YuWeb module not available in classpath");
        }
    }

    public Set<String> getLoadedModules() {
        return context.getLoadedModules();
    }

    public boolean isModuleLoaded(String moduleName) {
        if (moduleName == null || moduleName.isEmpty()) {
            return false;
        }
        return context.getLoadedModules().contains(moduleName.toLowerCase());
    }

    public boolean hasLoader(String moduleName) {
        if (moduleName == null || moduleName.isEmpty()) {
            return false;
        }
        return loaders.containsKey(moduleName.toLowerCase());
    }

    public static void registerFunctionsFromRegistry(FunctionRegistry targetRegistry, 
                                                      FunctionRegistry sourceRegistry) {
        if (targetRegistry == null || sourceRegistry == null) {
            return;
        }
        
        for (String funcName : sourceRegistry.getFunctionNames()) {
            IFunction func = sourceRegistry.getFunction(funcName);
            if (func != null) {
                targetRegistry.registerFunction(func);
            }
        }
    }

    public static void registerYuWebFunctionsSafely(FunctionRegistry registry) {
        if (registry == null) {
            return;
        }
        
        try {
            Class<?> sharedRegistryClass = Class.forName("cn.langlang.yuweb.functions.SharedFunctionRegistry");
            Method method = sharedRegistryClass.getMethod("registerBuiltinFunctions", FunctionRegistry.class);
            method.invoke(null, registry);
        } catch (ClassNotFoundException e) {
            logger.debug("YuWeb not available");
        } catch (Exception e) {
            logger.warn("Error loading YuWeb functions: {}", e.getMessage());
        }
    }
}
