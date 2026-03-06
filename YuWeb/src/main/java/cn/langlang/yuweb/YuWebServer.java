package cn.langlang.yuweb;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class YuWebServer {
    private static final Logger logger = LoggerFactory.getLogger(YuWebServer.class);
    private int port = 8080;
    private String projectPath;
    private Javalin app;
    private static final ThreadLocal<RequestContext> currentContext = new ThreadLocal<>();
    
    public YuWebServer(String projectPath) {
        this.projectPath = projectPath != null ? projectPath : ".";
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
        
        File staticDir = new File(projectPath + "/static");
        
        app = Javalin.create(config -> {
            config.http.maxRequestSize = 10_000_000L;
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
        
        String appFile = projectPath + "/app.iapp";
        File appFileObj = new File(appFile);
        if (appFileObj.exists()) {
            executeAppFile(appFile);
        }
        
        app.start(port);
        logger.info("YuWeb Server started on port {}", port);
        logger.info("Project path: {}", new File(projectPath).getAbsolutePath());
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
            ctx.status(404).result("Not Found: " + path);
            return;
        }
        
        File scriptFile = new File(projectPath + "/webroot" + scriptPath);
        if (!scriptFile.exists()) {
            ctx.status(404).result("Not Found: " + path);
            return;
        }
        
        try {
            RouteHandler handler = new RouteHandler(this);
            handler.handle(scriptFile.getAbsolutePath(), ctx);
        } catch (Exception e) {
            logger.error("Error handling request: {}", e.getMessage(), e);
            ctx.status(500).result("Internal Server Error");
        }
    }
    
    private String findScriptPath(String path) {
        if (path.equals("/")) {
            return "/index.iapp";
        }
        
        if (path.endsWith(".iapp")) {
            return path;
        }
        
        String iappPath = path + ".iapp";
        File iappFile = new File(projectPath + "/webroot" + iappPath);
        if (iappFile.exists()) {
            return iappPath;
        }
        
        String indexPath = path + "/index.iapp";
        File indexFile = new File(projectPath + "/webroot" + indexPath);
        if (indexFile.exists()) {
            return indexPath;
        }
        
        return null;
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
        YuWebServer server = new YuWebServer(projectPath);
        server.start();
    }
}
