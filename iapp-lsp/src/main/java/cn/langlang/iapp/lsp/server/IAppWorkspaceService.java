package cn.langlang.iapp.lsp.server;

import cn.langlang.iapp.lsp.core.LSContext;
import cn.langlang.iapp.lsp.core.model.FunctionInfo;
import cn.langlang.iapp.lsp.core.provider.FunctionProvider;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class IAppWorkspaceService implements WorkspaceService {
    private final LSContext context;
    private LanguageClient client;
    private final FunctionProvider functionProvider;

    public IAppWorkspaceService(LSContext context) {
        this.context = context;
        this.functionProvider = new FunctionProvider(context);
    }

    public void connect(LanguageClient client) {
        this.client = client;
    }

    @Override
    public CompletableFuture<Either<List<? extends SymbolInformation>, List<? extends WorkspaceSymbol>>> symbol(WorkspaceSymbolParams params) {
        String query = params.getQuery();
        List<SymbolInformation> symbols = new ArrayList<>();
        
        if (query == null || query.isEmpty()) {
            return CompletableFuture.completedFuture(Either.forLeft(symbols));
        }
        
        List<FunctionInfo> functions = functionProvider.getFunctionsByPrefix(query);
        for (FunctionInfo func : functions) {
            SymbolInformation symbol = new SymbolInformation();
            symbol.setName(func.getName());
            symbol.setKind(SymbolKind.Function);
            symbol.setContainerName(func.getCategory().getDisplayName());
            symbols.add(symbol);
        }
        
        return CompletableFuture.completedFuture(Either.forLeft(symbols));
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {
        // Handle configuration changes if needed
    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        // Handle file changes if needed
    }

    @Override
    public CompletableFuture<Object> executeCommand(ExecuteCommandParams params) {
        String command = params.getCommand();
        
        switch (command) {
            case "iapp.listFunctions":
                return listFunctions();
            case "iapp.listCategories":
                return listCategories();
            case "iapp.reloadModules":
                return reloadModules();
            default:
                return CompletableFuture.completedFuture(null);
        }
    }

    private CompletableFuture<Object> listFunctions() {
        List<FunctionInfo> functions = functionProvider.getAllFunctions();
        List<Map<String, Object>> result = functions.stream()
            .map(f -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("name", f.getName());
                map.put("category", f.getCategory().getDisplayName());
                map.put("minParams", f.getMinParameters());
                map.put("maxParams", f.getMaxParameters());
                map.put("signature", f.getSignature());
                return map;
            })
            .collect(Collectors.toList());
        
        return CompletableFuture.completedFuture(result);
    }

    private CompletableFuture<Object> listCategories() {
        List<Map<String, Object>> result = Arrays.stream(cn.langlang.iapp.lsp.registry.FunctionCategory.values())
            .map(c -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("name", c.name());
                map.put("displayName", c.getDisplayName());
                map.put("isYuWeb", c.isYuWebCategory());
                return map;
            })
            .collect(Collectors.toList());
        
        return CompletableFuture.completedFuture(result);
    }

    private CompletableFuture<Object> reloadModules() {
        cn.langlang.iapp.lsp.registry.ModuleRegistry registry = 
            new cn.langlang.iapp.lsp.registry.ModuleRegistry(context);
        registry.registerAllAvailable();
        functionProvider.refreshCache();
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("loadedModules", context.getLoadedModules());
        result.put("yuWebAvailable", context.isYuWebAvailable());
        
        return CompletableFuture.completedFuture(result);
    }
}
