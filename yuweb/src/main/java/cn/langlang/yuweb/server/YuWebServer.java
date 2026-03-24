package cn.langlang.yuweb.server;

import cn.langlang.yuweb.YuWebConfig;
import cn.langlang.yuweb.web.ErrorPageGenerator;
import cn.langlang.yuweb.web.RequestContext;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class YuWebServer {
    private static final Logger logger = LoggerFactory.getLogger(YuWebServer.class);
    
    public static final String VERSION = "1.0.0";
    public static final String SERVER_NAME = "YuWeb";
    
    private int port = 8080;
    private String projectPath;
    private Javalin app;
    private final YuWebConfig config;
    private final ErrorPageGenerator errorPageGenerator;
    private static final ThreadLocal<RequestContext> currentContext = new ThreadLocal<>();
    
    private ExecutorService asyncExecutor;
    private volatile boolean running = false;
    
    public YuWebServer(String projectPath) {
        this.projectPath = projectPath != null ? projectPath : ".";
        this.config = new YuWebConfig();
        this.config.setServerName(SERVER_NAME);
        this.config.setServerVersion(VERSION);
        this.errorPageGenerator = new ErrorPageGenerator(config);
    }
    
    public YuWebServer(String projectPath, boolean debugMode) {
        this(projectPath);
        this.config.setDebugMode(debugMode);
    }
    
    public YuWebServer(String projectPath, YuWebConfig config) {
        this.projectPath = projectPath != null ? projectPath : ".";
        this.config = config;
        this.errorPageGenerator = new ErrorPageGenerator(config);
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public int getPort() {
        return port;
    }
    
    public String getProjectPath() {
        return projectPath;
    }
    
    public YuWebConfig getConfig() {
        return config;
    }
    
    public void setDebugMode(boolean debugMode) {
        this.config.setDebugMode(debugMode);
    }
    
    public boolean isDebugMode() {
        return config.isDebugMode();
    }
    
    public void setCurrentContext(RequestContext context) {
        currentContext.set(context);
    }
    
    public RequestContext getCurrentContext() {
        return currentContext.get();
    }
    
    public void clearCurrentContext() {
        currentContext.remove();
    }
    
    public void start() {
        running = true;
        
        RouteHandler.configureDatabaseManager(config);
        
        asyncExecutor = Executors.newFixedThreadPool(config.getAsyncThreadPoolSize());
        logger.info("Async thread pool initialized with {} threads", config.getAsyncThreadPoolSize());
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down YuWeb Server...");
            stop();
        }));
        
        app = Javalin.create(javalinConfig -> {
            javalinConfig.http.maxRequestSize = 10_000_000L;
            javalinConfig.http.defaultContentType = "text/html; charset=utf-8";
            
            if (config.isHttp2Enabled()) {
                javalinConfig.http.generateEtags = true;
                logger.info("HTTP/2 optimizations enabled (ETags)");
            }
            
            String webrootPath = projectPath + "/webroot";
            File webrootDir = new File(webrootPath);
            if (webrootDir.exists() && webrootDir.isDirectory()) {
                javalinConfig.staticFiles.add(webrootPath, Location.EXTERNAL);
                logger.info("Static files served from: {}", webrootPath);
            }
        });
        
        app.before(ctx -> {
            currentContext.set(new RequestContext(ctx, this));
        });
        
        app.after(ctx -> {
            currentContext.remove();
        });
        
        app.get("/*", this::handleRequest);
        app.post("/*", this::handleRequest);
        app.put("/*", this::handleRequest);
        app.delete("/*", this::handleRequest);
        app.patch("/*", this::handleRequest);
        app.options("/*", this::handleRequest);
        app.head("/*", this::handleRequest);
        
        app.exception(Exception.class, (e, ctx) -> {
            logger.error("Unhandled exception: {}", e.getMessage(), e);
            errorPageGenerator.sendServerError(ctx, "Internal server error", e);
        });
        
        String appFile = projectPath + "/app.iapp";
        File appFileObj = new File(appFile);
        if (appFileObj.exists()) {
            executeAppFile(appFile);
        }
        
        if (config.isSafeMode() || config.isPreloadScripts()) {
            String webrootPath = projectPath + "/webroot";
            logger.info("Preloading scripts from: {}", webrootPath);
            RouteHandler.initializePreloader(webrootPath);
        }
        
        app.start(port);
        
        logger.info("========================================");
        logger.info("YuWeb Server started on port {}", port);
        logger.info("Project path: {}", new File(projectPath).getAbsolutePath());
        logger.info("----------------------------------------");
        logger.info("Configuration:");
        logger.info("  Safe mode: {}", config.isSafeMode() ? "ENABLED" : "disabled");
        logger.info("  Preload scripts: {}", config.isPreloadScripts() ? "ENABLED" : "disabled");
        logger.info("  Serve static files: {}", config.isServeStaticFiles() ? "ENABLED" : "disabled");
        logger.info("  HTTP/2 optimizations: {}", config.isHttp2Enabled() ? "ENABLED" : "disabled");
        logger.info("  Response compression: {}", config.isCompressionEnabled() ? "ENABLED" : "disabled");
        logger.info("----------------------------------------");
        logger.info("Performance:");
        logger.info("  Connection pool size: {}", config.getMaxPoolSize());
        logger.info("  Initial pool size: {}", config.getInitialPoolSize());
        logger.info("  Async thread pool: {}", config.getAsyncThreadPoolSize());
        logger.info("========================================");
        
        if (config.isDebugMode()) {
            logger.warn("Debug mode is ENABLED - Error details will be shown to clients");
        }
    }
    
    public void stop() {
        running = false;
        
        if (asyncExecutor != null) {
            asyncExecutor.shutdown();
            try {
                if (!asyncExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    asyncExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                asyncExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            logger.info("Async thread pool shut down");
        }
        
        if (app != null) {
            app.stop();
        }
        RouteHandler.closeDatabase();
    }
    
    public <T> CompletableFuture<T> runAsync(java.util.concurrent.Callable<T> task) {
        if (!running || asyncExecutor == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("Server not running"));
        }
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, asyncExecutor);
    }
    
    public CompletableFuture<Void> runAsync(Runnable task) {
        if (!running || asyncExecutor == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("Server not running"));
        }
        return CompletableFuture.runAsync(task, asyncExecutor);
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public ExecutorService getAsyncExecutor() {
        return asyncExecutor;
    }
    
    private void handleRequest(Context ctx) {
        String path = ctx.path();
        
        RouteMatch routeMatch = findScriptPath(path);
        
        if (routeMatch == null) {
            errorPageGenerator.sendNotFound(ctx, path);
            return;
        }
        
        String scriptPath = routeMatch.getScriptPath();
        File scriptFile = new File(projectPath + "/webroot" + scriptPath);
        if (!scriptFile.exists()) {
            errorPageGenerator.sendNotFound(ctx, path);
            return;
        }
        
        try {
            RouteHandler handler = new RouteHandler(this);
            handler.handle(scriptPath, ctx, routeMatch.getParams());
        } catch (Exception e) {
            logger.error("Error handling request: {}", e.getMessage(), e);
            errorPageGenerator.sendServerError(ctx, "Request handling error", e);
        }
    }
    
    private RouteMatch findScriptPath(String path) {
        if (path.equals("/")) {
            return new RouteMatch("/index.iapp");
        }
        
        String normalizedPath = normalizePath(path);
        if (normalizedPath == null) {
            return null;
        }
        
        File webrootDir = new File(projectPath, "webroot");
        File directFile = new File(webrootDir, normalizedPath);
        
        if (!isWithinWebroot(directFile, webrootDir)) {
            logger.warn("Path traversal attempt detected: {}", path);
            return null;
        }
        
        // Direct file match
        if (directFile.exists() && directFile.isFile()) {
            return new RouteMatch(normalizedPath);
        }
        
        // Try .iapp extension
        if (!normalizedPath.endsWith(".iapp")) {
            String iappPath = normalizedPath + ".iapp";
            File iappFile = new File(webrootDir, iappPath);
            if (iappFile.exists() && isWithinWebroot(iappFile, webrootDir)) {
                return new RouteMatch(iappPath);
            }
        }
        
        // Try index.iapp in directory
        String indexPath = normalizedPath + "/index.iapp";
        File indexFile = new File(webrootDir, indexPath);
        if (indexFile.exists() && isWithinWebroot(indexFile, webrootDir)) {
            return new RouteMatch(indexPath);
        }
        
        // Try dynamic route matching (e.g., /user/:id -> /user/[id].iapp or /user/_/id.iapp)
        RouteMatch dynamicMatch = findDynamicRoute(normalizedPath, webrootDir);
        if (dynamicMatch != null) {
            return dynamicMatch;
        }
        
        return null;
    }
    
    private RouteMatch findDynamicRoute(String path, File webrootDir) {
        String[] segments = path.split("/");
        Map<String, String> params = new HashMap<>();
        
        // Try to find a matching dynamic route
        // Pattern: /user/123 -> /user/:id.iapp or /user/[id].iapp
        StringBuilder currentPath = new StringBuilder();
        
        for (int i = 0; i < segments.length; i++) {
            String segment = segments[i];
            if (segment.isEmpty()) continue;
            
            currentPath.append("/").append(segment);
            
            // Check if this segment could be a parameter
            String remainingPath = buildRemainingPath(segments, i + 1);
            
            // Try :param pattern
            RouteMatch match = tryParamPattern(currentPath.toString(), remainingPath, webrootDir, params);
            if (match != null) {
                return match;
            }
        }
        
        // Try pattern where last segment is a parameter
        // /user/123 -> /user/:id.iapp
        if (segments.length >= 2) {
            String lastSegment = segments[segments.length - 1];
            String parentPath = buildParentPath(segments);
            
            // Check for :param.iapp files in parent directory
            File parentDir = new File(webrootDir, parentPath);
            if (parentDir.exists() && parentDir.isDirectory()) {
                File[] files = parentDir.listFiles((dir, name) -> name.startsWith(":") && name.endsWith(".iapp"));
                if (files != null) {
                    for (File file : files) {
                        String paramName = file.getName().substring(1, file.getName().length() - 5);
                        params.put(paramName, lastSegment);
                        return new RouteMatch(parentPath + "/" + file.getName(), params);
                    }
                }
                
                // Check for [param].iapp pattern
                files = parentDir.listFiles((dir, name) -> name.startsWith("[") && name.endsWith("].iapp"));
                if (files != null) {
                    for (File file : files) {
                        String paramName = file.getName().substring(1, file.getName().length() - 6);
                        params.put(paramName, lastSegment);
                        return new RouteMatch(parentPath + "/" + file.getName(), params);
                    }
                }
            }
        }
        
        return null;
    }
    
    private RouteMatch tryParamPattern(String currentPath, String remainingPath, File webrootDir, Map<String, String> params) {
        // Not used in current implementation
        return null;
    }
    
    private String buildRemainingPath(String[] segments, int startIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < segments.length; i++) {
            if (!segments[i].isEmpty()) {
                sb.append("/").append(segments[i]);
            }
        }
        return sb.toString();
    }
    
    private String buildParentPath(String[] segments) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segments.length - 1; i++) {
            if (!segments[i].isEmpty()) {
                sb.append("/").append(segments[i]);
            }
        }
        return sb.toString();
    }
    
    private String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        path = path.replace('\\', '/');
        while (path.contains("//")) {
            path = path.replace("//", "/");
        }
        if (path.contains("..") || path.contains("~") || path.contains("\0")) {
            return null;
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }
    
    private boolean isWithinWebroot(File file, File webrootDir) {
        try {
            String canonicalPath = file.getCanonicalPath();
            String canonicalWebroot = webrootDir.getCanonicalPath();
            return canonicalPath.startsWith(canonicalWebroot + File.separator) || 
                   canonicalPath.equals(canonicalWebroot);
        } catch (Exception e) {
            return false;
        }
    }
    
    private void executeAppFile(String appFile) {
        try {
            RouteHandler handler = new RouteHandler(this);
            handler.executeAppConfig(appFile);
        } catch (Exception e) {
            logger.error("Error executing app.iapp: {}", e.getMessage(), e);
        }
    }
    
    public static void main(String[] args) {
        String projectPath = args.length > 0 ? args[0] : ".";
        boolean debugMode = false;
        
        for (int i = 1; i < args.length; i++) {
            if ("--debug".equals(args[i]) || "-d".equals(args[i])) {
                debugMode = true;
            }
        }
        
        YuWebServer server = new YuWebServer(projectPath, debugMode);
        server.start();
    }
}
