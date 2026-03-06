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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LoginFunction extends AbstractFunction {
    private DatabaseManager dbManager;
    private static final Map<String, Map<String, Object>> tokenStore = new ConcurrentHashMap<>();
    
    public LoginFunction(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    @Override
    public String getName() {
        return "login";
    }
    
    @Override
    public int getMinParameters() {
        return 3;
    }
    
    @Override
    public int getMaxParameters() {
        return 3;
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
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> condition = new HashMap<>();
            condition.put("username", username);
            Map<String, Object> user = db.findOne(table, condition);
            
            if (user == null) {
                result.put("success", false);
                result.put("msg", "用户名或密码错误");
                return result;
            }
            
            String storedPassword = user.get("password") != null ? user.get("password").toString() : "";
            
            if (!BCrypt.checkpw(password, storedPassword)) {
                result.put("success", false);
                result.put("msg", "用户名或密码错误");
                return result;
            }
            
            String token = UUID.randomUUID().toString().replace("-", "");
            
            Map<String, Object> tokenData = new HashMap<>();
            tokenData.put("userId", user.get("id"));
            tokenData.put("username", user.get("username"));
            tokenData.put("createdAt", System.currentTimeMillis());
            tokenStore.put(token, tokenData);
            
            user.remove("password");
            
            result.put("success", true);
            result.put("token", token);
            result.put("user", user);
            result.put("msg", "登录成功");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "登录失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.STRING);
    }
    
    public static Map<String, Object> getTokenData(String token) {
        return tokenStore.get(token);
    }
    
    public static void removeToken(String token) {
        tokenStore.remove(token);
    }
}
