package cn.langlang.iapp.functions.net;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HwFunction extends AbstractFunction {
    
    private static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    private static final int DEFAULT_READ_TIMEOUT = 10000;
    
    @Override
    public String getName() {
        return "hw";
    }
    
    @Override
    public int getMinParameters() {
        return 1;
    }
    
    @Override
    public int getMaxParameters() {
        return 2;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) {
        String urlStr = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String charset = "UTF-8";
        
        if (arguments.size() > 1) {
            charset = arguments.get(1) != null ? arguments.get(1).toString() : "UTF-8";
        }
        
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
            
            return (long) conn.getResponseCode();
        } catch (Exception e) {
            return 0L;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING);
    }
}
