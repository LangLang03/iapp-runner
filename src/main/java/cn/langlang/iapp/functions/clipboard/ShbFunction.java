package cn.langlang.iapp.functions.clipboard;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class ShbFunction extends AbstractFunction {
    private static final boolean IS_ANDROID;
    
    static {
        boolean isAndroid = false;
        try {
            Class.forName("android.os.Build");
            isAndroid = true;
        } catch (ClassNotFoundException e) {
        }
        IS_ANDROID = isAndroid;
    }
    
    @Override
    public String getName() {
        return "shb";
    }
    
    @Override
    public int getMinParameters() {
        return 0;
    }
    
    @Override
    public int getMaxParameters() {
        return 0;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) {
        if (IS_ANDROID) {
            return "";
        }
        
        try {
            Class<?> toolkitClass = Class.forName("java.awt.Toolkit");
            Object toolkit = toolkitClass.getMethod("getDefaultToolkit").invoke(null);
            
            Class<?> clipboardClass = Class.forName("java.awt.datatransfer.Clipboard");
            Object clipboard = toolkitClass.getMethod("getSystemClipboard").invoke(toolkit);
            
            Class<?> dataFlavorClass = Class.forName("java.awt.datatransfer.DataFlavor");
            Object stringFlavor = dataFlavorClass.getField("stringFlavor").get(null);
            
            Object data = clipboardClass.getMethod("getData", dataFlavorClass).invoke(clipboard, stringFlavor);
            return data != null ? data.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types();
    }
}
