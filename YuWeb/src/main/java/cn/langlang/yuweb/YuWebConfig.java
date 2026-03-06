package cn.langlang.yuweb;

public class YuWebConfig {
    private boolean debugMode = false;
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
