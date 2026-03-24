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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ScriptCache {
    private static final Logger logger = LoggerFactory.getLogger(ScriptCache.class);
    
    private static final int DEFAULT_MAX_SIZE = 500;
    
    private final Map<String, CachedScript> cache;
    private final Map<String, Long> fileLastModified;
    private final FunctionRegistry sharedFunctionRegistry;
    private final int maxSize;
    private final ReadWriteLock lock;
    
    private volatile int hitCount = 0;
    private volatile int missCount = 0;
    private volatile int evictionCount = 0;
    
    public ScriptCache(FunctionRegistry sharedFunctionRegistry) {
        this(sharedFunctionRegistry, DEFAULT_MAX_SIZE);
    }
    
    public ScriptCache(FunctionRegistry sharedFunctionRegistry, int maxSize) {
        this.sharedFunctionRegistry = sharedFunctionRegistry;
        this.maxSize = maxSize > 0 ? maxSize : DEFAULT_MAX_SIZE;
        this.lock = new ReentrantReadWriteLock();
        
        this.cache = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, CachedScript> eldest) {
                if (size() > ScriptCache.this.maxSize) {
                    evictionCount++;
                    fileLastModified.remove(eldest.getKey());
                    logger.debug("Evicted cache entry: {}", eldest.getKey());
                    return true;
                }
                return false;
            }
        };
        
        this.fileLastModified = new ConcurrentHashMap<>();
    }
    
    public CachedScript getOrCompile(String filePath, String source) throws CacheException {
        File file = new File(filePath);
        long lastModified = file.exists() ? file.lastModified() : System.currentTimeMillis();
        
        String cacheKey = generateCacheKey(filePath);
        
        lock.readLock().lock();
        try {
            CachedScript cached = cache.get(cacheKey);
            Long cachedLastModified = fileLastModified.get(cacheKey);
            
            if (cached != null && cachedLastModified != null && cachedLastModified >= lastModified) {
                hitCount++;
                logger.debug("Cache hit for script: {}", filePath);
                return cached;
            }
        } finally {
            lock.readLock().unlock();
        }
        
        missCount++;
        logger.debug("Cache miss for script: {}, compiling...", filePath);
        
        CachedScript newCached = compile(source);
        
        lock.writeLock().lock();
        try {
            cache.put(cacheKey, newCached);
            fileLastModified.put(cacheKey, lastModified);
        } finally {
            lock.writeLock().unlock();
        }
        
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
        lock.writeLock().lock();
        try {
            cache.remove(cacheKey);
            fileLastModified.remove(cacheKey);
        } finally {
            lock.writeLock().unlock();
        }
        logger.debug("Invalidated cache for: {}", filePath);
    }
    
    public void clear() {
        lock.writeLock().lock();
        try {
            cache.clear();
            fileLastModified.clear();
        } finally {
            lock.writeLock().unlock();
        }
        hitCount = 0;
        missCount = 0;
        evictionCount = 0;
        logger.info("Script cache cleared");
    }
    
    public int size() {
        lock.readLock().lock();
        try {
            return cache.size();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public int getMaxSize() {
        return maxSize;
    }
    
    public CacheStats getStats() {
        lock.readLock().lock();
        try {
            return new CacheStats(hitCount, missCount, cache.size(), evictionCount, maxSize);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    private String generateCacheKey(String filePath) {
        return filePath;
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
    
    public static class CacheStats {
        private final int hitCount;
        private final int missCount;
        private final int size;
        private final int evictionCount;
        private final int maxSize;
        
        public CacheStats(int hitCount, int missCount, int size, int evictionCount, int maxSize) {
            this.hitCount = hitCount;
            this.missCount = missCount;
            this.size = size;
            this.evictionCount = evictionCount;
            this.maxSize = maxSize;
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
        
        public int getEvictionCount() {
            return evictionCount;
        }
        
        public int getMaxSize() {
            return maxSize;
        }
        
        public double getHitRate() {
            int total = hitCount + missCount;
            return total > 0 ? (double) hitCount / total : 0.0;
        }
        
        @Override
        public String toString() {
            return String.format("CacheStats{hits=%d, misses=%d, size=%d/%d, evictions=%d, hitRate=%.2f%%}", 
                    hitCount, missCount, size, maxSize, evictionCount, getHitRate() * 100);
        }
    }
}
