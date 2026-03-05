package cn.langlang.iapp.functions.time;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TimeFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "time";
    }
    
    @Override
    public int getMinParameters() {
        return 0;
    }
    
    @Override
    public int getMaxParameters() {
        return 3;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) {
        if (arguments.isEmpty()) {
            return System.currentTimeMillis();
        }
        
        Object firstArg = arguments.get(0);
        
        if (firstArg instanceof Number) {
            int type = ((Number) firstArg).intValue();
            SimpleDateFormat sdf;
            switch (type) {
                case 0:
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return sdf.format(new Date());
                case 1:
                    sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    return sdf.format(new Date());
                case 2:
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    return sdf.format(new Date());
                case 3:
                    sdf = new SimpleDateFormat("HH:mm:ss");
                    return sdf.format(new Date());
                case 4:
                    return System.currentTimeMillis();
                case 5:
                    sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    return sdf.format(new Date());
                default:
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return sdf.format(new Date());
            }
        }
        
        String format = firstArg != null ? firstArg.toString() : "yyyy-MM-dd HH:mm:ss";
        long timestamp = System.currentTimeMillis();
        
        if (arguments.size() > 1 && arguments.get(1) instanceof Number) {
            timestamp = ((Number) arguments.get(1)).longValue();
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(timestamp));
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OBJECT, ParamType.OUTPUT);
    }
    
    @Override
    public List<List<ParamType>> getParamTypeLists() {
        return typeLists(
            types(ParamType.OUTPUT),
            types(ParamType.INT, ParamType.OUTPUT),
            types(ParamType.STRING, ParamType.OUTPUT),
            types(ParamType.STRING, ParamType.LONG, ParamType.OUTPUT)
        );
    }
}
