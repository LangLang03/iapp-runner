package cn.langlang.yuweb.functions.crypto;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated SHA-1 has been proven vulnerable to collision attacks (SHAttered, 2017).
 *             Use SHA-256 (sha256 function) for security-sensitive applications.
 *             This function is kept for backward compatibility only.
 */
@Deprecated
public class Sha1Function extends AbstractFunction {
    private static final Logger LOGGER = LoggerFactory.getLogger(Sha1Function.class);
    private static volatile boolean warningLogged = false;
    
    @Override
    public String getName() {
        return "sha1";
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
        if (!warningLogged) {
            synchronized (Sha1Function.class) {
                if (!warningLogged) {
                    LOGGER.warn("SECURITY WARNING: SHA-1 is deprecated and vulnerable to collision attacks. " +
                                "Use sha256() instead for security-sensitive applications.");
                    warningLogged = true;
                }
            }
        }
        
        String data = arguments.get(0) != null ? arguments.get(0).toString() : "";
        
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new FunctionException("SHA-1 algorithm not available: " + e.getMessage());
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING);
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
