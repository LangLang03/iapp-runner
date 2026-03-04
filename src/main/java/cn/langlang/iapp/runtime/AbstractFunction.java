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
    
    @Override
    public List<List<ParamType>> getParamTypeLists() {
        List<List<ParamType>> lists = new ArrayList<>();
        lists.add(getParamTypes());
        return lists;
    }
    
    protected List<List<ParamType>> typeLists(List<ParamType>... lists) {
        List<List<ParamType>> result = new ArrayList<>();
        Collections.addAll(result, lists);
        return result;
    }
}
