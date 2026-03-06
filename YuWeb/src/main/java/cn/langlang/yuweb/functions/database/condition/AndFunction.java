package cn.langlang.yuweb.functions.database.condition;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.database.QueryCondition;

import java.util.List;

public class AndFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "and";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        return QueryCondition.and(arguments.toArray());
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OBJECT, ParamType.OBJECT);
    }
}
