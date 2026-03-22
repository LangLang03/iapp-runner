package cn.langlang.yuweb.functions.mail;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.mail.MailManager;

import java.util.List;

public class MailConfigFunction extends AbstractFunction {
    
    @Override
    public String getName() {
        return "mailConfig";
    }
    
    @Override
    public int getMinParameters() {
        return 4;
    }
    
    @Override
    public int getMaxParameters() {
        return 5;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String host = arguments.get(0) != null ? arguments.get(0).toString() : "";
        int port = toInt(arguments.get(1), 587);
        String username = arguments.get(2) != null ? arguments.get(2).toString() : "";
        String password = arguments.get(3) != null ? arguments.get(3).toString() : "";
        boolean ssl = false;
        
        if (arguments.size() > 4 && arguments.get(4) != null) {
            Object sslArg = arguments.get(4);
            if (sslArg instanceof Boolean) {
                ssl = (Boolean) sslArg;
            } else {
                ssl = Boolean.parseBoolean(sslArg.toString());
            }
        }
        
        MailManager.getInstance().configure(host, port, username, password, ssl);
        return true;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.LONG, ParamType.STRING, ParamType.STRING, ParamType.BOOLEAN);
    }
    
    private int toInt(Object obj, int defaultValue) {
        if (obj == null) return defaultValue;
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        try {
            return Integer.parseInt(obj.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
