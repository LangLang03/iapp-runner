package cn.langlang.yuweb;

import cn.langlang.iapp.api.IAppScript;
import cn.langlang.iapp.interpreter.Interpreter;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.cache.CachedScript;
import cn.langlang.yuweb.cache.ScriptCache;
import cn.langlang.yuweb.monitor.PerformanceMonitor;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RouteHandler {
    private static final Logger logger = LoggerFactory.getLogger(RouteHandler.class);
    
    private final YuWebServer server;
    private static final DatabaseManager dbManager = new DatabaseManager();
    private static final Map<String, Object> globalVariables = new ConcurrentHashMap<>();
    private static ScriptCache scriptCache;
    private static final Interpreter sharedInterpreter = new Interpreter();
    private static final PerformanceMonitor perfMonitor = PerformanceMonitor.getInstance();
    
    private static final String METRIC_REQUEST_COUNT = "request.count";
    private static final String METRIC_REQUEST_TIME = "request.time";
    private static final String METRIC_CACHE_HIT = "cache.hit";
    private static final String METRIC_CACHE_MISS = "cache.miss";
    private static final String METRIC_SCRIPT_COMPILE = "script.compile";
    private static final String METRIC_SCRIPT_EXECUTE = "script.execute";
    
    public RouteHandler(YuWebServer server) {
        this.server = server;
        ensureInitialized();
    }
    
    private synchronized void ensureInitialized() {
        if (scriptCache == null) {
            SharedFunctionRegistry.initialize(server, dbManager);
            scriptCache = new ScriptCache(SharedFunctionRegistry.getSharedRegistry());
            logger.info("Script cache initialized");
        }
    }
    
    public void handle(String scriptPath, Context ctx) throws Exception {
        long startTime = System.currentTimeMillis();
        perfMonitor.incrementCounter(METRIC_REQUEST_COUNT);
        
        String source = readFile(scriptPath);
        if (source == null) {
            ctx.status(404).result("Script not found: " + scriptPath);
            return;
        }
        
        RequestContext requestCtx = new RequestContext(ctx, server);
        server.setCurrentContext(requestCtx);
        
        try {
            ScriptCache.CacheStats beforeStats = scriptCache.getStats();
            
            long compileStart = System.currentTimeMillis();
            CachedScript cachedScript = scriptCache.getOrCompile(scriptPath, source);
            long compileTime = System.currentTimeMillis() - compileStart;
            perfMonitor.recordTime(METRIC_SCRIPT_COMPILE, compileTime);
            
            ScriptCache.CacheStats afterStats = scriptCache.getStats();
            if (afterStats.getHitCount() > beforeStats.getHitCount()) {
                perfMonitor.incrementCounter(METRIC_CACHE_HIT);
            } else if (afterStats.getMissCount() > beforeStats.getMissCount()) {
                perfMonitor.incrementCounter(METRIC_CACHE_MISS);
            }
            
            RuntimeContext runtimeContext = new RuntimeContext(SharedFunctionRegistry.getSharedRegistry());
            
            registerRequestFunctions(runtimeContext, requestCtx);
            
            for (Map.Entry<String, Object> entry : globalVariables.entrySet()) {
                runtimeContext.setVariable(entry.getKey(), entry.getValue());
            }
            
            runtimeContext.resetEndCodeRequest();
            
            long executeStart = System.currentTimeMillis();
            sharedInterpreter.execute(cachedScript.getProgram(), runtimeContext);
            long executeTime = System.currentTimeMillis() - executeStart;
            perfMonitor.recordTime(METRIC_SCRIPT_EXECUTE, executeTime);
            
        } catch (Exception e) {
            logger.error("Error executing script {}: {}", scriptPath, e.getMessage(), e);
            throw e;
        } finally {
            long elapsed = System.currentTimeMillis() - startTime;
            perfMonitor.recordTime(METRIC_REQUEST_TIME, elapsed);
            if (elapsed > 100) {
                logger.warn("Slow request: {} took {}ms", scriptPath, elapsed);
            }
        }
    }
    
    public void executeAppConfig(String appFile) throws Exception {
        String source = readFile(appFile);
        if (source == null) {
            return;
        }
        
        RuntimeContext runtimeContext = new RuntimeContext(SharedFunctionRegistry.getSharedRegistry());
        
        IAppScript script = IAppScript.createWithContext(runtimeContext);
        script.loadString(source);
        script.eval();
    }
    
    private String readFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }
            return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }
    
    private void registerRequestFunctions(RuntimeContext context, RequestContext requestCtx) {
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.MethodFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.GetFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.GetsFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.PostFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.PostsFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.FormFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.FormsFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.BodyFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.PathFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.UrlFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.HeaderFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.ClientIpFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.UserAgentFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.IsJsonFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.IsAjaxFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.GetCookieFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.SetCookieFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.DelCookieFunction(requestCtx));
        
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.response.JsonFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.response.TextFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.response.HtmlFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.response.ErrorFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.response.StatusFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.response.SetHeaderFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.response.RedirectFunction(requestCtx));
    }
    
    public DatabaseManager getDbManager() {
        return dbManager;
    }
    
    public static void closeDatabase() {
        dbManager.closeAll();
    }
    
    public static ScriptCache.CacheStats getCacheStats() {
        return scriptCache != null ? scriptCache.getStats() : null;
    }
    
    public static void clearCache() {
        if (scriptCache != null) {
            scriptCache.clear();
        }
    }
    
    public static PerformanceMonitor getPerformanceMonitor() {
        return perfMonitor;
    }
    
    public static void logPerformanceSummary() {
        perfMonitor.logSummary();
    }
}
