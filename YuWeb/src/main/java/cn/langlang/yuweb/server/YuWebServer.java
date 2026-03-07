package cn.langlang.yuweb.server;

import cn.langlang.yuweb.YuWebConfig;
import cn.langlang.yuweb.web.ErrorPageGenerator;
import cn.langlang.yuweb.web.RequestContext;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

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
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down YuWeb Server...");
            stop();
        }));
        
        app = Javalin.create(javalinConfig -> {
            javalinConfig.http.maxRequestSize = 10_000_000L;
            javalinConfig.http.defaultContentType = "text/html; charset=utf-8";
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
        
        JitWarmup.warmup();
        
        if (config.isSafeMode() || config.isPreloadScripts()) {
            String webrootPath = projectPath + "/webroot";
            logger.info("Preloading scripts from: {}", webrootPath);
            RouteHandler.initializePreloader(webrootPath);
        }
        
        app.start(port);
        
        logger.info("YuWeb Server started on port {}", port);
        logger.info("Project path: {}", new File(projectPath).getAbsolutePath());
        logger.info("Safe mode: {}", config.isSafeMode() ? "ENABLED" : "disabled");
        logger.info("Preload scripts: {}", config.isPreloadScripts() ? "ENABLED" : "disabled");
        logger.info("Serve static files: {}", config.isServeStaticFiles() ? "ENABLED" : "disabled");
        
        if (config.isDebugMode()) {
            logger.warn("Debug mode is ENABLED - Error details will be shown to clients");
        }
    }
    
    public void stop() {
        if (app != null) {
            app.stop();
        }
        RouteHandler.closeDatabase();
    }
    
    private void handleRequest(Context ctx) {
        String path = ctx.path();
        
        String scriptPath = findScriptPath(path);
        
        if (scriptPath == null) {
            errorPageGenerator.sendNotFound(ctx, path);
            return;
        }
        
        File scriptFile = new File(projectPath + "/webroot" + scriptPath);
        if (!scriptFile.exists()) {
            errorPageGenerator.sendNotFound(ctx, path);
            return;
        }
        
        try {
            RouteHandler handler = new RouteHandler(this);
            handler.handle(scriptPath, ctx);
        } catch (Exception e) {
            logger.error("Error handling request: {}", e.getMessage(), e);
            errorPageGenerator.sendServerError(ctx, "Request handling error", e);
        }
    }
    
    private String findScriptPath(String path) {
        if (path.equals("/")) {
            return "/index.iapp";
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
        
        if (directFile.exists() && directFile.isFile()) {
            return normalizedPath;
        }
        
        if (normalizedPath.endsWith(".iapp")) {
            return normalizedPath;
        }
        
        String iappPath = normalizedPath + ".iapp";
        File iappFile = new File(webrootDir, iappPath);
        if (iappFile.exists() && isWithinWebroot(iappFile, webrootDir)) {
            return iappPath;
        }
        
        String indexPath = normalizedPath + "/index.iapp";
        File indexFile = new File(webrootDir, indexPath);
        if (indexFile.exists() && isWithinWebroot(indexFile, webrootDir)) {
            return indexPath;
        }
        
        return null;
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
