package cn.langlang.iapp.functions.net;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HdFunction extends AbstractFunction {
    
    private static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    private static final int DEFAULT_READ_TIMEOUT = 10000;
    
    @Override
    public String getName() {
        return "hd";
    }
    
    @Override
    public int getMinParameters() {
        return 1;
    }
    
    @Override
    public int getMaxParameters() {
        return 3;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String urlStr = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String method = "GET";
        String charset = "UTF-8";
        
        if (arguments.size() > 1) {
            method = arguments.get(1) != null ? arguments.get(1).toString() : "GET";
        }
        if (arguments.size() > 2) {
            charset = arguments.get(2) != null ? arguments.get(2).toString() : "UTF-8";
        }
        
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
            
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
            return "";
        } catch (Exception e) {
            throw new FunctionException("HTTP 请求失败: " + e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.STRING);
    }
}
