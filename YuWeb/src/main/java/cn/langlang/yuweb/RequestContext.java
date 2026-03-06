package cn.langlang.yuweb;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RequestContext {
    private static final Logger logger = LoggerFactory.getLogger(RequestContext.class);
    private Context ctx;
    private YuWebServer server;
    private Map<String, Object> jsonData;
    private boolean jsonParsed = false;
    private static final Gson gson = new Gson();
    
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
        return ctx.pathParam(name);
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
    
    public Context getJavalinContext() {
        return ctx;
    }
    
    public YuWebServer getServer() {
        return server;
    }
}
