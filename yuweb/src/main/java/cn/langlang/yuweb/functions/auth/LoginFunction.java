package cn.langlang.yuweb.functions.auth;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.database.DatabaseManager;
import cn.langlang.yuweb.database.Database;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LoginFunction extends AbstractFunction {
    private static final Logger logger = LoggerFactory.getLogger(LoginFunction.class);
    
    private DatabaseManager dbManager;
    private static final Map<String, TokenData> tokenStore = new ConcurrentHashMap<>();
    private static final long DEFAULT_TOKEN_EXPIRY_MS = 24 * 60 * 60 * 1000;
    private static volatile long tokenExpiryMs = DEFAULT_TOKEN_EXPIRY_MS;
    
    public LoginFunction(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    public static void setTokenExpiry(long expiryMs) {
        tokenExpiryMs = expiryMs > 0 ? expiryMs : DEFAULT_TOKEN_EXPIRY_MS;
    }
    
    public static long getTokenExpiry() {
        return tokenExpiryMs;
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
            long now = System.currentTimeMillis();
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", user.get("id"));
            userData.put("username", user.get("username"));
            userData.put("createdAt", now);
            
            TokenData tokenData = new TokenData(userData, now + tokenExpiryMs);
            tokenStore.put(token, tokenData);
            
            user.remove("password");
            
            result.put("success", true);
            result.put("token", token);
            result.put("user", user);
            result.put("expiresIn", tokenExpiryMs / 1000);
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
        TokenData data = tokenStore.get(token);
        if (data == null) {
            return null;
        }
        if (data.isExpired()) {
            tokenStore.remove(token);
            logger.debug("Token expired and removed: {}", token);
            return null;
        }
        return data.getData();
    }
    
    public static void removeToken(String token) {
        tokenStore.remove(token);
    }
    
    public static int getActiveTokenCount() {
        cleanExpiredTokens();
        return tokenStore.size();
    }
    
    public static void cleanExpiredTokens() {
        long now = System.currentTimeMillis();
        tokenStore.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    public static void clearAllTokens() {
        tokenStore.clear();
    }
    
    public static class TokenData {
        private final Map<String, Object> data;
        private final long expiresAt;
        
        public TokenData(Map<String, Object> data, long expiresAt) {
            this.data = data;
            this.expiresAt = expiresAt;
        }
        
        public Map<String, Object> getData() {
            return data;
        }
        
        public long getExpiresAt() {
            return expiresAt;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }
}
