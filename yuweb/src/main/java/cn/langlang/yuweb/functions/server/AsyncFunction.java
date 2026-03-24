package cn.langlang.yuweb.functions.server;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.server.YuWebServer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class AsyncFunction extends AbstractFunction {
    private static final AtomicLong taskIdGenerator = new AtomicLong(0);
    private static final Map<Long, CompletableFuture<?>> pendingTasks = new ConcurrentHashMap<>();
    private static final Map<Long, Object> completedResults = new ConcurrentHashMap<>();
    private static final Map<Long, Throwable> failedTasks = new ConcurrentHashMap<>();
    
    private static YuWebServer server;
    
    public static void setServer(YuWebServer serverInstance) {
        server = serverInstance;
    }
    
    @Override
    public String getName() {
        return "async";
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
    public List<ParamType> getParamTypes() {
        return types(ParamType.OBJECT, ParamType.LONG);
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        if (server == null) {
            throw new FunctionException("Async server not initialized");
        }
        
        Object task = arguments.get(0);
        long timeout = arguments.size() > 1 ? toLong(arguments.get(1)) : 30000;
        
        long taskId = taskIdGenerator.incrementAndGet();
        
        CompletableFuture<Object> future = server.runAsync(() -> {
            if (task instanceof java.util.function.Supplier) {
                return ((java.util.function.Supplier<?>) task).get();
            } else if (task instanceof Runnable) {
                ((Runnable) task).run();
                return null;
            } else {
                return task;
            }
        });
        
        pendingTasks.put(taskId, future);
        
        future.whenComplete((result, error) -> {
            pendingTasks.remove(taskId);
            if (error != null) {
                failedTasks.put(taskId, error);
            } else {
                completedResults.put(taskId, result);
            }
        });
        
        return taskId;
    }
    
    public static Object getAsyncResult(long taskId) {
        Object result = completedResults.remove(taskId);
        if (result != null) {
            return result;
        }
        Throwable error = failedTasks.remove(taskId);
        if (error != null) {
            throw new RuntimeException("Async task failed: " + error.getMessage(), error);
        }
        return null;
    }
    
    public static boolean isAsyncComplete(long taskId) {
        return completedResults.containsKey(taskId) || failedTasks.containsKey(taskId);
    }
    
    public static boolean isAsyncPending(long taskId) {
        return pendingTasks.containsKey(taskId);
    }
    
    public static void cancelAsync(long taskId) {
        CompletableFuture<?> future = pendingTasks.remove(taskId);
        if (future != null) {
            future.cancel(true);
        }
        completedResults.remove(taskId);
        failedTasks.remove(taskId);
    }
    
    private long toLong(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
