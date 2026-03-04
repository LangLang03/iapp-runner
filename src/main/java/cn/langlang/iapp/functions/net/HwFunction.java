package cn.langlang.iapp.functions.net;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;

public class HwFunction implements IFunction {
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
        return 3;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String url = toString(arguments.get(0));
        String titleColor = null;
        String bottomColor = null;
        
        if (arguments.size() >= 2) {
            titleColor = toString(arguments.get(1));
        }
        if (arguments.size() >= 3) {
            bottomColor = toString(arguments.get(2));
        }
        
        try {
            String os = System.getProperty("os.name").toLowerCase();
            
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                return true;
            } else if (os.contains("win")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
                return true;
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec("open " + url);
                return true;
            } else if (os.contains("nix") || os.contains("nux")) {
                Runtime.getRuntime().exec("xdg-open " + url);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            throw new FunctionException("Failed to open browser: " + e.getMessage());
        }
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
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
