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

public class AesDecryptFunction extends AbstractFunction {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String AES = "AES";
    private static final int IV_LENGTH = 16;
    
    @Override
    public String getName() {
        return "aesdecrypt";
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
            
            byte[] combined = Base64.getDecoder().decode(data);
            
            if (combined.length < IV_LENGTH) {
                throw new FunctionException("AES decryption failed: invalid encrypted data length");
            }
            
            byte[] ivBytes = new byte[IV_LENGTH];
            System.arraycopy(combined, 0, ivBytes, 0, IV_LENGTH);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            
            byte[] encrypted = new byte[combined.length - IV_LENGTH];
            System.arraycopy(combined, IV_LENGTH, encrypted, 0, encrypted.length);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new FunctionException("AES decryption failed: " + e.getMessage());
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
