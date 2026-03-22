package cn.langlang.yuweb.functions.crypto;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class AesEncryptFunction extends AbstractFunction {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String AES = "AES";
    
    @Override
    public String getName() {
        return "aesEncrypt";
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
        String data = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String key = arguments.get(1) != null ? arguments.get(1).toString() : "";
        
        try {
            // Ensure key is 16 bytes (128-bit AES)
            byte[] keyBytes = padKey(key.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, AES);
            
            // Use zero IV for simplicity (in production, use random IV)
            IvParameterSpec iv = new IvParameterSpec(new byte[16]);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new FunctionException("AES encryption failed: " + e.getMessage());
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING);
    }
    
    private byte[] padKey(byte[] key) {
        byte[] result = new byte[16];
        int len = Math.min(key.length, 16);
        System.arraycopy(key, 0, result, 0, len);
        return result;
    }
}
