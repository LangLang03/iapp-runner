package cn.langlang.iapp.functions.net;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HsFunction implements IFunction {
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
        return 7;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String urlStr = toString(arguments.get(0));
        
        if (urlStr.equals("cookie")) {
            return getCookieManager().getAllCookies();
        }
        
        if (urlStr.equals("del cookie")) {
            getCookieManager().clear();
            return null;
        }
        
        if (urlStr.startsWith("cookie:")) {
            String domain = urlStr.substring(7);
            return getCookieManager().getCookiesForDomain(domain);
        }
        
        String postData = null;
        String encoding = "UTF-8";
        String cookie = null;
        boolean autoCookie = false;
        String headers = null;
        int connectTimeout = 15000;
        int readTimeout = 15000;
        String proxy = null;
        
        if (arguments.size() >= 2) {
            postData = toString(arguments.get(1));
        }
        if (arguments.size() >= 3) {
            encoding = toString(arguments.get(2));
        }
        if (arguments.size() >= 4) {
            cookie = toString(arguments.get(3));
            if ("null".equals(cookie)) cookie = null;
        }
        if (arguments.size() >= 5) {
            autoCookie = toBoolean(arguments.get(4));
        }
        if (arguments.size() >= 6) {
            headers = toString(arguments.get(5));
            if ("null".equals(headers)) headers = null;
        }
        if (arguments.size() >= 7) {
            connectTimeout = toInt(arguments.get(5));
            readTimeout = toInt(arguments.get(6));
        }
        if (arguments.size() >= 8) {
            proxy = toString(arguments.get(7));
        }
        
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(postData != null ? "POST" : "GET");
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setDoInput(true);
            if (postData != null) {
                conn.setDoOutput(true);
            }
            
            conn.setRequestProperty("Accept-Charset", encoding);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + encoding);
            
            if (cookie != null) {
                conn.setRequestProperty("Cookie", cookie);
            } else if (autoCookie) {
                String savedCookie = getCookieManager().getCookiesForDomain(url.getHost());
                if (savedCookie != null && !savedCookie.isEmpty()) {
                    conn.setRequestProperty("Cookie", savedCookie);
                }
            }
            
            if (headers != null) {
                String[] headerPairs = headers.split("\\|\\|");
                for (String pair : headerPairs) {
                    int eqIdx = pair.indexOf('=');
                    if (eqIdx > 0) {
                        String key = pair.substring(0, eqIdx).trim();
                        String value = pair.substring(eqIdx + 1).trim();
                        conn.setRequestProperty(key, value);
                    }
                }
            }
            
            if (postData != null) {
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes(StandardCharsets.UTF_8));
                }
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                if (autoCookie) {
                    Map<String, List<String>> headerFields = conn.getHeaderFields();
                    List<String> cookies = headerFields.get("Set-Cookie");
                    if (cookies != null) {
                        getCookieManager().setCookies(url.getHost(), cookies);
                    }
                }
                
                try (InputStream is = conn.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is, encoding))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line).append("\n");
                    }
                    return response.toString().trim();
                }
            } else {
                throw new FunctionException("HTTP error: " + responseCode);
            }
        } catch (Exception e) {
            throw new FunctionException("Failed to fetch URL: " + e.getMessage());
        }
    }
    
    private SimpleCookieManager getCookieManager() {
        return SimpleCookieManager.getInstance();
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
    }
    
    private boolean toBoolean(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        return Boolean.parseBoolean(value.toString());
    }
    
    private int toInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    @Override
    public boolean isSupported() {
        return true;
    }
    
    @Override
    public String getUnsupportedReason() {
        return null;
    }
}
