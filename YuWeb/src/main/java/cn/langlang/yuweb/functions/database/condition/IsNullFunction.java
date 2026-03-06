package cn.langlang.yuweb.functions.database.condition;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.database.QueryCondition;

import java.util.List;

public class IsNullFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "isnull";
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
        String field = arguments.get(0) != null ? arguments.get(0).toString() : "";
        return QueryCondition.isNull(field);
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING);
    }
}
