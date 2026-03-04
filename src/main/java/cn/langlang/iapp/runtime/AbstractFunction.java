package cn.langlang.iapp.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractFunction implements IFunction {
    
    @Override
    public boolean isSupported() {
        return true;
    }
    
    @Override
    public String getUnsupportedReason() {
        return null;
    }
    
    protected List<ParamType> types(ParamType... types) {
        List<ParamType> result = new ArrayList<>();
        Collections.addAll(result, types);
        return result;
    }
}
