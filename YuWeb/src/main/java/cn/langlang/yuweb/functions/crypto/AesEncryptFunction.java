package cn.langlang.yuweb.functions.crypto;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

public class AesEncryptFunction extends AbstractFunction {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String AES = "AES";
    private static final int IV_LENGTH = 16;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
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
            byte[] keyBytes = padKey(key.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, AES);
            
            byte[] ivBytes = new byte[IV_LENGTH];
            SECURE_RANDOM.nextBytes(ivBytes);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            byte[] combined = new byte[IV_LENGTH + encrypted.length];
            System.arraycopy(ivBytes, 0, combined, 0, IV_LENGTH);
            System.arraycopy(encrypted, 0, combined, IV_LENGTH, encrypted.length);
            
            return Base64.getEncoder().encodeToString(combined);
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
