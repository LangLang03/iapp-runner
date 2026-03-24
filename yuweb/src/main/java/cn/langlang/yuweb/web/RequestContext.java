package cn.langlang.yuweb.web;

import cn.langlang.yuweb.server.YuWebServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonPrimitive;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequestContext {
    private static final Logger logger = LoggerFactory.getLogger(RequestContext.class);
    private Context ctx;
    private YuWebServer server;
    private Map<String, Object> jsonData;
    private boolean jsonParsed = false;
    private String sessionId;
    private Map<String, String> routeParams;
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(Long.class, new JsonSerializer<Long>() {
            @Override
            public JsonPrimitive serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.toString());
            }
        })
        .registerTypeAdapter(long.class, new JsonSerializer<Long>() {
            @Override
            public JsonPrimitive serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.toString());
            }
        })
        .create();
    
    private static final long DEFAULT_MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    private static long maxFileSize = DEFAULT_MAX_FILE_SIZE;
    private static Set<String> allowedExtensions = null;
    private static boolean allowAllExtensions = true;
    
    public static void setMaxFileSize(long maxSize) {
        maxFileSize = maxSize > 0 ? maxSize : DEFAULT_MAX_FILE_SIZE;
    }
    
    public static void setAllowedExtensions(Set<String> extensions) {
        if (extensions != null && !extensions.isEmpty()) {
            allowedExtensions = new HashSet<>(extensions);
            allowAllExtensions = false;
        } else {
            allowedExtensions = null;
            allowAllExtensions = true;
        }
    }
    
    public static void addAllowedExtension(String extension) {
        if (extension != null && !extension.isEmpty()) {
            if (allowedExtensions == null) {
                allowedExtensions = new HashSet<>();
                allowAllExtensions = false;
            }
            allowedExtensions.add(extension.toLowerCase());
        }
    }
    
    public static boolean isAllowAllExtensions() {
        return allowAllExtensions;
    }
    
    public static Set<String> getAllowedExtensions() {
        return allowedExtensions;
    }
    
    public RequestContext(Context ctx, YuWebServer server) {
        this.ctx = ctx;
        this.server = server;
    }
    
    public String method() {
        return ctx.method().name();
    }
    
    public String get(String name) {
        return ctx.queryParam(name);
    }
    
    public String get(String name, String defaultValue) {
        String value = ctx.queryParam(name);
        return value != null ? value : defaultValue;
    }
    
    public Map<String, String> gets() {
        Map<String, String> params = new HashMap<>();
        ctx.queryParamMap().forEach((key, values) -> {
            if (!values.isEmpty()) {
                params.put(key, values.get(0));
            }
        });
        return params;
    }
    
    public String post(String name) {
        String value = ctx.formParam(name);
        if (value == null) {
            Map<String, Object> json = getJsonBody();
            if (json != null && json.containsKey(name)) {
                Object jsonValue = json.get(name);
                return jsonValue != null ? jsonValue.toString() : null;
            }
        }
        return value;
    }
    
    public String post(String name, String defaultValue) {
        String value = post(name);
        return value != null ? value : defaultValue;
    }
    
    public Map<String, String> posts() {
        Map<String, String> params = new HashMap<>();
        ctx.formParamMap().forEach((key, values) -> {
            if (!values.isEmpty()) {
                params.put(key, values.get(0));
            }
        });
        return params;
    }
    
    public String form(String name) {
        return post(name);
    }
    
    public String form(String name, String defaultValue) {
        return post(name, defaultValue);
    }
    
    public Map<String, String> forms() {
        return posts();
    }
    
    public String body() {
        return ctx.body();
    }
    
    public Map<String, Object> getJsonBody() {
        if (!jsonParsed) {
            jsonParsed = true;
            try {
                String body = ctx.body();
                logger.debug("Request body: {}", body);
                if (body != null && !body.isEmpty()) {
                    jsonData = gson.fromJson(body, new TypeToken<Map<String, Object>>(){}.getType());
                    logger.debug("Parsed JSON: {}", jsonData);
                }
            } catch (Exception e) {
                logger.debug("JSON parse error: {}", e.getMessage());
                jsonData = null;
            }
        }
        return jsonData;
    }
    
    public Map<String, Object> json() {
        return getJsonBody();
    }
    
    public String param(String name) {
        // First check route params
        if (routeParams != null && routeParams.containsKey(name)) {
            return routeParams.get(name);
        }
        return ctx.pathParam(name);
    }
    
    public Map<String, String> params() {
        Map<String, String> allParams = new HashMap<>();
        // Add Javalin path params
        Map<String, String> pathParams = ctx.pathParamMap();
        if (pathParams != null) {
            allParams.putAll(pathParams);
        }
        // Add custom route params
        if (routeParams != null) {
            allParams.putAll(routeParams);
        }
        return allParams;
    }
    
    public void setRouteParams(Map<String, String> params) {
        this.routeParams = params;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String path() {
        return ctx.path();
    }
    
    public String url() {
        return ctx.url();
    }
    
    public String header(String name) {
        return ctx.header(name);
    }
    
    public String clientIp() {
        return ctx.ip();
    }
    
    public String userAgent() {
        return ctx.userAgent();
    }
    
    public boolean isJson() {
        String contentType = ctx.contentType();
        return contentType != null && contentType.contains("application/json");
    }
    
    public boolean isAjax() {
        String xRequestedWith = ctx.header("X-Requested-With");
        return "XMLHttpRequest".equals(xRequestedWith);
    }
    
    public boolean isMobile() {
        String ua = ctx.userAgent();
        if (ua == null) return false;
        ua = ua.toLowerCase();
        return ua.contains("mobile") || ua.contains("android") || ua.contains("iphone");
    }
    
    public String getCookie(String name) {
        return ctx.cookie(name);
    }
    
    public void setCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        ctx.cookie(cookie);
    }
    
    public void delCookie(String name) {
        ctx.removeCookie(name);
    }
    
    public void json(Object data) {
        ctx.contentType("application/json; charset=UTF-8").result(gson.toJson(data));
    }
    
    public void text(String content) {
        ctx.contentType("text/plain; charset=UTF-8").result(content);
    }
    
    public void html(String content) {
        ctx.contentType("text/html; charset=UTF-8").result(content);
    }
    
    public void error(int code, String message) {
        ctx.status(code).contentType("text/plain; charset=UTF-8").result(message);
    }
    
    public void status(int code) {
        ctx.status(code);
    }
    
    public void setHeader(String name, String value) {
        ctx.header(name, value);
    }
    
    public void redirect(String location) {
        ctx.redirect(location);
    }
    
    public void file(String path) {
        try {
            java.io.File file = new java.io.File(path);
            java.io.InputStream is = new java.io.FileInputStream(file);
            ctx.result(is);
        } catch (Exception e) {
            ctx.status(404).result("File not found");
        }
    }
    
    public void download(String path, String filename) {
        try {
            java.io.File file = new java.io.File(path);
            java.io.InputStream is = new java.io.FileInputStream(file);
            ctx.header("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            ctx.result(is);
        } catch (Exception e) {
            ctx.status(404).result("File not found");
        }
    }
    
    public UploadedFile getFile(String name) {
        try {
            io.javalin.http.UploadedFile juf = ctx.uploadedFile(name);
            if (juf == null) {
                return null;
            }
            
            if (!validateUploadedFile(juf.filename(), juf.size())) {
                logger.warn("File upload rejected: {} (size: {})", juf.filename(), juf.size());
                return null;
            }
            
            String safeFilename = sanitizeFilename(juf.filename());
            
            return new UploadedFile(
                name,
                safeFilename,
                juf.contentType(),
                juf.size(),
                juf.content()
            );
        } catch (Exception e) {
            logger.error("Error getting uploaded file: {}", e.getMessage());
            return null;
        }
    }
    
    public Map<String, UploadedFile> getFiles() {
        Map<String, UploadedFile> files = new HashMap<>();
        try {
            List<io.javalin.http.UploadedFile> uploadedFiles = ctx.uploadedFiles();
            if (uploadedFiles != null) {
                for (io.javalin.http.UploadedFile juf : uploadedFiles) {
                    if (juf.filename() != null && !juf.filename().isEmpty()) {
                        if (!validateUploadedFile(juf.filename(), juf.size())) {
                            logger.warn("File upload rejected: {} (size: {})", juf.filename(), juf.size());
                            continue;
                        }
                        
                        String safeFilename = sanitizeFilename(juf.filename());
                        UploadedFile uf = new UploadedFile(
                            safeFilename,
                            safeFilename,
                            juf.contentType(),
                            juf.size(),
                            juf.content()
                        );
                        files.put(safeFilename, uf);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error getting uploaded files: {}", e.getMessage());
        }
        return files;
    }
    
    private boolean validateUploadedFile(String filename, long size) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        
        if (size > maxFileSize) {
            logger.warn("File size exceeds limit: {} > {}", size, maxFileSize);
            return false;
        }
        
        if (!allowAllExtensions && allowedExtensions != null) {
            String extension = getFileExtension(filename);
            if (extension.isEmpty() || !allowedExtensions.contains(extension.toLowerCase())) {
                logger.warn("File extension not allowed: {}", extension);
                return false;
            }
        }
        
        return true;
    }
    
    private String sanitizeFilename(String filename) {
        if (filename == null) {
            return "unknown";
        }
        
        String sanitized = filename.replaceAll("[\\\\/]", "_");
        sanitized = sanitized.replaceAll("[^a-zA-Z0-9._-]", "_");
        sanitized = sanitized.replaceAll("_{2,}", "_");
        
        if (sanitized.length() > 255) {
            String ext = getFileExtension(sanitized);
            String name = sanitized.substring(0, sanitized.length() - ext.length());
            sanitized = name.substring(0, 250 - ext.length()) + ext;
        }
        
        return sanitized;
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex).toLowerCase();
        }
        return "";
    }
    
    public Context getJavalinContext() {
        return ctx;
    }
    
    public YuWebServer getServer() {
        return server;
    }
}
