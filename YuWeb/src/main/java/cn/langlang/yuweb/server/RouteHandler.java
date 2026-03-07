package cn.langlang.yuweb.server;

import cn.langlang.iapp.api.IAppScript;
import cn.langlang.iapp.interpreter.Interpreter;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.YuWebConfig;
import cn.langlang.yuweb.cache.CachedScript;
import cn.langlang.yuweb.cache.ScriptCache;
import cn.langlang.yuweb.cache.ScriptPreloader;
import cn.langlang.yuweb.database.DatabaseManager;
import cn.langlang.yuweb.functions.SharedFunctionRegistry;
import cn.langlang.yuweb.functions.server.InfoFunction;
import cn.langlang.yuweb.functions.server.request.*;
import cn.langlang.yuweb.functions.server.response.*;
import cn.langlang.yuweb.monitor.PerformanceMonitor;
import cn.langlang.yuweb.web.ErrorPageGenerator;
import cn.langlang.yuweb.web.RequestContext;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RouteHandler {
    private static final Logger logger = LoggerFactory.getLogger(RouteHandler.class);
    
    private final YuWebServer server;
    private final YuWebConfig config;
    private final ErrorPageGenerator errorPageGenerator;
    private static final DatabaseManager dbManager = new DatabaseManager();
    private static final Map<String, Object> globalVariables = new ConcurrentHashMap<>();
    private static ScriptCache scriptCache;
    private static ScriptPreloader scriptPreloader;
    private static final ThreadLocal<Interpreter> interpreterPool = ThreadLocal.withInitial(Interpreter::new);
    private static final PerformanceMonitor perfMonitor = PerformanceMonitor.getInstance();
    
    private static final String METRIC_REQUEST_COUNT = "request.count";
    private static final String METRIC_REQUEST_TIME = "request.time";
    private static final String METRIC_CACHE_HIT = "cache.hit";
    private static final String METRIC_CACHE_MISS = "cache.miss";
    private static final String METRIC_SCRIPT_COMPILE = "script.compile";
    private static final String METRIC_SCRIPT_EXECUTE = "script.execute";
    
    private static final Map<String, String> MIME_TYPES = new ConcurrentHashMap<>();
    
    static {
        MIME_TYPES.put("html", "text/html; charset=utf-8");
        MIME_TYPES.put("htm", "text/html; charset=utf-8");
        MIME_TYPES.put("css", "text/css; charset=utf-8");
        MIME_TYPES.put("js", "application/javascript; charset=utf-8");
        MIME_TYPES.put("json", "application/json; charset=utf-8");
        MIME_TYPES.put("xml", "application/xml; charset=utf-8");
        MIME_TYPES.put("txt", "text/plain; charset=utf-8");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("svg", "image/svg+xml");
        MIME_TYPES.put("ico", "image/x-icon");
        MIME_TYPES.put("woff", "font/woff");
        MIME_TYPES.put("woff2", "font/woff2");
        MIME_TYPES.put("ttf", "font/ttf");
        MIME_TYPES.put("eot", "application/vnd.ms-fontobject");
        MIME_TYPES.put("pdf", "application/pdf");
        MIME_TYPES.put("zip", "application/zip");
        MIME_TYPES.put("mp3", "audio/mpeg");
        MIME_TYPES.put("mp4", "video/mp4");
        MIME_TYPES.put("webm", "video/webm");
    }
    
    public RouteHandler(YuWebServer server) {
        this.server = server;
        this.config = server.getConfig();
        this.errorPageGenerator = new ErrorPageGenerator(config);
        ensureInitialized();
    }
    
    private synchronized void ensureInitialized() {
        if (scriptCache == null) {
            SharedFunctionRegistry.initialize(server, dbManager);
            scriptCache = new ScriptCache(SharedFunctionRegistry.getSharedRegistry());
            logger.info("Script cache initialized");
        }
    }
    
    public static synchronized void initializePreloader(String webrootPath) {
        if (scriptPreloader == null) {
            scriptPreloader = new ScriptPreloader(SharedFunctionRegistry.getSharedRegistry(), webrootPath);
            scriptPreloader.preloadAll();
        }
    }
    
    public void handle(String scriptPath, Context ctx) throws Exception {
        long startTime = System.currentTimeMillis();
        perfMonitor.incrementCounter(METRIC_REQUEST_COUNT);
        
        String absolutePath = server.getProjectPath() + "/webroot" + scriptPath;
        File file = new File(absolutePath);
        
        if (!file.exists()) {
            errorPageGenerator.sendNotFound(ctx, scriptPath);
            return;
        }
        
        if (isStaticFile(scriptPath)) {
            if (!config.isServeStaticFiles()) {
                errorPageGenerator.sendError(ctx, 403, "Static file serving is disabled");
                return;
            }
            serveStaticFile(file, ctx);
            return;
        }
        
        if (!scriptPath.endsWith(".iapp")) {
            String iappPath = scriptPath + ".iapp";
            File iappFile = new File(server.getProjectPath() + "/webroot" + iappPath);
            if (iappFile.exists()) {
                scriptPath = iappPath;
                absolutePath = iappFile.getAbsolutePath();
                file = iappFile;
            } else {
                errorPageGenerator.sendNotFound(ctx, scriptPath);
                return;
            }
        }
        
        if (config.isSafeMode()) {
            if (scriptPreloader == null) {
                logger.error("Safe mode enabled but preloader not initialized");
                errorPageGenerator.sendError(ctx, 500, "Server configuration error");
                return;
            }
            
            if (!scriptPreloader.isScriptAllowed(scriptPath)) {
                logger.warn("Script not in preloaded list (safe mode): {}", scriptPath);
                errorPageGenerator.sendError(ctx, 400, "Script not allowed in safe mode");
                return;
            }
            
            handlePreloadedScript(scriptPath, ctx, startTime);
            return;
        }
        
        if (config.isPreloadScripts() && scriptPreloader != null) {
            ScriptPreloader.PreloadedScript preloaded = scriptPreloader.getScript(scriptPath);
            if (preloaded != null) {
                handlePreloadedScript(scriptPath, ctx, startTime);
                return;
            }
        }
        
        handleDynamicScript(scriptPath, absolutePath, ctx, startTime);
    }
    
    private void handlePreloadedScript(String scriptPath, Context ctx, long startTime) {
        ScriptPreloader.PreloadedScript preloaded = scriptPreloader.getScript(scriptPath);
        if (preloaded == null) {
            errorPageGenerator.sendNotFound(ctx, scriptPath);
            return;
        }
        
        RequestContext requestCtx = new RequestContext(ctx, server);
        server.setCurrentContext(requestCtx);
        
        try {
            perfMonitor.incrementCounter(METRIC_CACHE_HIT);
            
            RuntimeContext runtimeContext = new RuntimeContext(SharedFunctionRegistry.getSharedRegistry());
            registerRequestFunctions(runtimeContext, requestCtx);
            
            for (Map.Entry<String, Object> entry : globalVariables.entrySet()) {
                runtimeContext.setVariable(entry.getKey(), entry.getValue());
            }
            
            runtimeContext.resetEndCodeRequest();
            
            long executeStart = System.currentTimeMillis();
            interpreterPool.get().execute(preloaded.getProgram(), runtimeContext);
            long executeTime = System.currentTimeMillis() - executeStart;
            perfMonitor.recordTime(METRIC_SCRIPT_EXECUTE, executeTime);
            
        } catch (Exception e) {
            logger.error("Error executing preloaded script {}: {}", scriptPath, e.getMessage(), e);
            errorPageGenerator.sendServerError(ctx, "Script execution error: " + e.getMessage(), e);
        } finally {
            long elapsed = System.currentTimeMillis() - startTime;
            perfMonitor.recordTime(METRIC_REQUEST_TIME, elapsed);
            if (elapsed > 100) {
                logger.warn("Slow request: {} took {}ms", scriptPath, elapsed);
            }
        }
    }
    
    private void handleDynamicScript(String scriptPath, String absolutePath, Context ctx, long startTime) {
        String source = readFile(absolutePath);
        if (source == null) {
            errorPageGenerator.sendNotFound(ctx, scriptPath);
            return;
        }
        
        RequestContext requestCtx = new RequestContext(ctx, server);
        server.setCurrentContext(requestCtx);
        
        try {
            ScriptCache.CacheStats beforeStats = scriptCache.getStats();
            
            long compileStart = System.currentTimeMillis();
            CachedScript cachedScript = scriptCache.getOrCompile(absolutePath, source);
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
            interpreterPool.get().execute(cachedScript.getProgram(), runtimeContext);
            long executeTime = System.currentTimeMillis() - executeStart;
            perfMonitor.recordTime(METRIC_SCRIPT_EXECUTE, executeTime);
            
        } catch (Exception e) {
            logger.error("Error executing script {}: {}", scriptPath, e.getMessage(), e);
            errorPageGenerator.sendServerError(ctx, "Script execution error: " + e.getMessage(), e);
        } finally {
            long elapsed = System.currentTimeMillis() - startTime;
            perfMonitor.recordTime(METRIC_REQUEST_TIME, elapsed);
            if (elapsed > 100) {
                logger.warn("Slow request: {} took {}ms", scriptPath, elapsed);
            }
        }
    }
    
    private boolean isStaticFile(String path) {
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == path.length() - 1) {
            return false;
        }
        String ext = path.substring(dotIndex + 1).toLowerCase();
        return MIME_TYPES.containsKey(ext);
    }
    
    private void serveStaticFile(File file, Context ctx) throws IOException {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        String ext = dotIndex > 0 ? fileName.substring(dotIndex + 1).toLowerCase() : "";
        
        String contentType = MIME_TYPES.getOrDefault(ext, "application/octet-stream");
        ctx.contentType(contentType);
        
        ctx.header("Cache-Control", "public, max-age=3600");
        ctx.header("X-Content-Type-Options", "nosniff");
        ctx.header("Content-Length", String.valueOf(file.length()));
        
        try (java.io.InputStream is = new java.io.BufferedInputStream(new java.io.FileInputStream(file))) {
            ctx.result(is);
        }
    }
    
    public void executeAppConfig(String appFile) {
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
            return Files.readString(file.toPath());
        } catch (IOException e) {
            return null;
        }
    }
    
    private void registerRequestFunctions(RuntimeContext context, RequestContext requestCtx) {
        context.getFunctionRegistry().registerFunction(new MethodFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new GetFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new GetsFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new PostFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new PostsFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new FormFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new FormsFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new BodyFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new PathFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new UrlFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new HeaderFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new ClientIpFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new UserAgentFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new IsJsonFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new IsAjaxFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new GetCookieFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new SetCookieFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new DelCookieFunction(requestCtx));
        
        context.getFunctionRegistry().registerFunction(new JsonFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new TextFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new HtmlFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new ErrorFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new StatusFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new SetHeaderFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new RedirectFunction(requestCtx));
        
        context.getFunctionRegistry().registerFunction(new InfoFunction(requestCtx, server));
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
    
    public static ScriptPreloader.PreloadStats getPreloadStats() {
        return scriptPreloader != null ? scriptPreloader.getStats() : null;
    }
    
    public static ScriptPreloader getScriptPreloader() {
        return scriptPreloader;
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
