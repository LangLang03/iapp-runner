package cn.langlang.yuweb.cors;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CorsConfig {
    private Set<String> allowedOrigins = new HashSet<>();
    private Set<String> allowedMethods = new HashSet<>();
    private Set<String> allowedHeaders = new HashSet<>();
    private Set<String> exposedHeaders = new HashSet<>();
    private boolean allowCredentials = false;
    private long maxAge = 3600;
    private boolean enabled = false;
    
    public CorsConfig() {
        // Default values
        allowedMethods.add("GET");
        allowedMethods.add("POST");
        allowedMethods.add("PUT");
        allowedMethods.add("DELETE");
        allowedMethods.add("OPTIONS");
        allowedMethods.add("PATCH");
        
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("Authorization");
        allowedHeaders.add("X-Requested-With");
        allowedHeaders.add("Accept");
        allowedHeaders.add("Origin");
    }
    
    public void setAllowedOrigins(List<String> origins) {
        this.allowedOrigins.clear();
        if (origins != null) {
            this.allowedOrigins.addAll(origins);
        }
    }
    
    public void addAllowedOrigin(String origin) {
        if (origin != null && !origin.isEmpty()) {
            this.allowedOrigins.add(origin);
        }
    }
    
    public void setAllowedMethods(List<String> methods) {
        this.allowedMethods.clear();
        if (methods != null) {
            for (String method : methods) {
                this.allowedMethods.add(method.toUpperCase());
            }
        }
    }
    
    public void addAllowedMethod(String method) {
        if (method != null && !method.isEmpty()) {
            this.allowedMethods.add(method.toUpperCase());
        }
    }
    
    public void setAllowedHeaders(List<String> headers) {
        this.allowedHeaders.clear();
        if (headers != null) {
            this.allowedHeaders.addAll(headers);
        }
    }
    
    public void addAllowedHeader(String header) {
        if (header != null && !header.isEmpty()) {
            this.allowedHeaders.add(header);
        }
    }
    
    public void setExposedHeaders(List<String> headers) {
        this.exposedHeaders.clear();
        if (headers != null) {
            this.exposedHeaders.addAll(headers);
        }
    }
    
    public void addExposedHeader(String header) {
        if (header != null && !header.isEmpty()) {
            this.exposedHeaders.add(header);
        }
    }
    
    public void setAllowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }
    
    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge > 0 ? maxAge : 3600;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public boolean isOriginAllowed(String origin) {
        if (allowedOrigins.isEmpty()) {
            return true; // Allow all origins if none specified
        }
        return allowedOrigins.contains("*") || allowedOrigins.contains(origin);
    }
    
    public String getAllowedOriginsHeader(String requestOrigin) {
        if (allowedOrigins.contains("*")) {
            return "*";
        }
        if (allowedOrigins.isEmpty() && requestOrigin != null) {
            return requestOrigin;
        }
        if (requestOrigin != null && allowedOrigins.contains(requestOrigin)) {
            return requestOrigin;
        }
        return String.join(", ", allowedOrigins);
    }
    
    public String getAllowedMethodsHeader() {
        return String.join(", ", allowedMethods);
    }
    
    public String getAllowedHeadersHeader() {
        return String.join(", ", allowedHeaders);
    }
    
    public String getExposedHeadersHeader() {
        return String.join(", ", exposedHeaders);
    }
    
    public boolean isAllowCredentials() {
        return allowCredentials;
    }
    
    public long getMaxAge() {
        return maxAge;
    }
    
    public Set<String> getAllowedOrigins() {
        return allowedOrigins;
    }
    
    public Set<String> getAllowedMethods() {
        return allowedMethods;
    }
    
    public Set<String> getAllowedHeaders() {
        return allowedHeaders;
    }
    
    public Set<String> getExposedHeaders() {
        return exposedHeaders;
    }
}
