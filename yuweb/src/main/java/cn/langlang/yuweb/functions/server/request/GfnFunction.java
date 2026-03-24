package cn.langlang.yuweb.functions.server.request;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.web.UploadedFile;

import java.util.List;

public class GfnFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "gfn";
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
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Object fileObj = arguments.get(0);
        
        if (fileObj == null) {
            return null;
        }
        
        if (fileObj instanceof UploadedFile) {
            return ((UploadedFile) fileObj).getFilename();
        }
        
        return null;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OBJECT);
    }
}
