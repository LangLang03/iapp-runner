package cn.langlang.iapp.functions.clipboard;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;

public class SxbFunction extends AbstractFunction {
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
        return "sxb";
    }
    
    @Override
    public int getMinParameters() {
        return 1;
    }
    
    @Override
    public int getMaxParameters() {
        return 1;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) {
        if (IS_ANDROID) {
            return true;
        }
        
        try {
            String content = arguments.get(0) != null ? arguments.get(0).toString() : "";
            
            Class<?> toolkitClass = Class.forName("java.awt.Toolkit");
            Object toolkit = toolkitClass.getMethod("getDefaultToolkit").invoke(null);
            
            Class<?> clipboardClass = Class.forName("java.awt.datatransfer.Clipboard");
            Object clipboard = toolkitClass.getMethod("getSystemClipboard").invoke(toolkit);
            
            Class<?> stringSelectionClass = Class.forName("java.awt.datatransfer.StringSelection");
            Object stringSelection = stringSelectionClass.getConstructor(String.class).newInstance(content);
            
            clipboardClass.getMethod("setContents", 
                Class.forName("java.awt.datatransfer.Transferable"), 
                Class.forName("java.awt.datatransfer.ClipboardOwner"))
                .invoke(clipboard, stringSelection, null);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING);
    }
}
