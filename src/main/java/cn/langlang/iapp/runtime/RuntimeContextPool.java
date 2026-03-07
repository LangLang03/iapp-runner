package cn.langlang.iapp.runtime;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class RuntimeContextPool {
    
    private final FunctionRegistry sharedRegistry;
    private final ConcurrentLinkedQueue<RuntimeContext> pool;
    private final AtomicInteger createdCount;
    private final AtomicInteger borrowedCount;
    private final int maxPoolSize;
    
    public RuntimeContextPool(FunctionRegistry sharedRegistry) {
        this(sharedRegistry, 100);
    }
    
    public RuntimeContextPool(FunctionRegistry sharedRegistry, int maxPoolSize) {
        this.sharedRegistry = sharedRegistry;
        this.maxPoolSize = maxPoolSize;
        this.pool = new ConcurrentLinkedQueue<>();
        this.createdCount = new AtomicInteger(0);
        this.borrowedCount = new AtomicInteger(0);
    }
    
    public RuntimeContext borrow() {
        RuntimeContext context = pool.poll();
        
        if (context != null) {
            context.reset();
            borrowedCount.incrementAndGet();
            return context;
        }
        
        if (createdCount.get() < maxPoolSize) {
            int count = createdCount.incrementAndGet();
            if (count <= maxPoolSize) {
                context = new RuntimeContext(sharedRegistry);
                borrowedCount.incrementAndGet();
                return context;
            } else {
                createdCount.decrementAndGet();
            }
        }
        
        context = pool.poll();
        if (context != null) {
            context.reset();
            borrowedCount.incrementAndGet();
            return context;
        }
        
        context = new RuntimeContext(sharedRegistry);
        borrowedCount.incrementAndGet();
        return context;
    }
    
    public void release(RuntimeContext context) {
        if (context == null) {
            return;
        }
        
        if (!context.isUseSharedRegistry()) {
            return;
        }
        
        context.reset();
        
        if (pool.size() < maxPoolSize) {
            pool.offer(context);
            borrowedCount.decrementAndGet();
        }
    }
    
    public int getPoolSize() {
        return pool.size();
    }
    
    public int getCreatedCount() {
        return createdCount.get();
    }
    
    public int getBorrowedCount() {
        return borrowedCount.get();
    }
    
    public void clear() {
        pool.clear();
        createdCount.set(0);
        borrowedCount.set(0);
    }
    
    public PoolStats getStats() {
        return new PoolStats(pool.size(), createdCount.get(), borrowedCount.get());
    }
    
    public static class PoolStats {
        private final int available;
        private final int totalCreated;
        private final int currentlyBorrowed;
        
        public PoolStats(int available, int totalCreated, int currentlyBorrowed) {
            this.available = available;
            this.totalCreated = totalCreated;
            this.currentlyBorrowed = currentlyBorrowed;
        }
        
        public int getAvailable() {
            return available;
        }
        
        public int getTotalCreated() {
            return totalCreated;
        }
        
        public int getCurrentlyBorrowed() {
            return currentlyBorrowed;
        }
        
        @Override
        public String toString() {
            return String.format("PoolStats{available=%d, created=%d, borrowed=%d}", 
                    available, totalCreated, currentlyBorrowed);
        }
    }
}
