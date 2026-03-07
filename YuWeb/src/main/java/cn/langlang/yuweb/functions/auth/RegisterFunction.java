package cn.langlang.yuweb.functions.auth;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.database.DatabaseManager;
import cn.langlang.yuweb.database.Database;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterFunction extends AbstractFunction {
    private DatabaseManager dbManager;
    
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 100;
    
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
        String username = arguments.get(1) != null ? arguments.get(1).toString().trim() : "";
        String password = arguments.get(2) != null ? arguments.get(2).toString() : "";
        Object extraObj = arguments.get(3);
        
        Map<String, Object> result = new HashMap<>();
        
        String validationError = validateInput(table, username, password);
        if (validationError != null) {
            result.put("success", false);
            result.put("msg", validationError);
            return result;
        }
        
        try {
            Map<String, Object> condition = new HashMap<>();
            condition.put("username", username);
            Map<String, Object> existUser = db.findOne(table, condition);
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
    
    private String validateInput(String table, String username, String password) {
        if (table == null || table.trim().isEmpty()) {
            return "表名不能为空";
        }
        
        if (username == null || username.isEmpty()) {
            return "用户名不能为空";
        }
        
        if (username.length() < MIN_USERNAME_LENGTH) {
            return "用户名长度必须至少" + MIN_USERNAME_LENGTH + "个字符";
        }
        
        if (username.length() > MAX_USERNAME_LENGTH) {
            return "用户名长度不能超过" + MAX_USERNAME_LENGTH + "个字符";
        }
        
        if (!username.matches("^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$")) {
            return "用户名只能包含字母、数字、下划线和中文";
        }
        
        if (password == null || password.isEmpty()) {
            return "密码不能为空";
        }
        
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "密码长度必须至少" + MIN_PASSWORD_LENGTH + "个字符";
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            return "密码长度不能超过" + MAX_PASSWORD_LENGTH + "个字符";
        }
        
        return null;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.STRING, ParamType.OBJECT);
    }
}
