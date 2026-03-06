package cn.langlang.yuweb.functions.database.condition;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.database.QueryCondition;

import java.util.List;

public class InFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "in";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return 2;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String field = arguments.get(0) != null ? arguments.get(0).toString() : "";
        Object values = arguments.get(1);
        return QueryCondition.in(field, values);
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.OBJECT);
    }
}
