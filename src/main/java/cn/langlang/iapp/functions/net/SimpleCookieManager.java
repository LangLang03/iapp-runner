package cn.langlang.iapp.functions.net;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleCookieManager {
    private static final SimpleCookieManager INSTANCE = new SimpleCookieManager();
    private final Map<String, Map<String, String>> cookiesByDomain = new ConcurrentHashMap<>();
    
    private SimpleCookieManager() {}
    
    public static SimpleCookieManager getInstance() {
        return INSTANCE;
    }
    
    public void setCookies(String domain, List<String> cookieHeaders) {
        Map<String, String> domainCookies = cookiesByDomain.computeIfAbsent(domain, k -> new HashMap<>());
        for (String header : cookieHeaders) {
            String[] parts = header.split(";")[0].split("=", 2);
            if (parts.length == 2) {
                domainCookies.put(parts[0].trim(), parts[1].trim());
            }
        }
    }
    
    public String getCookiesForDomain(String domain) {
        Map<String, String> domainCookies = cookiesByDomain.get(domain);
        if (domainCookies == null || domainCookies.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : domainCookies.entrySet()) {
            if (sb.length() > 0) sb.append("; ");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }
    
    public String getAllCookies() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Map<String, String>> domainEntry : cookiesByDomain.entrySet()) {
            sb.append("Domain: ").append(domainEntry.getKey()).append("\n");
            for (Map.Entry<String, String> cookie : domainEntry.getValue().entrySet()) {
                sb.append("  ").append(cookie.getKey()).append("=").append(cookie.getValue()).append("\n");
            }
        }
        return sb.toString();
    }
    
    public void clear() {
        cookiesByDomain.clear();
    }
}
