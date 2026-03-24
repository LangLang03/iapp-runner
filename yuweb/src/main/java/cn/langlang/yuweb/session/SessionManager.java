package cn.langlang.yuweb.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    
    private static final SessionManager INSTANCE = new SessionManager();
    
    private final Map<String, SessionData> sessions = new ConcurrentHashMap<>();
    private long defaultTtl = 1800000; // 30 minutes
    private volatile boolean cleanerRunning = false;
    private Thread cleanerThread;
    
    private SessionManager() {
        startCleaner();
    }
    
    public static SessionManager getInstance() {
        return INSTANCE;
    }
    
    public String createSession() {
        String sessionId = generateSessionId();
        sessions.put(sessionId, new SessionData(new ConcurrentHashMap<>(), System.currentTimeMillis() + defaultTtl));
        logger.debug("Session created: {}", sessionId);
        return sessionId;
    }
    
    public String createSession(long ttl) {
        String sessionId = generateSessionId();
        sessions.put(sessionId, new SessionData(new ConcurrentHashMap<>(), System.currentTimeMillis() + ttl));
        return sessionId;
    }
    
    public Object get(String sessionId, String key) {
        SessionData session = sessions.get(sessionId);
        if (session == null || session.isExpired()) {
            return null;
        }
        return session.getData().get(key);
    }
    
    public void set(String sessionId, String key, Object value) {
        SessionData session = sessions.get(sessionId);
        if (session != null && !session.isExpired()) {
            session.getData().put(key, value);
        }
    }
    
    public void set(String sessionId, String key, Object value, long ttl) {
        SessionData session = sessions.get(sessionId);
        if (session != null && !session.isExpired()) {
            session.getData().put(key, value);
            session.setExpiresAt(System.currentTimeMillis() + ttl);
        }
    }
    
    public void delete(String sessionId, String key) {
        SessionData session = sessions.get(sessionId);
        if (session != null) {
            session.getData().remove(key);
        }
    }
    
    public boolean has(String sessionId, String key) {
        SessionData session = sessions.get(sessionId);
        if (session == null || session.isExpired()) {
            return false;
        }
        return session.getData().containsKey(key);
    }
    
    public boolean exists(String sessionId) {
        SessionData session = sessions.get(sessionId);
        return session != null && !session.isExpired();
    }
    
    public void destroy(String sessionId) {
        sessions.remove(sessionId);
        logger.debug("Session destroyed: {}", sessionId);
    }
    
    public void refresh(String sessionId) {
        SessionData session = sessions.get(sessionId);
        if (session != null) {
            session.setExpiresAt(System.currentTimeMillis() + defaultTtl);
        }
    }
    
    public void refresh(String sessionId, long ttl) {
        SessionData session = sessions.get(sessionId);
        if (session != null) {
            session.setExpiresAt(System.currentTimeMillis() + ttl);
        }
    }
    
    public Map<String, Object> getAllData(String sessionId) {
        SessionData session = sessions.get(sessionId);
        if (session == null || session.isExpired()) {
            return null;
        }
        return session.getData();
    }
    
    public int getSessionCount() {
        cleanExpiredSessions();
        return sessions.size();
    }
    
    public void setDefaultTtl(long ttl) {
        this.defaultTtl = ttl > 0 ? ttl : 1800000;
    }
    
    public long getDefaultTtl() {
        return defaultTtl;
    }
    
    private String generateSessionId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    private void cleanExpiredSessions() {
        long now = System.currentTimeMillis();
        sessions.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    private void startCleaner() {
        if (cleanerRunning) {
            return;
        }
        cleanerRunning = true;
        cleanerThread = new Thread(() -> {
            while (cleanerRunning) {
                try {
                    Thread.sleep(60000); // Clean every minute
                    cleanExpiredSessions();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "session-cleaner");
        cleanerThread.setDaemon(true);
        cleanerThread.start();
        logger.info("Session cleaner started");
    }
    
    public void stopCleaner() {
        cleanerRunning = false;
        if (cleanerThread != null) {
            cleanerThread.interrupt();
        }
    }
    
    public void clearAll() {
        sessions.clear();
        logger.info("All sessions cleared");
    }
    
    private static class SessionData {
        private final Map<String, Object> data;
        private volatile long expiresAt;
        
        public SessionData(Map<String, Object> data, long expiresAt) {
            this.data = data;
            this.expiresAt = expiresAt;
        }
        
        public Map<String, Object> getData() {
            return data;
        }
        
        public long getExpiresAt() {
            return expiresAt;
        }
        
        public void setExpiresAt(long expiresAt) {
            this.expiresAt = expiresAt;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }
}
