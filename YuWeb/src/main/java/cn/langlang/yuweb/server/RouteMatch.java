package cn.langlang.yuweb.server;

import java.util.HashMap;
import java.util.Map;

public class RouteMatch {
    private final String scriptPath;
    private final Map<String, String> params;
    
    public RouteMatch(String scriptPath) {
        this.scriptPath = scriptPath;
        this.params = new HashMap<>();
    }
    
    public RouteMatch(String scriptPath, Map<String, String> params) {
        this.scriptPath = scriptPath;
        this.params = params != null ? params : new HashMap<>();
    }
    
    public String getScriptPath() {
        return scriptPath;
    }
    
    public Map<String, String> getParams() {
        return params;
    }
    
    public void addParam(String name, String value) {
        params.put(name, value);
    }
    
    public String getParam(String name) {
        return params.get(name);
    }
    
    public boolean hasParams() {
        return !params.isEmpty();
    }
}
