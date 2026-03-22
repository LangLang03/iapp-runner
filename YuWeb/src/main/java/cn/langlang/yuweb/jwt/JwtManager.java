package cn.langlang.yuweb.jwt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class JwtManager {
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final Gson gson = new GsonBuilder().create();
    
    public static String encode(Map<String, Object> payload, String secret) {
        // Header
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");
        
        String headerJson = gson.toJson(header);
        String payloadJson = gson.toJson(payload);
        
        String encodedHeader = base64UrlEncode(headerJson);
        String encodedPayload = base64UrlEncode(payloadJson);
        
        String signature = sign(encodedHeader + "." + encodedPayload, secret);
        
        return encodedHeader + "." + encodedPayload + "." + signature;
    }
    
    public static Map<String, Object> decode(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return null;
        }
        
        try {
            String payloadJson = base64UrlDecode(parts[1]);
            return gson.fromJson(payloadJson, new TypeToken<Map<String, Object>>(){}.getType());
        } catch (Exception e) {
            return null;
        }
    }
    
    public static Map<String, Object> verify(String token, String secret) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return null;
        }
        
        try {
            // Verify signature
            String expectedSignature = sign(parts[0] + "." + parts[1], secret);
            if (!expectedSignature.equals(parts[2])) {
                return null;
            }
            
            // Decode payload
            String payloadJson = base64UrlDecode(parts[1]);
            Map<String, Object> payload = gson.fromJson(payloadJson, new TypeToken<Map<String, Object>>(){}.getType());
            
            // Check expiration
            if (payload != null && payload.containsKey("exp")) {
                Object expObj = payload.get("exp");
                long exp = 0;
                if (expObj instanceof Number) {
                    exp = ((Number) expObj).longValue();
                } else if (expObj instanceof String) {
                    exp = Long.parseLong((String) expObj);
                }
                if (exp > 0 && System.currentTimeMillis() / 1000 > exp) {
                    return null; // Token expired
                }
            }
            
            return payload;
        } catch (Exception e) {
            return null;
        }
    }
    
    private static String sign(String data, String secret) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(secretKey);
            byte[] hmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(hmac);
        } catch (Exception e) {
            throw new RuntimeException("JWT signing failed", e);
        }
    }
    
    private static String base64UrlEncode(String data) {
        return base64UrlEncode(data.getBytes(StandardCharsets.UTF_8));
    }
    
    private static String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }
    
    private static String base64UrlDecode(String data) {
        return new String(Base64.getUrlDecoder().decode(data), StandardCharsets.UTF_8);
    }
}
