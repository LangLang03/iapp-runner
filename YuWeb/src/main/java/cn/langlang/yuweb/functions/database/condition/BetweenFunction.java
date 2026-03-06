package cn.langlang.yuweb.functions.database.condition;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.database.QueryCondition;

import java.util.List;

public class BetweenFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "between";
    }
    
    @Override
    public int getMinParameters() {
        return 3;
    }
    
    @Override
    public int getMaxParameters() {
        return 3;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String field = arguments.get(0) != null ? arguments.get(0).toString() : "";
        Object value1 = arguments.get(1);
        Object value2 = arguments.get(2);
        return QueryCondition.between(field, value1, value2);
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.OBJECT, ParamType.OBJECT);
    }
}
