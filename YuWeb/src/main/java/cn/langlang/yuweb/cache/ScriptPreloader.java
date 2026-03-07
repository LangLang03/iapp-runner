package cn.langlang.yuweb.cache;

import cn.langlang.iapp.ast.Program;
import cn.langlang.iapp.lexer.Lexer;
import cn.langlang.iapp.lexer.LexerException;
import cn.langlang.iapp.lexer.Token;
import cn.langlang.iapp.parser.Parser;
import cn.langlang.iapp.parser.ParserException;
import cn.langlang.iapp.runtime.FunctionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptPreloader {
    private static final Logger logger = LoggerFactory.getLogger(ScriptPreloader.class);
    
    private final Map<String, PreloadedScript> preloadedScripts = new ConcurrentHashMap<>();
    private final Set<String> allowedScriptPaths = ConcurrentHashMap.newKeySet();
    private final FunctionRegistry functionRegistry;
    private final String webrootPath;
    
    private int totalScripts = 0;
    private int successCount = 0;
    private int errorCount = 0;
    private long totalCompileTime = 0;
    
    public ScriptPreloader(FunctionRegistry functionRegistry, String webrootPath) {
        this.functionRegistry = functionRegistry;
        this.webrootPath = webrootPath;
    }
    
    public void preloadAll() {
        logger.info("Starting script preloading from: {}", webrootPath);
        long startTime = System.currentTimeMillis();
        
        File webrootDir = new File(webrootPath);
        if (!webrootDir.exists() || !webrootDir.isDirectory()) {
            logger.warn("Webroot directory not found: {}", webrootPath);
            return;
        }
        
        List<File> iappFiles = new ArrayList<>();
        collectIappFiles(webrootDir, iappFiles);
        
        totalScripts = iappFiles.size();
        logger.info("Found {} .iapp scripts to preload", totalScripts);
        
        for (File file : iappFiles) {
            preloadScript(file);
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        logger.info("Preloading completed: {} scripts loaded, {} errors, took {}ms", 
                successCount, errorCount, elapsed);
    }
    
    private void collectIappFiles(File directory, List<File> files) {
        File[] children = directory.listFiles();
        if (children == null) return;
        
        for (File child : children) {
            if (child.isDirectory()) {
                collectIappFiles(child, files);
            } else if (child.getName().endsWith(".iapp")) {
                files.add(child);
            }
        }
    }
    
    private void preloadScript(File file) {
        String relativePath = getRelativePath(file);
        
        try {
            String source = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            
            long compileStart = System.currentTimeMillis();
            
            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.tokenizeInternal();
            
            Parser parser = new Parser(tokens);
            parser.setFunctionRegistry(functionRegistry);
            Program program = parser.parse();
            
            long compileTime = System.currentTimeMillis() - compileStart;
            totalCompileTime += compileTime;
            
            String sourceHash = hashSource(source);
            
            PreloadedScript preloaded = new PreloadedScript(
                    relativePath,
                    file.getAbsolutePath(),
                    program,
                    sourceHash,
                    file.lastModified(),
                    compileTime
            );
            
            preloadedScripts.put(relativePath, preloaded);
            allowedScriptPaths.add(relativePath);
            successCount++;
            
            logger.debug("Preloaded: {} ({}ms)", relativePath, compileTime);
            
        } catch (IOException e) {
            errorCount++;
            logger.error("Failed to read script {}: {}", relativePath, e.getMessage());
        } catch (LexerException e) {
            errorCount++;
            logger.error("Lexer error in {}: {}", relativePath, e.getMessage());
        } catch (ParserException e) {
            errorCount++;
            logger.error("Parser error in {}: {}", relativePath, e.getMessage());
        }
    }
    
    private String getRelativePath(File file) {
        Path basePath = Paths.get(webrootPath);
        Path filePath = file.toPath();
        Path relative = basePath.relativize(filePath);
        return "/" + relative.toString().replace("\\", "/");
    }
    
    public PreloadedScript getScript(String path) {
        return preloadedScripts.get(path);
    }
    
    public boolean isScriptAllowed(String path) {
        return allowedScriptPaths.contains(path);
    }
    
    public void addAllowedScript(String path) {
        allowedScriptPaths.add(path);
    }
    
    public void removeAllowedScript(String path) {
        allowedScriptPaths.remove(path);
    }
    
    public int getPreloadedCount() {
        return preloadedScripts.size();
    }
    
    public int getTotalScripts() {
        return totalScripts;
    }
    
    public int getSuccessCount() {
        return successCount;
    }
    
    public int getErrorCount() {
        return errorCount;
    }
    
    public long getTotalCompileTime() {
        return totalCompileTime;
    }
    
    public double getAverageCompileTime() {
        return successCount > 0 ? (double) totalCompileTime / successCount : 0;
    }
    
    public Set<String> getPreloadedPaths() {
        return Collections.unmodifiableSet(preloadedScripts.keySet());
    }
    
    public PreloadStats getStats() {
        return new PreloadStats(totalScripts, successCount, errorCount, totalCompileTime, getAverageCompileTime());
    }
    
    private String hashSource(String source) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(source.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(source.hashCode());
        }
    }
    
    public static class PreloadedScript {
        private final String relativePath;
        private final String absolutePath;
        private final Program program;
        private final String sourceHash;
        private final long lastModified;
        private final long compileTime;
        
        public PreloadedScript(String relativePath, String absolutePath, Program program, 
                               String sourceHash, long lastModified, long compileTime) {
            this.relativePath = relativePath;
            this.absolutePath = absolutePath;
            this.program = program;
            this.sourceHash = sourceHash;
            this.lastModified = lastModified;
            this.compileTime = compileTime;
        }
        
        public String getRelativePath() {
            return relativePath;
        }
        
        public String getAbsolutePath() {
            return absolutePath;
        }
        
        public Program getProgram() {
            return program;
        }
        
        public String getSourceHash() {
            return sourceHash;
        }
        
        public long getLastModified() {
            return lastModified;
        }
        
        public long getCompileTime() {
            return compileTime;
        }
    }
    
    public static class PreloadStats {
        private final int totalScripts;
        private final int successCount;
        private final int errorCount;
        private final long totalCompileTime;
        private final double averageCompileTime;
        
        public PreloadStats(int totalScripts, int successCount, int errorCount, 
                           long totalCompileTime, double averageCompileTime) {
            this.totalScripts = totalScripts;
            this.successCount = successCount;
            this.errorCount = errorCount;
            this.totalCompileTime = totalCompileTime;
            this.averageCompileTime = averageCompileTime;
        }
        
        public int getTotalScripts() {
            return totalScripts;
        }
        
        public int getSuccessCount() {
            return successCount;
        }
        
        public int getErrorCount() {
            return errorCount;
        }
        
        public long getTotalCompileTime() {
            return totalCompileTime;
        }
        
        public double getAverageCompileTime() {
            return averageCompileTime;
        }
        
        @Override
        public String toString() {
            return String.format("PreloadStats{total=%d, success=%d, errors=%d, totalTime=%dms, avgTime=%.2fms}",
                    totalScripts, successCount, errorCount, totalCompileTime, averageCompileTime);
        }
    }
}
