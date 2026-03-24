package cn.langlang.iapp.functions.net;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HsFunction extends AbstractFunction {
    
    private static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    private static final int DEFAULT_READ_TIMEOUT = 10000;
    
    @Override
    public String getName() {
        return "hs";
    }
    
    @Override
    public int getMinParameters() {
        return 1;
    }
    
    @Override
    public int getMaxParameters() {
        return 6;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String urlStr = arguments.get(0) != null ? arguments.get(0).toString() : "";
        
        String postData = null;
        String charset = "UTF-8";
        String cookie = null;
        boolean autoCookie = false;
        
        if (arguments.size() > 1 && arguments.get(1) != null) {
            postData = arguments.get(1).toString();
        }
        
        if (arguments.size() > 2 && arguments.get(2) != null) {
            charset = arguments.get(2).toString();
        }
        
        if (arguments.size() > 3 && arguments.get(3) != null) {
            cookie = arguments.get(3).toString();
        }
        
        if (arguments.size() > 4 && arguments.get(4) instanceof Boolean) {
            autoCookie = (Boolean) arguments.get(4);
        }
        
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            
            if (postData != null && !postData.isEmpty()) {
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            } else {
                conn.setRequestMethod("GET");
            }
            
            conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
            
            if (cookie != null) {
                conn.setRequestProperty("Cookie", cookie);
            }
            
            if (postData != null && !postData.isEmpty()) {
                try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
                    out.writeBytes(postData);
                    out.flush();
                }
            }
            
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
        return types(ParamType.STRING, ParamType.OUTPUT);
    }
    
    @Override
    public List<List<ParamType>> getParamTypeLists() {
        return typeLists(
            types(ParamType.STRING, ParamType.OUTPUT),
            types(ParamType.STRING, ParamType.STRING, ParamType.STRING, ParamType.OUTPUT),
            types(ParamType.STRING, ParamType.STRING, ParamType.STRING, ParamType.STRING, ParamType.OUTPUT),
            types(ParamType.STRING, ParamType.STRING, ParamType.STRING, ParamType.STRING, ParamType.BOOLEAN, ParamType.OUTPUT)
        );
    }
}
