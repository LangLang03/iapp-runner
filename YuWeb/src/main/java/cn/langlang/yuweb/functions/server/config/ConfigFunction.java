package cn.langlang.yuweb.functions.server.config;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.server.YuWebServer;
import cn.langlang.yuweb.web.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ConfigFunction extends AbstractFunction {
    private static final Logger logger = LoggerFactory.getLogger(ConfigFunction.class);
    
    private final YuWebServer server;
    
    public ConfigFunction(YuWebServer server) {
        this.server = server;
    }
    
    @Override
    public String getName() {
        return "config";
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
        if (server == null) {
            throw new FunctionException("服务器实例不可用");
        }
        
        String key = arguments.get(0) != null ? arguments.get(0).toString().toLowerCase() : "";
        Object value = arguments.get(1);
        
        switch (key) {
            case "port":
                return configurePort(value);
            case "debug":
                return configureDebug(value);
            case "safe":
                return configureSafe(value);
            case "preload":
                return configurePreload(value);
            case "static":
                return configureStatic(value);
            case "poolsize":
                return configurePoolSize(value);
            case "poolinit":
                return configurePoolInit(value);
            case "pooltimeout":
                return configurePoolTimeout(value);
            case "usepool":
                return configureUsePool(value);
            case "asyncpool":
                return configureAsyncPool(value);
            case "asynctimeout":
                return configureAsyncTimeout(value);
            case "http2":
                return configureHttp2(value);
            case "compression":
                return configureCompression(value);
            case "compression_min":
                return configureCompressionMin(value);
            case "max_upload_size":
                return configureMaxUploadSize(value);
            default:
                throw new FunctionException("未知的配置项: " + key + 
                    "。可用配置: port, debug, safe, preload, static, poolsize, poolinit, pooltimeout, usepool, asyncpool, asynctimeout, http2, compression, compression_min, max_upload_size");
        }
    }
    
    private boolean configurePort(Object value) {
        int port = toInt(value, 8080);
        if (port < 1 || port > 65535) {
            logger.warn("无效的端口号: {}, 使用默认端口 8080", port);
            port = 8080;
        }
        server.setPort(port);
        logger.info("配置端口: {}", port);
        return true;
    }
    
    private boolean configureDebug(Object value) {
        boolean debug = toBoolean(value, false);
        server.setDebugMode(debug);
        logger.info("配置调试模式: {}", debug);
        return true;
    }
    
    private boolean configureSafe(Object value) {
        boolean safe = toBoolean(value, false);
        server.getConfig().setSafeMode(safe);
        logger.info("配置安全模式: {}", safe);
        return true;
    }
    
    private boolean configurePreload(Object value) {
        boolean preload = toBoolean(value, false);
        server.getConfig().setPreloadScripts(preload);
        logger.info("配置脚本预加载: {}", preload);
        return true;
    }
    
    private boolean configureStatic(Object value) {
        boolean serveStatic = toBoolean(value, true);
        server.getConfig().setServeStaticFiles(serveStatic);
        logger.info("配置静态文件服务: {}", serveStatic);
        return true;
    }
    
    private boolean configurePoolSize(Object value) {
        int size = toInt(value, 100);
        if (size < 1) {
            logger.warn("无效的连接池大小: {}, 使用默认值 100", size);
            size = 100;
        }
        server.getConfig().setMaxPoolSize(size);
        logger.info("配置连接池最大大小: {}", size);
        return true;
    }
    
    private boolean configurePoolInit(Object value) {
        int size = toInt(value, 10);
        if (size < 1) {
            logger.warn("无效的初始连接池大小: {}, 使用默认值 10", size);
            size = 10;
        }
        server.getConfig().setInitialPoolSize(size);
        logger.info("配置连接池初始大小: {}", size);
        return true;
    }
    
    private boolean configurePoolTimeout(Object value) {
        long timeout = toLong(value, 30000);
        if (timeout < 1000) {
            logger.warn("连接超时时间过短: {}ms, 使用默认值 30000ms", timeout);
            timeout = 30000;
        }
        server.getConfig().setConnectionTimeout(timeout);
        logger.info("配置连接超时: {}ms", timeout);
        return true;
    }
    
    private boolean configureUsePool(Object value) {
        boolean usePool = toBoolean(value, true);
        server.getConfig().setUseConnectionPool(usePool);
        logger.info("配置使用连接池: {}", usePool);
        return true;
    }
    
    private boolean configureAsyncPool(Object value) {
        int size = toInt(value, 50);
        if (size < 1) {
            logger.warn("无效的异步线程池大小: {}, 使用默认值 50", size);
            size = 50;
        }
        server.getConfig().setAsyncThreadPoolSize(size);
        logger.info("配置异步线程池大小: {}", size);
        return true;
    }
    
    private boolean configureAsyncTimeout(Object value) {
        int timeout = toInt(value, 30000);
        if (timeout < 1000) {
            logger.warn("异步超时时间过短: {}ms, 使用默认值 30000ms", timeout);
            timeout = 30000;
        }
        server.getConfig().setAsyncTimeout(timeout);
        logger.info("配置异步超时: {}ms", timeout);
        return true;
    }
    
    private boolean configureHttp2(Object value) {
        boolean http2 = toBoolean(value, true);
        server.getConfig().setHttp2Enabled(http2);
        logger.info("配置HTTP/2优化: {}", http2);
        return true;
    }
    
    private boolean configureCompression(Object value) {
        boolean compression = toBoolean(value, true);
        server.getConfig().setCompressionEnabled(compression);
        logger.info("配置响应压缩: {}", compression);
        return true;
    }
    
    private boolean configureCompressionMin(Object value) {
        int size = toInt(value, 1024);
        if (size < 100) {
            logger.warn("最小压缩大小过小: {}bytes, 使用默认值 1024bytes", size);
            size = 1024;
        }
        server.getConfig().setCompressionMinSize(size);
        logger.info("配置最小压缩大小: {}bytes", size);
        return true;
    }
    
    private boolean configureMaxUploadSize(Object value) {
        long size = toLong(value, 10 * 1024 * 1024);
        if (size < 0) {
            size = Long.MAX_VALUE;
        }
        RequestContext.setMaxFileSize(size);
        logger.info("配置最大上传文件大小: {}bytes ({}MB)", size, size / (1024 * 1024));
        return true;
    }
    
    private int toInt(Object value, int defaultValue) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value != null) {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
    
    private long toLong(Object value, long defaultValue) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value != null) {
            try {
                return Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
    
    private boolean toBoolean(Object value, boolean defaultValue) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value != null) {
            String str = value.toString().toLowerCase();
            return "true".equals(str) || "1".equals(str) || "yes".equals(str) || "on".equals(str);
        }
        return defaultValue;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.OBJECT);
    }
}
