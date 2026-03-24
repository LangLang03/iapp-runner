package cn.langlang.yuweb.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PerformanceMonitor {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitor.class);
    
    private static final PerformanceMonitor INSTANCE = new PerformanceMonitor();
    
    private final Map<String, AtomicLong> counters = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> timers = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> maxTimers = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> minTimers = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> callCounts = new ConcurrentHashMap<>();
    
    private volatile boolean enabled = true;
    
    private PerformanceMonitor() {
    }
    
    public static PerformanceMonitor getInstance() {
        return INSTANCE;
    }
    
    public void enable() {
        this.enabled = true;
    }
    
    public void disable() {
        this.enabled = false;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void incrementCounter(String name) {
        if (!enabled) return;
        counters.computeIfAbsent(name, k -> new AtomicLong(0)).incrementAndGet();
    }
    
    public void addCounter(String name, long value) {
        if (!enabled) return;
        counters.computeIfAbsent(name, k -> new AtomicLong(0)).addAndGet(value);
    }
    
    public long getCounter(String name) {
        AtomicLong counter = counters.get(name);
        return counter != null ? counter.get() : 0;
    }
    
    public void recordTime(String name, long timeMs) {
        if (!enabled) return;
        
        timers.computeIfAbsent(name, k -> new AtomicLong(0)).addAndGet(timeMs);
        callCounts.computeIfAbsent(name, k -> new AtomicLong(0)).incrementAndGet();
        
        AtomicLong maxTimer = maxTimers.computeIfAbsent(name, k -> new AtomicLong(0));
        long currentMax = maxTimer.get();
        while (timeMs > currentMax) {
            if (maxTimer.compareAndSet(currentMax, timeMs)) {
                break;
            }
            currentMax = maxTimer.get();
        }
        
        AtomicLong minTimer = minTimers.computeIfAbsent(name, k -> new AtomicLong(Long.MAX_VALUE));
        long currentMin = minTimer.get();
        while (timeMs < currentMin) {
            if (minTimer.compareAndSet(currentMin, timeMs)) {
                break;
            }
            currentMin = minTimer.get();
        }
    }
    
    public long getTotalTime(String name) {
        AtomicLong timer = timers.get(name);
        return timer != null ? timer.get() : 0;
    }
    
    public long getCallCount(String name) {
        AtomicLong count = callCounts.get(name);
        return count != null ? count.get() : 0;
    }
    
    public double getAverageTime(String name) {
        long total = getTotalTime(name);
        long count = getCallCount(name);
        return count > 0 ? (double) total / count : 0.0;
    }
    
    public long getMaxTime(String name) {
        AtomicLong maxTimer = maxTimers.get(name);
        return maxTimer != null ? maxTimer.get() : 0;
    }
    
    public long getMinTime(String name) {
        AtomicLong minTimer = minTimers.get(name);
        return minTimer != null ? (minTimer.get() == Long.MAX_VALUE ? 0 : minTimer.get()) : 0;
    }
    
    public void reset() {
        counters.clear();
        timers.clear();
        maxTimers.clear();
        minTimers.clear();
        callCounts.clear();
    }
    
    public void logSummary() {
        if (!enabled) {
            logger.info("Performance monitoring is disabled");
            return;
        }
        
        logger.info("=== Performance Monitor Summary ===");
        
        logger.info("--- Counters ---");
        for (Map.Entry<String, AtomicLong> entry : counters.entrySet()) {
            logger.info("  {}: {}", entry.getKey(), entry.getValue().get());
        }
        
        logger.info("--- Timers ---");
        for (Map.Entry<String, AtomicLong> entry : timers.entrySet()) {
            String name = entry.getKey();
            long total = entry.getValue().get();
            long count = getCallCount(name);
            double avg = getAverageTime(name);
            long max = getMaxTime(name);
            long min = getMinTime(name);
            
            logger.info("  {}: total={}ms, count={}, avg={:.2f}ms, min={}ms, max={}ms", 
                    name, total, count, avg, min, max);
        }
        
        logger.info("=================================");
    }
    
    public Map<String, Long> getCounters() {
        Map<String, Long> result = new ConcurrentHashMap<>();
        for (Map.Entry<String, AtomicLong> entry : counters.entrySet()) {
            result.put(entry.getKey(), entry.getValue().get());
        }
        return result;
    }
    
    public Map<String, TimerStats> getTimerStats() {
        Map<String, TimerStats> result = new ConcurrentHashMap<>();
        for (String name : timers.keySet()) {
            result.put(name, new TimerStats(
                    getTotalTime(name),
                    getCallCount(name),
                    getAverageTime(name),
                    getMinTime(name),
                    getMaxTime(name)
            ));
        }
        return result;
    }
    
    public static class TimerStats {
        private final long totalTime;
        private final long callCount;
        private final double averageTime;
        private final long minTime;
        private final long maxTime;
        
        public TimerStats(long totalTime, long callCount, double averageTime, long minTime, long maxTime) {
            this.totalTime = totalTime;
            this.callCount = callCount;
            this.averageTime = averageTime;
            this.minTime = minTime;
            this.maxTime = maxTime;
        }
        
        public long getTotalTime() {
            return totalTime;
        }
        
        public long getCallCount() {
            return callCount;
        }
        
        public double getAverageTime() {
            return averageTime;
        }
        
        public long getMinTime() {
            return minTime;
        }
        
        public long getMaxTime() {
            return maxTime;
        }
        
        @Override
        public String toString() {
            return String.format("TimerStats{total=%dms, count=%d, avg=%.2fms, min=%dms, max=%dms}",
                    totalTime, callCount, averageTime, minTime, maxTime);
        }
    }
}
