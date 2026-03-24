package cn.langlang.yuweb.functions.auth;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class HashPasswordFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "hashpassword";
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
        String password = arguments.get(0) != null ? arguments.get(0).toString() : "";
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING);
    }
}
