package cn.langlang.iapp.functions.net;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HdflFunction extends AbstractFunction {
    
    private static final int DEFAULT_CONNECT_TIMEOUT = 30000;
    private static final int DEFAULT_READ_TIMEOUT = 30000;
    
    @Override
    public String getName() {
        return "hdfl";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return 2;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String urlStr = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String filePath = arguments.get(1) != null ? arguments.get(1).toString() : "";
        filePath = context.resolvePath(filePath);
        
        HttpURLConnection conn = null;
        InputStream in = null;
        FileOutputStream out = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
            
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                File file = new File(filePath);
                file.getParentFile().mkdirs();
                
                in = conn.getInputStream();
                out = new FileOutputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new FunctionException("下载失败: " + e.getMessage(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ignored) {
                }
            }
            if (in != null) {
                try {
                    in.close();
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
        return types(ParamType.STRING, ParamType.STRING);
    }
}
