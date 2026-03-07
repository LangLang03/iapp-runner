package cn.langlang.yuweb;

public class YuWebConfig {
    private boolean debugMode = false;
    private boolean safeMode = false;
    private boolean preloadScripts = false;
    private boolean serveStaticFiles = true;
    private String serverName = "YuWeb";
    private String serverVersion = "1.0.0";
    
    private int maxPoolSize = 100;
    private int initialPoolSize = 10;
    private long connectionTimeout = 30000;
    private boolean useConnectionPool = true;
    
    private boolean http2Enabled = true;
    private boolean compressionEnabled = true;
    private int compressionMinSize = 1024;
    
    private int asyncTimeout = 30000;
    private int asyncThreadPoolSize = 50;
    
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
    
    public int getMaxPoolSize() {
        return maxPoolSize;
    }
    
    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize > 0 ? maxPoolSize : 100;
    }
    
    public int getInitialPoolSize() {
        return initialPoolSize;
    }
    
    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize > 0 ? initialPoolSize : 10;
    }
    
    public long getConnectionTimeout() {
        return connectionTimeout;
    }
    
    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout > 0 ? connectionTimeout : 30000;
    }
    
    public boolean isUseConnectionPool() {
        return useConnectionPool;
    }
    
    public void setUseConnectionPool(boolean useConnectionPool) {
        this.useConnectionPool = useConnectionPool;
    }
    
    public boolean isHttp2Enabled() {
        return http2Enabled;
    }
    
    public void setHttp2Enabled(boolean http2Enabled) {
        this.http2Enabled = http2Enabled;
    }
    
    public boolean isCompressionEnabled() {
        return compressionEnabled;
    }
    
    public void setCompressionEnabled(boolean compressionEnabled) {
        this.compressionEnabled = compressionEnabled;
    }
    
    public int getCompressionMinSize() {
        return compressionMinSize;
    }
    
    public void setCompressionMinSize(int compressionMinSize) {
        this.compressionMinSize = compressionMinSize > 0 ? compressionMinSize : 1024;
    }
    
    public int getAsyncTimeout() {
        return asyncTimeout;
    }
    
    public void setAsyncTimeout(int asyncTimeout) {
        this.asyncTimeout = asyncTimeout > 0 ? asyncTimeout : 30000;
    }
    
    public int getAsyncThreadPoolSize() {
        return asyncThreadPoolSize;
    }
    
    public void setAsyncThreadPoolSize(int asyncThreadPoolSize) {
        this.asyncThreadPoolSize = asyncThreadPoolSize > 0 ? asyncThreadPoolSize : 50;
    }
}
