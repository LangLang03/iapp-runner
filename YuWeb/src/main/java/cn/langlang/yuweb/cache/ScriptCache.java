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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptCache {
    private static final Logger logger = LoggerFactory.getLogger(ScriptCache.class);
    
    private final Map<String, CachedScript> cache = new ConcurrentHashMap<>();
    private final Map<String, Long> fileLastModified = new ConcurrentHashMap<>();
    private final FunctionRegistry sharedFunctionRegistry;
    
    private int hitCount = 0;
    private int missCount = 0;
    
    public ScriptCache(FunctionRegistry sharedFunctionRegistry) {
        this.sharedFunctionRegistry = sharedFunctionRegistry;
    }
    
    public CachedScript getOrCompile(String filePath, String source) throws CacheException {
        File file = new File(filePath);
        long lastModified = file.exists() ? file.lastModified() : System.currentTimeMillis();
        
        String cacheKey = generateCacheKey(filePath);
        
        CachedScript cached = cache.get(cacheKey);
        Long cachedLastModified = fileLastModified.get(cacheKey);
        
        if (cached != null && cachedLastModified != null && cachedLastModified >= lastModified) {
            hitCount++;
            logger.debug("Cache hit for script: {}", filePath);
            return cached;
        }
        
        missCount++;
        logger.debug("Cache miss for script: {}, compiling...", filePath);
        
        CachedScript newCached = compile(source);
        cache.put(cacheKey, newCached);
        fileLastModified.put(cacheKey, lastModified);
        
        return newCached;
    }
    
    public CachedScript compile(String source) throws CacheException {
        try {
            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.tokenizeInternal();
            
            Parser parser = new Parser(tokens);
            parser.setFunctionRegistry(sharedFunctionRegistry);
            Program program = parser.parse();
            
            String sourceHash = hashSource(source);
            
            return new CachedScript(program, System.currentTimeMillis(), sourceHash);
        } catch (LexerException e) {
            throw new CacheException("Lexer error: " + e.getMessage(), e);
        } catch (ParserException e) {
            throw new CacheException("Parser error: " + e.getMessage(), e);
        }
    }
    
    public void invalidate(String filePath) {
        String cacheKey = generateCacheKey(filePath);
        cache.remove(cacheKey);
        fileLastModified.remove(cacheKey);
        logger.debug("Invalidated cache for: {}", filePath);
    }
    
    public void clear() {
        cache.clear();
        fileLastModified.clear();
        hitCount = 0;
        missCount = 0;
        logger.info("Script cache cleared");
    }
    
    public int size() {
        return cache.size();
    }
    
    public CacheStats getStats() {
        return new CacheStats(hitCount, missCount, cache.size());
    }
    
    private String generateCacheKey(String filePath) {
        return filePath;
    }
    
    private String hashSource(String source) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(source.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(source.hashCode());
        }
    }
    
    public static class CacheStats {
        private final int hitCount;
        private final int missCount;
        private final int size;
        
        public CacheStats(int hitCount, int missCount, int size) {
            this.hitCount = hitCount;
            this.missCount = missCount;
            this.size = size;
        }
        
        public int getHitCount() {
            return hitCount;
        }
        
        public int getMissCount() {
            return missCount;
        }
        
        public int getSize() {
            return size;
        }
        
        public double getHitRate() {
            int total = hitCount + missCount;
            return total > 0 ? (double) hitCount / total : 0.0;
        }
        
        @Override
        public String toString() {
            return String.format("CacheStats{hits=%d, misses=%d, size=%d, hitRate=%.2f%%}", 
                    hitCount, missCount, size, getHitRate() * 100);
        }
    }
}
