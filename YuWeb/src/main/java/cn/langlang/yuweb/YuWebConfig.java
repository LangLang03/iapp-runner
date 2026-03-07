package cn.langlang.yuweb;

public class YuWebConfig {
    private boolean debugMode = false;
    private boolean safeMode = false;
    private boolean preloadScripts = false;
    private boolean serveStaticFiles = true;
    private String serverName = "YuWeb";
    private String serverVersion = "1.0.0";
    
    public YuWebConfig() {
    }
    
    public boolean isDebugMode() {
        return debugMode;
    }
    
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
    
    public boolean isSafeMode() {
        return safeMode;
    }
    
    public void setSafeMode(boolean safeMode) {
        this.safeMode = safeMode;
    }
    
    public boolean isPreloadScripts() {
        return preloadScripts;
    }
    
    public void setPreloadScripts(boolean preloadScripts) {
        this.preloadScripts = preloadScripts;
    }
    
    public boolean isServeStaticFiles() {
        return serveStaticFiles;
    }
    
    public void setServeStaticFiles(boolean serveStaticFiles) {
        this.serveStaticFiles = serveStaticFiles;
    }
    
    public String getServerName() {
        return serverName;
    }
    
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    
    public String getServerVersion() {
        return serverVersion;
    }
    
    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }
    
    public String getServerSignature() {
        return serverName + "/" + serverVersion;
    }
}
