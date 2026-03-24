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
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class HufFunction extends AbstractFunction {
    
    private static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    private static final int DEFAULT_READ_TIMEOUT = 10000;
    
    @Override
    public String getName() {
        return "huf";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return 4;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String urlStr = arguments.get(0) != null ? arguments.get(0).toString() : "";
        Object params = arguments.get(1);
        String charset = "UTF-8";
        String method = "POST";
        
        if (arguments.size() > 2) {
            charset = arguments.get(2) != null ? arguments.get(2).toString() : "UTF-8";
        }
        if (arguments.size() > 3) {
            method = arguments.get(3) != null ? arguments.get(3).toString() : "POST";
        }
        
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            StringBuilder postData = new StringBuilder();
            if (params instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> paramMap = (Map<String, Object>) params;
                boolean first = true;
                for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                    if (!first) postData.append("&");
                    postData.append(URLEncoder.encode(entry.getKey(), charset));
                    postData.append("=");
                    postData.append(URLEncoder.encode(String.valueOf(entry.getValue()), charset));
                    first = false;
                }
            } else if (params instanceof Object[]) {
                Object[] arr = (Object[]) params;
                for (int i = 0; i < arr.length - 1; i += 2) {
                    if (i > 0) postData.append("&");
                    postData.append(URLEncoder.encode(String.valueOf(arr[i]), charset));
                    postData.append("=");
                    postData.append(URLEncoder.encode(String.valueOf(arr[i + 1]), charset));
                }
            }
            
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
                out.writeBytes(postData.toString());
                out.flush();
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
        return types(ParamType.STRING, ParamType.OBJECT, ParamType.STRING, ParamType.STRING);
    }
}
