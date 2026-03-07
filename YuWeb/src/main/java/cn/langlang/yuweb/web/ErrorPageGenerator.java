package cn.langlang.yuweb.web;

import cn.langlang.yuweb.YuWebConfig;
import io.javalin.http.Context;

public class ErrorPageGenerator {
    
    private final YuWebConfig config;
    
    public ErrorPageGenerator(YuWebConfig config) {
        this.config = config;
    }
    
    public void sendError(Context ctx, int statusCode, String message) {
        String errorTitle = getStatusTitle(statusCode);
        
        if (config.isDebugMode()) {
            sendDebugError(ctx, statusCode, errorTitle, message);
        } else {
            sendNginxStyleError(ctx, statusCode, errorTitle);
        }
    }
    
    public void sendNotFound(Context ctx, String path) {
        if (config.isDebugMode()) {
            sendDebugError(ctx, 404, "Not Found", "Path: " + path);
        } else {
            sendNginxStyleError(ctx, 404, "Not Found");
        }
    }
    
    public void sendServerError(Context ctx, String errorMessage, Exception e) {
        if (config.isDebugMode()) {
            String debugMessage = errorMessage;
            if (e != null) {
                debugMessage += "\n\nException: " + e.getClass().getName();
                debugMessage += "\nMessage: " + e.getMessage();
                debugMessage += "\n\nStack Trace:\n" + getStackTraceString(e);
            }
            sendDebugError(ctx, 500, "Internal Server Error", debugMessage);
        } else {
            sendNginxStyleError(ctx, 500, "Internal Server Error");
        }
    }
    
    private void sendNginxStyleError(Context ctx, int statusCode, String errorTitle) {
        String html = "<html>\r\n" +
                "<head><title>" + statusCode + " " + errorTitle + "</title></head>\r\n" +
                "<body>\r\n" +
                "<center><h1>" + statusCode + " " + errorTitle + "</h1></center>\r\n" +
                "<hr><center>" + escapeHtml(config.getServerSignature()) + "</center>\r\n" +
                "</body>\r\n" +
                "</html>\r\n";
        
        ctx.status(statusCode);
        ctx.contentType("text/html; charset=utf-8");
        ctx.result(html);
    }
    
    private void sendDebugError(Context ctx, int statusCode, String errorTitle, String debugMessage) {
        String html = "<html>\r\n" +
                "<head><title>" + statusCode + " " + errorTitle + "</title></head>\r\n" +
                "<body>\r\n" +
                "<center><h1>" + statusCode + " " + errorTitle + "</h1></center>\r\n" +
                "<hr><center>" + escapeHtml(config.getServerSignature()) + " (Debug Mode)</center>\r\n" +
                "<pre style=\"background:#f5f5f5;padding:15px;margin:20px;border:1px solid #ddd;overflow:auto;\">" + 
                escapeHtml(debugMessage != null ? debugMessage : "No debug information") + "</pre>\r\n" +
                "</body>\r\n" +
                "</html>\r\n";
        
        ctx.status(statusCode);
        ctx.contentType("text/html; charset=utf-8");
        ctx.result(html);
    }
    
    private String getStatusTitle(int statusCode) {
        return switch (statusCode) {
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            case 408 -> "Request Timeout";
            case 429 -> "Too Many Requests";
            case 500 -> "Internal Server Error";
            case 501 -> "Not Implemented";
            case 502 -> "Bad Gateway";
            case 503 -> "Service Unavailable";
            case 504 -> "Gateway Timeout";
            default -> "Error";
        };
    }
    
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
    
    private String getStackTraceString(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("    at ").append(element.toString()).append("\n");
            if (sb.length() > 5000) {
                sb.append("    ...(truncated)\n");
                break;
            }
        }
        return sb.toString();
    }
}
