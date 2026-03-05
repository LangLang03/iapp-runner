package cn.langlang.iapp.functions.clipboard;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.util.List;

public class ShbFunction extends AbstractFunction {
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
        return 1;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Object data = clipboard.getData(DataFlavor.stringFlavor);
            return data != null ? data.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OUTPUT);
    }
}
