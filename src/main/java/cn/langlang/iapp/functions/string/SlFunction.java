package cn.langlang.iapp.functions.string;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.List;
import java.util.regex.Pattern;

public class SlFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "sl";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return 4;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String source = toString(arguments.get(0));
        String delimiter = toString(arguments.get(1));
        
        boolean useRegex = false;
        if (arguments.size() > 2 && arguments.get(2) instanceof Boolean) {
            useRegex = (Boolean) arguments.get(2);
        }
        
        if (useRegex) {
            return source.split(delimiter);
        } else {
            return source.split(Pattern.quote(delimiter));
        }
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.OUTPUT);
    }
    
    @Override
    public List<List<ParamType>> getParamTypeLists() {
        return typeLists(
            types(ParamType.STRING, ParamType.STRING, ParamType.OUTPUT),
            types(ParamType.STRING, ParamType.STRING, ParamType.BOOLEAN, ParamType.OUTPUT)
        );
    }
}
