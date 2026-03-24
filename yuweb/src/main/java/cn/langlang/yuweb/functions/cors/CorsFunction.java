package cn.langlang.yuweb.functions.cors;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.cors.CorsConfig;

import java.util.List;
import java.util.Map;

public class CorsFunction extends AbstractFunction {
    private static CorsConfig corsConfig = new CorsConfig();
    
    public static CorsConfig getCorsConfig() {
        return corsConfig;
    }
    
    public static void reset() {
        corsConfig = new CorsConfig();
    }
    
    @Override
    public String getName() {
        return "cors";
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
        Object configArg = arguments.get(0);
        
        if (!(configArg instanceof Map)) {
            throw new FunctionException("cors() requires a map configuration");
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) configArg;
        
        // Parse origins
        Object origins = config.get("origins");
        if (origins != null) {
            if (origins instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> originList = (List<String>) origins;
                corsConfig.setAllowedOrigins(originList);
            } else if (origins instanceof String) {
                String originStr = (String) origins;
                if (originStr.contains(",")) {
                    String[] parts = originStr.split(",");
                    for (String part : parts) {
                        corsConfig.addAllowedOrigin(part.trim());
                    }
                } else {
                    corsConfig.addAllowedOrigin(originStr);
                }
            }
        }
        
        // Parse methods
        Object methods = config.get("methods");
        if (methods != null) {
            if (methods instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> methodList = (List<String>) methods;
                corsConfig.setAllowedMethods(methodList);
            } else if (methods instanceof String) {
                String methodStr = (String) methods;
                if (methodStr.contains(",")) {
                    String[] parts = methodStr.split(",");
                    for (String part : parts) {
                        corsConfig.addAllowedMethod(part.trim());
                    }
                } else {
                    corsConfig.addAllowedMethod(methodStr);
                }
            }
        }
        
        // Parse headers
        Object headers = config.get("headers");
        if (headers != null) {
            if (headers instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> headerList = (List<String>) headers;
                corsConfig.setAllowedHeaders(headerList);
            } else if (headers instanceof String) {
                String headerStr = (String) headers;
                if (headerStr.contains(",")) {
                    String[] parts = headerStr.split(",");
                    for (String part : parts) {
                        corsConfig.addAllowedHeader(part.trim());
                    }
                } else {
                    corsConfig.addAllowedHeader(headerStr);
                }
            }
        }
        
        // Parse exposed headers
        Object exposedHeaders = config.get("exposedHeaders");
        if (exposedHeaders != null) {
            if (exposedHeaders instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> headerList = (List<String>) exposedHeaders;
                corsConfig.setExposedHeaders(headerList);
            } else if (exposedHeaders instanceof String) {
                String headerStr = (String) exposedHeaders;
                if (headerStr.contains(",")) {
                    String[] parts = headerStr.split(",");
                    for (String part : parts) {
                        corsConfig.addExposedHeader(part.trim());
                    }
                } else {
                    corsConfig.addExposedHeader(headerStr);
                }
            }
        }
        
        // Parse credentials
        Object credentials = config.get("credentials");
        if (credentials != null) {
            if (credentials instanceof Boolean) {
                corsConfig.setAllowCredentials((Boolean) credentials);
            } else {
                corsConfig.setAllowCredentials(Boolean.parseBoolean(credentials.toString()));
            }
        }
        
        // Parse max age
        Object maxAge = config.get("maxAge");
        if (maxAge != null) {
            if (maxAge instanceof Number) {
                corsConfig.setMaxAge(((Number) maxAge).longValue());
            } else {
                try {
                    corsConfig.setMaxAge(Long.parseLong(maxAge.toString()));
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        }
        
        // Enable CORS
        corsConfig.setEnabled(true);
        
        return true;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OBJECT);
    }
}
