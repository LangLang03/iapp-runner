package cn.langlang.iapp.lsp.core;

import cn.langlang.iapp.api.FunctionRegistryProvider;
import cn.langlang.iapp.lsp.core.provider.*;
import cn.langlang.iapp.lsp.registry.FunctionCategory;
import cn.langlang.iapp.lsp.registry.ModuleLoader;
import cn.langlang.iapp.runtime.FunctionRegistry;
import cn.langlang.iapp.runtime.IFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LSContext {
    private static final Logger logger = LoggerFactory.getLogger(LSContext.class);
    
    private final FunctionRegistry functionRegistry;
    private final Set<String> loadedModules;
    private final Map<String, FunctionCategory> functionCategoryMap;
    private final List<ModuleLoader> moduleLoaders;
    private boolean yuWebAvailable;
    
    private FunctionProvider functionProvider;
    private VariableProvider variableProvider;
    private CompletionProvider completionProvider;
    private HoverProvider hoverProvider;
    private SignatureProvider signatureProvider;
    private DiagnosticProvider diagnosticProvider;

    public LSContext() {
        this.functionRegistry = new FunctionRegistry();
        this.loadedModules = ConcurrentHashMap.newKeySet();
        this.functionCategoryMap = new ConcurrentHashMap<>();
        this.moduleLoaders = new ArrayList<>();
        this.yuWebAvailable = false;
    }

    public void registerCoreFunctions() {
        registerBuiltinFunctions();
        loadedModules.add("core");
    }

    private void registerBuiltinFunctions() {
        registerFunction(new cn.langlang.iapp.functions.output.SysoFunction(), FunctionCategory.OUTPUT);
        registerFunction(new cn.langlang.iapp.functions.output.TwFunction(), FunctionCategory.OUTPUT);
        registerFunction(new cn.langlang.iapp.functions.string.SsFunction(), FunctionCategory.STRING);
        registerFunction(new cn.langlang.iapp.functions.string.SrFunction(), FunctionCategory.STRING);
        registerFunction(new cn.langlang.iapp.functions.string.SjFunction(), FunctionCategory.STRING);
        registerFunction(new cn.langlang.iapp.functions.string.SlFunction(), FunctionCategory.STRING);
        registerFunction(new cn.langlang.iapp.functions.string.SsgFunction(), FunctionCategory.STRING);
        registerFunction(new cn.langlang.iapp.functions.string.SlgFunction(), FunctionCategory.STRING);
        registerFunction(new cn.langlang.iapp.functions.string.StrimFunction(), FunctionCategory.STRING);
        registerFunction(new cn.langlang.iapp.functions.string.SlowerFunction(), FunctionCategory.STRING);
        registerFunction(new cn.langlang.iapp.functions.string.SupperFunction(), FunctionCategory.STRING);
        registerFunction(new cn.langlang.iapp.functions.string.SiofFunction(), FunctionCategory.STRING);
        registerFunction(new cn.langlang.iapp.functions.string.SlofFunction(), FunctionCategory.STRING);
        registerFunction(new cn.langlang.iapp.functions.string.StobmFunction(), FunctionCategory.STRING);
        registerFunction(new cn.langlang.iapp.functions.string.Sutf8toFunction(), FunctionCategory.STRING);
        registerFunction(new cn.langlang.iapp.functions.math.SAddFunction(), FunctionCategory.MATH);
        registerFunction(new cn.langlang.iapp.functions.math.SSubFunction(), FunctionCategory.MATH);
        registerFunction(new cn.langlang.iapp.functions.math.SMulFunction(), FunctionCategory.MATH);
        registerFunction(new cn.langlang.iapp.functions.math.SDivFunction(), FunctionCategory.MATH);
        registerFunction(new cn.langlang.iapp.functions.math.SModFunction(), FunctionCategory.MATH);
        registerFunction(new cn.langlang.iapp.functions.math.SFunction(), FunctionCategory.MATH);
        registerFunction(new cn.langlang.iapp.functions.math.S2Function(), FunctionCategory.MATH);
        registerFunction(new cn.langlang.iapp.functions.math.SnFunction(), FunctionCategory.MATH);
        registerFunction(new cn.langlang.iapp.functions.math.SranFunction(), FunctionCategory.MATH);
        registerFunction(new cn.langlang.iapp.functions.array.NszFunction(), FunctionCategory.ARRAY);
        registerFunction(new cn.langlang.iapp.functions.array.SgszFunction(), FunctionCategory.ARRAY);
        registerFunction(new cn.langlang.iapp.functions.array.SsszFunction(), FunctionCategory.ARRAY);
        registerFunction(new cn.langlang.iapp.functions.array.SgszlFunction(), FunctionCategory.ARRAY);
        registerFunction(new cn.langlang.iapp.functions.file.FdFunction(), FunctionCategory.FILE);
        registerFunction(new cn.langlang.iapp.functions.file.FeFunction(), FunctionCategory.FILE);
        registerFunction(new cn.langlang.iapp.functions.file.FsFunction(), FunctionCategory.FILE);
        registerFunction(new cn.langlang.iapp.functions.file.FrFunction(), FunctionCategory.FILE);
        registerFunction(new cn.langlang.iapp.functions.file.FwFunction(), FunctionCategory.FILE);
        registerFunction(new cn.langlang.iapp.functions.file.FcFunction(), FunctionCategory.FILE);
        registerFunction(new cn.langlang.iapp.functions.file.FlFunction(), FunctionCategory.FILE);
        registerFunction(new cn.langlang.iapp.functions.file.FtFunction(), FunctionCategory.FILE);
        registerFunction(new cn.langlang.iapp.functions.file.FdirFunction(), FunctionCategory.FILE);
        registerFunction(new cn.langlang.iapp.functions.file.FuzFunction(), FunctionCategory.FILE);
        registerFunction(new cn.langlang.iapp.functions.file.FuzsFunction(), FunctionCategory.FILE);
        registerFunction(new cn.langlang.iapp.functions.file.FjFunction(), FunctionCategory.FILE);
        registerFunction(new cn.langlang.iapp.functions.file.FoFunction(), FunctionCategory.FILE);
        registerFunction(new cn.langlang.iapp.functions.file.FiFunction(), FunctionCategory.FILE);
        registerFunction(new cn.langlang.iapp.functions.net.HsFunction(), FunctionCategory.NET);
        registerFunction(new cn.langlang.iapp.functions.net.HdFunction(), FunctionCategory.NET);
        registerFunction(new cn.langlang.iapp.functions.net.HdflFunction(), FunctionCategory.NET);
        registerFunction(new cn.langlang.iapp.functions.net.HufFunction(), FunctionCategory.NET);
        registerFunction(new cn.langlang.iapp.functions.net.HwFunction(), FunctionCategory.NET);
        registerFunction(new cn.langlang.iapp.functions.net.HwsFunction(), FunctionCategory.NET);
        registerFunction(new cn.langlang.iapp.functions.time.TimeFunction(), FunctionCategory.TIME);
        registerFunction(new cn.langlang.iapp.functions.other.StopFunction(), FunctionCategory.OTHER);
        registerFunction(new cn.langlang.iapp.functions.java.JavaFunction(), FunctionCategory.JAVA);
        registerFunction(new cn.langlang.iapp.functions.java.JavaxFunction(), FunctionCategory.JAVA);
        registerFunction(new cn.langlang.iapp.functions.java.JavanewFunction(), FunctionCategory.JAVA);
        registerFunction(new cn.langlang.iapp.functions.java.JavagsFunction(), FunctionCategory.JAVA);
        registerFunction(new cn.langlang.iapp.functions.java.JavassFunction(), FunctionCategory.JAVA);
        registerFunction(new cn.langlang.iapp.functions.java.ClsFunction(), FunctionCategory.JAVA);
        registerFunction(new cn.langlang.iapp.functions.other.CallFunction(), FunctionCategory.OTHER);
        registerFunction(new cn.langlang.iapp.functions.list.AslistFunction(), FunctionCategory.LIST);
        registerFunction(new cn.langlang.iapp.functions.list.SslistFunction(), FunctionCategory.LIST);
        registerFunction(new cn.langlang.iapp.functions.list.GslistFunction(), FunctionCategory.LIST);
        registerFunction(new cn.langlang.iapp.functions.list.GslistlFunction(), FunctionCategory.LIST);
        registerFunction(new cn.langlang.iapp.functions.list.DslistFunction(), FunctionCategory.LIST);
        registerFunction(new cn.langlang.iapp.functions.list.GslistszFunction(), FunctionCategory.LIST);
        registerFunction(new cn.langlang.iapp.functions.list.GslistisFunction(), FunctionCategory.LIST);
        registerFunction(new cn.langlang.iapp.functions.list.GslistiofFunction(), FunctionCategory.LIST);
        registerFunction(new cn.langlang.iapp.functions.list.GslistlofFunction(), FunctionCategory.LIST);
        registerFunction(new cn.langlang.iapp.functions.clipboard.SxbFunction(), FunctionCategory.CLIPBOARD);
        registerFunction(new cn.langlang.iapp.functions.clipboard.ShbFunction(), FunctionCategory.CLIPBOARD);
    }

    public void registerFunction(IFunction function, FunctionCategory category) {
        if (function == null) {
            return;
        }
        functionRegistry.registerFunction(function);
        functionCategoryMap.put(function.getName().toLowerCase(), category);
    }

    public void registerYuWebFunctions() {
        if (yuWebAvailable) {
            return;
        }
        try {
            ServiceLoader<FunctionRegistryProvider> providers = ServiceLoader.load(FunctionRegistryProvider.class);
            for (FunctionRegistryProvider provider : providers) {
                if (provider.isAvailable()) {
                    provider.registerFunctions(functionRegistry);
                    Map<String, String> categories = provider.getFunctionCategories();
                    if (categories != null) {
                        for (Map.Entry<String, String> entry : categories.entrySet()) {
                            FunctionCategory category = FunctionCategory.fromString(entry.getValue());
                            if (category != null) {
                                functionCategoryMap.put(entry.getKey().toLowerCase(), category);
                            }
                        }
                    }
                    logger.info("Functions registered from provider: {}", provider.getProviderName());
                }
            }
            yuWebAvailable = true;
            loadedModules.add("yuweb");
            logger.info("YuWeb functions registered successfully via SPI");
        } catch (Exception e) {
            logger.warn("Failed to load YuWeb functions: {}", e.getMessage());
            yuWebAvailable = false;
        }
    }

    private void categorizeYuWebFunctions() {
        for (String funcName : functionRegistry.getFunctionNames()) {
            if (!functionCategoryMap.containsKey(funcName)) {
                IFunction func = functionRegistry.getFunction(funcName);
                if (func != null) {
                    FunctionCategory category = FunctionCategory.fromClassName(func.getClass().getName());
                    functionCategoryMap.put(funcName, category);
                }
            }
        }
    }

    public void registerFunction(IFunction function) {
        FunctionCategory category = FunctionCategory.fromClassName(function.getClass().getName());
        registerFunction(function, category);
    }

    public void registerModuleLoader(ModuleLoader loader) {
        if (loader != null) {
            moduleLoaders.add(loader);
        }
    }

    public void loadModules() {
        for (ModuleLoader loader : moduleLoaders) {
            try {
                loader.load(this);
                Map<String, FunctionCategory> categories = loader.getFunctionCategories();
                if (categories != null) {
                    functionCategoryMap.putAll(categories);
                }
                loadedModules.add(loader.getName().toLowerCase());
                logger.info("Module '{}' loaded successfully", loader.getName());
            } catch (Exception e) {
                logger.warn("Failed to load module '{}': {}", loader.getName(), e.getMessage());
            }
        }
    }

    public synchronized FunctionProvider getFunctionProvider() {
        if (functionProvider == null) {
            functionProvider = new FunctionProvider(this);
        }
        return functionProvider;
    }

    public synchronized VariableProvider getVariableProvider() {
        if (variableProvider == null) {
            variableProvider = new VariableProvider(this);
        }
        return variableProvider;
    }

    public synchronized CompletionProvider getCompletionProvider() {
        if (completionProvider == null) {
            completionProvider = new CompletionProvider(this, getFunctionProvider(), getVariableProvider());
        }
        return completionProvider;
    }

    public synchronized HoverProvider getHoverProvider() {
        if (hoverProvider == null) {
            hoverProvider = new HoverProvider(this, getFunctionProvider(), getVariableProvider());
        }
        return hoverProvider;
    }

    public synchronized SignatureProvider getSignatureProvider() {
        if (signatureProvider == null) {
            signatureProvider = new SignatureProvider(this, getFunctionProvider());
        }
        return signatureProvider;
    }

    public synchronized DiagnosticProvider getDiagnosticProvider() {
        if (diagnosticProvider == null) {
            diagnosticProvider = new DiagnosticProvider(this, getVariableProvider());
        }
        return diagnosticProvider;
    }

    public FunctionRegistry getFunctionRegistry() {
        return functionRegistry;
    }

    public IFunction getFunction(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return functionRegistry.getFunction(name);
    }

    public FunctionCategory getFunctionCategory(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return functionCategoryMap.get(name.toLowerCase());
    }

    public void setFunctionCategory(String name, FunctionCategory category) {
        if (name == null || name.isEmpty() || category == null) {
            return;
        }
        functionCategoryMap.put(name.toLowerCase(), category);
    }

    public Collection<IFunction> getAllFunctions() {
        return functionRegistry.getFunctionNames().stream()
                .map(functionRegistry::getFunction)
                .filter(Objects::nonNull)
                .toList();
    }

    public Set<String> getFunctionNames() {
        return functionRegistry.getFunctionNames();
    }

    public Set<String> getLoadedModules() {
        return Collections.unmodifiableSet(loadedModules);
    }

    public boolean isYuWebAvailable() {
        return yuWebAvailable;
    }

    public boolean hasFunction(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return functionRegistry.hasFunction(name);
    }

    public synchronized void refreshProviders() {
        if (functionProvider != null) {
            functionProvider.refreshCache();
        }
    }
}
