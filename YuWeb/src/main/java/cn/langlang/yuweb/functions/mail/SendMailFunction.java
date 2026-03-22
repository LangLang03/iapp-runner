package cn.langlang.yuweb.functions.mail;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.mail.MailManager;

import java.util.List;

public class SendMailFunction extends AbstractFunction {
    
    @Override
    public String getName() {
        return "sendMail";
    }
    
    @Override
    public int getMinParameters() {
        return 3;
    }
    
    @Override
    public int getMaxParameters() {
        return 4;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String to = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String subject = arguments.get(1) != null ? arguments.get(1).toString() : "";
        String body = arguments.get(2) != null ? arguments.get(2).toString() : "";
        boolean html = false;
        
        if (arguments.size() > 3 && arguments.get(3) != null) {
            Object htmlArg = arguments.get(3);
            if (htmlArg instanceof Boolean) {
                html = (Boolean) htmlArg;
            } else {
                html = Boolean.parseBoolean(htmlArg.toString());
            }
        }
        
        return MailManager.getInstance().sendMail(to, subject, body, html);
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.STRING, ParamType.BOOLEAN);
    }
}
