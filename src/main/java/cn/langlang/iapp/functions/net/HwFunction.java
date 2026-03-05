package cn.langlang.iapp.functions.net;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HwFunction extends AbstractFunction {
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
        
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            
            return (long) conn.getResponseCode();
        } catch (Exception e) {
            return 0L;
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING);
    }
}
