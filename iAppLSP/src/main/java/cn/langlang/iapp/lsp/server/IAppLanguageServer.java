package cn.langlang.iapp.lsp.server;

import cn.langlang.iapp.lsp.core.LSContext;
import cn.langlang.iapp.lsp.registry.ModuleRegistry;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.concurrent.CompletableFuture;

public class IAppLanguageServer implements LanguageServer, LanguageClientAware {
    private IAppTextDocumentService textDocumentService;
    private IAppWorkspaceService workspaceService;
    private LanguageClient client;
    private LSContext context;
    private int shutdownRequested = 0;

    public IAppLanguageServer() {
        this.context = new LSContext();
        
        ModuleRegistry moduleRegistry = new ModuleRegistry(context);
        moduleRegistry.autoDiscover();
        
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
        
        this.textDocumentService = new IAppTextDocumentService(context);
        this.workspaceService = new IAppWorkspaceService(context);
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
        
        InitializeResult result = new InitializeResult(capabilities);
        return CompletableFuture.completedFuture(result);
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
