package cn.langlang.iapp.lsp.header;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HeaderLoader {
    private static final Logger logger = LoggerFactory.getLogger(HeaderLoader.class);
    
    private final HeaderParser parser;
    private final Map<String, HeaderFile> headerFiles;
    private final Map<String, HeaderFunctionInfo> functionCache;
    private final List<HeaderFile.SnippetInfo> snippetCache;
    
    public HeaderLoader() {
        this.parser = new HeaderParser();
        this.headerFiles = new ConcurrentHashMap<>();
        this.functionCache = new ConcurrentHashMap<>();
        this.snippetCache = Collections.synchronizedList(new ArrayList<>());
    }
    
    public void loadDirectory(String directoryPath) {
        if (directoryPath == null || directoryPath.isEmpty()) {
            return;
        }
        
        Path dir = Paths.get(directoryPath);
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            logger.warn("Header directory does not exist or is not a directory: {}", directoryPath);
            return;
        }
        
        try {
            Files.walk(dir)
                .filter(p -> p.toString().endsWith(".iapph"))
                .forEach(this::loadFile);
        } catch (IOException e) {
            logger.error("Error walking header directory: {}", directoryPath, e);
        }
    }
    
    public void loadDirectories(List<String> directoryPaths) {
        if (directoryPaths == null || directoryPaths.isEmpty()) {
            return;
        }
        
        for (String path : directoryPaths) {
            loadDirectory(path);
        }
    }
    
    public void loadFile(Path filePath) {
        if (filePath == null || !Files.exists(filePath)) {
            return;
        }
        
        try (Reader reader = new InputStreamReader(new FileInputStream(filePath.toFile()), StandardCharsets.UTF_8)) {
            HeaderFile headerFile = parser.parse(filePath.toString(), reader);
            
            if (headerFile != null) {
                headerFiles.put(filePath.toString(), headerFile);
                
                for (HeaderFunctionInfo func : headerFile.getFunctions()) {
                    if (func.getName() != null) {
                        functionCache.put(func.getName().toLowerCase(), func);
                    }
                }
                
                snippetCache.addAll(headerFile.getSnippets());
                
                logger.debug("Loaded header file: {} with {} functions and {} snippets",
                    filePath, headerFile.getFunctionCount(), headerFile.getSnippetCount());
            }
        } catch (IOException e) {
            logger.error("Error loading header file: {}", filePath, e);
        }
    }
    
    public void loadFromClasspath(String resourcePath) {
        if (resourcePath == null || resourcePath.isEmpty()) {
            return;
        }
        
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (is == null) {
                logger.warn("Header resource not found on classpath: {}", resourcePath);
                return;
            }
            
            try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                HeaderFile headerFile = parser.parse(resourcePath, reader);
                
                if (headerFile != null) {
                    headerFiles.put(resourcePath, headerFile);
                    
                    for (HeaderFunctionInfo func : headerFile.getFunctions()) {
                        if (func.getName() != null) {
                            functionCache.put(func.getName().toLowerCase(), func);
                        }
                    }
                    
                    snippetCache.addAll(headerFile.getSnippets());
                    
                    logger.debug("Loaded header from classpath: {} with {} functions",
                        resourcePath, headerFile.getFunctionCount());
                }
            }
        } catch (IOException e) {
            logger.error("Error loading header from classpath: {}", resourcePath, e);
        }
    }
    
    public void loadFromClasspathDirectory(String directoryPath) {
        if (directoryPath == null || directoryPath.isEmpty()) {
            return;
        }
        
        try {
            var resourceUrl = getClass().getClassLoader().getResource(directoryPath);
            if (resourceUrl == null) {
                logger.debug("Header directory not found on classpath: {}", directoryPath);
                return;
            }
            
            if (resourceUrl.getProtocol().equals("file")) {
                Path dirPath = Paths.get(resourceUrl.toURI());
                loadDirectory(dirPath.toString());
            } else if (resourceUrl.getProtocol().equals("jar")) {
                loadFromJarDirectory(directoryPath);
            }
        } catch (Exception e) {
            logger.error("Error loading headers from classpath directory: {}", directoryPath, e);
        }
    }
    
    private void loadFromJarDirectory(String directoryPath) {
        String jarPath = directoryPath;
        if (!jarPath.endsWith("/")) {
            jarPath = jarPath + "/";
        }
        
        try {
            java.net.URL jarUrl = getClass().getProtectionDomain()
                .getCodeSource().getLocation();
            
            if (jarUrl != null && "file".equals(jarUrl.getProtocol())) {
                String path = jarUrl.getPath();
                if (path.endsWith(".jar")) {
                    try (java.util.jar.JarFile jarFile = new java.util.jar.JarFile(path)) {
                        java.util.Enumeration<java.util.jar.JarEntry> entries = jarFile.entries();
                        
                        while (entries.hasMoreElements()) {
                            java.util.jar.JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            
                            if (name.startsWith(jarPath) && name.endsWith(".iapph")) {
                                loadFromClasspath(name);
                            }
                        }
                    }
                } else {
                    Path dirPath = Paths.get(path, jarPath);
                    if (Files.exists(dirPath)) {
                        loadDirectory(dirPath.toString());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error reading jar directory: {}", directoryPath, e);
        }
    }
    
    public HeaderFunctionInfo getFunction(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return functionCache.get(name.toLowerCase());
    }
    
    public boolean hasFunction(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return functionCache.containsKey(name.toLowerCase());
    }
    
    public Collection<HeaderFunctionInfo> getAllFunctions() {
        return functionCache.values();
    }
    
    public List<HeaderFunctionInfo> getFunctionsByPrefix(String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        
        String lowerPrefix = prefix.toLowerCase();
        List<HeaderFunctionInfo> result = new ArrayList<>();
        
        for (HeaderFunctionInfo func : functionCache.values()) {
            if (func.getName().toLowerCase().startsWith(lowerPrefix)) {
                result.add(func);
            }
        }
        
        return result;
    }
    
    public List<HeaderFunctionInfo> getYuWebFunctions() {
        List<HeaderFunctionInfo> result = new ArrayList<>();
        
        for (HeaderFunctionInfo func : functionCache.values()) {
            if (func.isYuWeb()) {
                result.add(func);
            }
        }
        
        return result;
    }
    
    public List<HeaderFunctionInfo> getCoreFunctions() {
        List<HeaderFunctionInfo> result = new ArrayList<>();
        
        for (HeaderFunctionInfo func : functionCache.values()) {
            if (!func.isYuWeb()) {
                result.add(func);
            }
        }
        
        return result;
    }
    
    public List<HeaderFile.SnippetInfo> getAllSnippets() {
        return new ArrayList<>(snippetCache);
    }
    
    public List<HeaderFile.SnippetInfo> getSnippetsByPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return getAllSnippets();
        }
        
        String lowerPrefix = prefix.toLowerCase();
        List<HeaderFile.SnippetInfo> result = new ArrayList<>();
        
        for (HeaderFile.SnippetInfo snippet : snippetCache) {
            if (snippet.getPrefix() != null && snippet.getPrefix().toLowerCase().startsWith(lowerPrefix)) {
                result.add(snippet);
            }
        }
        
        return result;
    }
    
    public void clear() {
        headerFiles.clear();
        functionCache.clear();
        snippetCache.clear();
    }
    
    public int getFunctionCount() {
        return functionCache.size();
    }
    
    public int getSnippetCount() {
        return snippetCache.size();
    }
    
    public int getHeaderFileCount() {
        return headerFiles.size();
    }
}
