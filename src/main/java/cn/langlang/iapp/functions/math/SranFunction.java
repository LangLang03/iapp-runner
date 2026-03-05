package cn.langlang.iapp.functions.math;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SranFunction extends AbstractFunction {
    private final Random random;
    
    public SranFunction() {
        this.random = new Random();
    }
    
    @Override
    public String getName() {
        return "sran";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return 3;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) {
        int min = toInt(arguments.get(0));
        int max = toInt(arguments.get(1));
        
        if (min > max) {
            int temp = min;
            min = max;
            max = temp;
        }
        
        return random.nextInt(max - min + 1) + min;
    }
    
    private int toInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return Arrays.asList(ParamType.INT, ParamType.INT, ParamType.OUTPUT);
    }
}
