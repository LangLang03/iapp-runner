package cn.langlang.yuweb.functions.server.request;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.web.UploadedFile;

import java.util.List;

public class GfsFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "gfs";
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
            return 0L;
        }
        
        if (fileObj instanceof UploadedFile) {
            return ((UploadedFile) fileObj).getSize();
        }
        
        return 0L;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OBJECT);
    }
}
