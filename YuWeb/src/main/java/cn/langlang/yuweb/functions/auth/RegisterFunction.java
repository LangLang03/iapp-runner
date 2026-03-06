package cn.langlang.yuweb.functions.auth;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.DatabaseManager;
import cn.langlang.yuweb.database.Database;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterFunction extends AbstractFunction {
    private DatabaseManager dbManager;
    
    public RegisterFunction(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    @Override
    public String getName() {
        return "register";
    }
    
    @Override
    public int getMinParameters() {
        return 4;
    }
    
    @Override
    public int getMaxParameters() {
        return 4;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        Database db = dbManager.getDefaultDatabase();
        if (db == null) {
            throw new FunctionException("Database not connected");
        }
        
        String table = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String username = arguments.get(1) != null ? arguments.get(1).toString() : "";
        String password = arguments.get(2) != null ? arguments.get(2).toString() : "";
        Object extraObj = arguments.get(3);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> existUser = db.findOne(table, "username = '" + username + "'");
            if (existUser != null) {
                result.put("success", false);
                result.put("msg", "用户名已存在");
                return result;
            }
            
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", username);
            userData.put("password", hashedPassword);
            userData.put("created_at", System.currentTimeMillis());
            
            if (extraObj instanceof Map) {
                Map<String, Object> extra = (Map<String, Object>) extraObj;
                for (Map.Entry<String, Object> entry : extra.entrySet()) {
                    if (!userData.containsKey(entry.getKey())) {
                        userData.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            
            long userId = db.insert(table, userData);
            
            result.put("success", true);
            result.put("userId", userId);
            result.put("msg", "注册成功");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "注册失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.STRING, ParamType.OBJECT);
    }
}
