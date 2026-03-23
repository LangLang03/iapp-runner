package cn.langlang.iapp.lsp.server;

import cn.langlang.iapp.lsp.core.LSContext;
import cn.langlang.iapp.lsp.registry.ModuleRegistry;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class IAppLanguageServer implements LanguageServer, LanguageClientAware {
    private static final Logger logger = LoggerFactory.getLogger(IAppLanguageServer.class);
    
    private IAppTextDocumentService textDocumentService;
    private IAppWorkspaceService workspaceService;
    private LanguageClient client;
    private LSContext context;
    private int shutdownRequested = 0;

    public IAppLanguageServer() {
        this.context = new LSContext();
        
        ModuleRegistry moduleRegistry = new ModuleRegistry(context);
        moduleRegistry.autoDiscover();
        
        loadDefaultHeaders();
        
        this.textDocumentService = new IAppTextDocumentService(context);
        this.workspaceService = new IAppWorkspaceService(context);
    }

    public IAppLanguageServer(boolean loadYuWeb) {
        this.context = new LSContext();
        
        ModuleRegistry moduleRegistry = new ModuleRegistry(context);
        moduleRegistry.registerModule("core");
        if (loadYuWeb) {
            moduleRegistry.registerModule("yuweb");
        }
        
        loadDefaultHeaders();
        
        this.textDocumentService = new IAppTextDocumentService(context);
        this.workspaceService = new IAppWorkspaceService(context);
    }
    
    private void loadDefaultHeaders() {
        context.loadHeaderFilesFromClasspath("headers");
        logger.info("Default headers loaded: {} functions, {} snippets", 
            context.getHeaderLoader().getFunctionCount(),
            context.getHeaderLoader().getSnippetCount());
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        ServerCapabilities capabilities = new ServerCapabilities();
        
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);
        
        CompletionOptions completionOptions = new CompletionOptions();
        completionOptions.setTriggerCharacters(java.util.Arrays.asList(".", "("));
        capabilities.setCompletionProvider(completionOptions);
        
        capabilities.setHoverProvider(true);
        
        SignatureHelpOptions signatureHelpOptions = new SignatureHelpOptions();
        signatureHelpOptions.setTriggerCharacters(java.util.Arrays.asList("(", ","));
        capabilities.setSignatureHelpProvider(signatureHelpOptions);
        
        capabilities.setDocumentSymbolProvider(true);
        capabilities.setWorkspaceSymbolProvider(true);
        
        capabilities.setDefinitionProvider(true);
        capabilities.setReferencesProvider(true);
        
        capabilities.setDocumentFormattingProvider(true);
        
        if (params != null && params.getInitializationOptions() != null) {
            handleInitializationOptions(params.getInitializationOptions());
        }
        
        InitializeResult result = new InitializeResult(capabilities);
        return CompletableFuture.completedFuture(result);
    }
    
    private void handleInitializationOptions(Object options) {
        try {
            if (options instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> optionsMap = (java.util.Map<String, Object>) options;
                
                if (optionsMap.containsKey("enableYuWeb")) {
                    boolean enableYuWeb = Boolean.TRUE.equals(optionsMap.get("enableYuWeb"));
                    context.setShowYuWebCompletions(enableYuWeb);
                    logger.info("YuWeb completions enabled: {}", enableYuWeb);
                }
                
                if (optionsMap.containsKey("headerDirectories")) {
                    Object dirs = optionsMap.get("headerDirectories");
                    if (dirs instanceof java.util.List) {
                        @SuppressWarnings("unchecked")
                        java.util.List<String> dirList = (java.util.List<String>) dirs;
                        for (String dir : dirList) {
                            context.loadHeaderFiles(dir);
                        }
                    }
                }
                
                if (optionsMap.containsKey("showYuWebCompletions")) {
                    boolean show = Boolean.TRUE.equals(optionsMap.get("showYuWebCompletions"));
                    context.setShowYuWebCompletions(show);
                    logger.info("Show YuWeb completions: {}", show);
                }
            }
        } catch (Exception e) {
            logger.warn("Error handling initialization options: {}", e.getMessage());
        }
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        shutdownRequested = 1;
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void exit() {
        System.exit(shutdownRequested);
    }

    @Override
    public void setTrace(SetTraceParams params) {
        logger.debug("Set trace: {}", params.getValue());
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return textDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return workspaceService;
    }

    @Override
    public void connect(LanguageClient client) {
        this.client = client;
        this.textDocumentService.connect(client);
        this.workspaceService.connect(client);
    }

    public LanguageClient getClient() {
        return client;
    }

    public LSContext getContext() {
        return context;
    }
}
