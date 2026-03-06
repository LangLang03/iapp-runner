package cn.langlang.yuweb.functions.server.config;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.YuWebServer;

import java.util.List;

public class PortFunction extends AbstractFunction {
    private YuWebServer server;
    
    public PortFunction(YuWebServer server) {
        this.server = server;
    }
    
    @Override
    public String getName() {
        return "port";
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
        if (server == null) {
            return null;
        }
        int port = 8080;
        if (arguments.get(0) instanceof Number) {
            port = ((Number) arguments.get(0)).intValue();
        }
        server.setPort(port);
        return null;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.INT);
    }
}
