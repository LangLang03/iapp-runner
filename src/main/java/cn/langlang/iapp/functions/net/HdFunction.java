package cn.langlang.iapp.functions.net;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HdFunction implements IFunction {
    @Override
    public String getName() {
        return "hd";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return 9;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String urlStr = toString(arguments.get(0));
        String savePath = toString(arguments.get(1));
        savePath = context.resolvePath(savePath);
        
        boolean overwrite = false;
        String postData = null;
        String encoding = "UTF-8";
        String cookie = null;
        boolean autoCookie = false;
        String headers = null;
        
        if (arguments.size() >= 3) {
            overwrite = toBoolean(arguments.get(2));
        }
        if (arguments.size() >= 4) {
            postData = toString(arguments.get(3));
        }
        if (arguments.size() >= 5) {
            encoding = toString(arguments.get(4));
        }
        if (arguments.size() >= 6) {
            cookie = toString(arguments.get(5));
            if ("null".equals(cookie)) cookie = null;
        }
        if (arguments.size() >= 7) {
            autoCookie = toBoolean(arguments.get(6));
        }
        if (arguments.size() >= 8) {
            headers = toString(arguments.get(7));
            if ("null".equals(headers)) headers = null;
        }
        
        File saveFile = new File(savePath);
        if (saveFile.exists() && !overwrite) {
            return 1L;
        }
        
        saveFile.getParentFile().mkdirs();
        
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(postData != null ? "POST" : "GET");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(60000);
            conn.setDoInput(true);
            if (postData != null) {
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + encoding);
            }
            
            if (cookie != null) {
                conn.setRequestProperty("Cookie", cookie);
            } else if (autoCookie) {
                String savedCookie = SimpleCookieManager.getInstance().getCookiesForDomain(url.getHost());
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
                try (InputStream is = conn.getInputStream();
                     FileOutputStream fos = new FileOutputStream(saveFile)) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                return 0L;
            } else {
                return -1L;
            }
        } catch (Exception e) {
            return -1L;
        }
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
    }
    
    private boolean toBoolean(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        return Boolean.parseBoolean(value.toString());
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
