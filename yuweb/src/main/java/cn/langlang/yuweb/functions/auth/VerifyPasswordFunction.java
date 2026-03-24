package cn.langlang.yuweb.functions.auth;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class VerifyPasswordFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "verifypassword";
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
        String password = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String hash = arguments.get(1) != null ? arguments.get(1).toString() : "";
        return BCrypt.checkpw(password, hash);
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING);
    }
}
