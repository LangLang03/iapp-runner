package cn.langlang.yuweb.functions.server.config;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.web.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UploadConfigFunction extends AbstractFunction {
    private static final Logger logger = LoggerFactory.getLogger(UploadConfigFunction.class);
    
    @Override
    public String getName() {
        return "upc";
    }
    
    @Override
    public int getMinParameters() {
        return 1;
    }
    
    @Override
    public int getMaxParameters() {
        return 2;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String action = arguments.get(0) != null ? arguments.get(0).toString() : "";
        
        switch (action.toLowerCase()) {
            case "extensions":
                if (arguments.size() < 2) {
                    throw new FunctionException("upc(\"extensions\", [\".jpg\", \".png\", ...]) 需要提供扩展名列表");
                }
                return configureExtensions(arguments.get(1));
                
            case "maxsize":
                if (arguments.size() < 2) {
                    throw new FunctionException("upc(\"maxsize\", 字节数) 需要提供最大文件大小");
                }
                return configureMaxSize(arguments.get(1));
                
            case "add_extensions":
                if (arguments.size() < 2) {
                    throw new FunctionException("upc(\"add_extensions\", [\".exe\", ...]) 需要提供扩展名列表");
                }
                return addExtensions(arguments.get(1));
                
            case "reset":
                RequestContext.setAllowedExtensions(null);
                RequestContext.setMaxFileSize(10 * 1024 * 1024);
                logger.info("Upload config reset to defaults");
                return true;
                
            default:
                throw new FunctionException("未知的配置操作: " + action + "。可用: extensions, maxsize, add_extensions, reset");
        }
    }
    
    private boolean configureExtensions(Object extensionsObj) {
        Set<String> extensions = parseExtensions(extensionsObj);
        if (extensions.isEmpty()) {
            logger.warn("Empty extensions list provided, keeping current config");
            return false;
        }
        RequestContext.setAllowedExtensions(extensions);
        logger.info("Configured allowed upload extensions: {}", extensions);
        return true;
    }
    
    private boolean configureMaxSize(Object sizeObj) {
        long maxSize = 10 * 1024 * 1024;
        if (sizeObj instanceof Number) {
            maxSize = ((Number) sizeObj).longValue();
        } else if (sizeObj != null) {
            try {
                maxSize = Long.parseLong(sizeObj.toString());
            } catch (NumberFormatException e) {
                logger.warn("Invalid max size: {}, using default 10MB", sizeObj);
                return false;
            }
        }
        
        if (maxSize <= 0) {
            maxSize = Long.MAX_VALUE;
            logger.info("Max file size set to unlimited");
        } else {
            logger.info("Max file size set to {} bytes ({} MB)", maxSize, maxSize / (1024 * 1024));
        }
        
        RequestContext.setMaxFileSize(maxSize);
        return true;
    }
    
    private boolean addExtensions(Object extensionsObj) {
        Set<String> newExtensions = parseExtensions(extensionsObj);
        for (String ext : newExtensions) {
            RequestContext.addAllowedExtension(ext);
        }
        logger.info("Added upload extensions: {}", newExtensions);
        return true;
    }
    
    @SuppressWarnings("unchecked")
    private Set<String> parseExtensions(Object extensionsObj) {
        Set<String> extensions = new HashSet<>();
        
        if (extensionsObj instanceof List) {
            for (Object ext : (List<?>) extensionsObj) {
                if (ext != null) {
                    String extStr = ext.toString().trim();
                    if (!extStr.isEmpty()) {
                        if (!extStr.startsWith(".")) {
                            extStr = "." + extStr;
                        }
                        extensions.add(extStr.toLowerCase());
                    }
                }
            }
        } else if (extensionsObj instanceof String) {
            String extStr = ((String) extensionsObj).trim();
            if (!extStr.isEmpty()) {
                if (!extStr.startsWith(".")) {
                    extStr = "." + extStr;
                }
                extensions.add(extStr.toLowerCase());
            }
        }
        
        return extensions;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.OBJECT);
    }
}
