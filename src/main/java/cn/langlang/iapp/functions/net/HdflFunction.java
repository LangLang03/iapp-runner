package cn.langlang.iapp.functions.net;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class HdflFunction implements IFunction {
    private static final ExecutorService downloadExecutor = Executors.newCachedThreadPool();
    private static final Map<String, DownloadTask> downloadTasks = new ConcurrentHashMap<>();
    private static long taskIdCounter = 0;
    
    @Override
    public String getName() {
        return "hdfl";
    }
    
    @Override
    public int getMinParameters() {
        return 1;
    }
    
    @Override
    public int getMaxParameters() {
        return 6;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String saveDir = null;
        String tempDir = null;
        int threadCount = 3;
        int connectTimeout = 25000;
        boolean overwrite = true;
        
        if (arguments.size() >= 1) {
            String first = toString(arguments.get(0));
            if (arguments.size() == 1) {
                saveDir = context.resolvePath(first);
            } else {
                tempDir = context.resolvePath(first);
            }
        }
        if (arguments.size() >= 2) {
            saveDir = context.resolvePath(toString(arguments.get(1)));
        }
        if (arguments.size() >= 3) {
            threadCount = toInt(arguments.get(2));
        }
        if (arguments.size() >= 4) {
            connectTimeout = toInt(arguments.get(3));
        }
        if (arguments.size() >= 5) {
            overwrite = toBoolean(arguments.get(4));
        }
        
        if (tempDir == null) {
            tempDir = saveDir;
        }
        
        new File(saveDir).mkdirs();
        new File(tempDir).mkdirs();
        
        String taskId = "dl_" + (++taskIdCounter);
        DownloadTask task = new DownloadTask(taskId, saveDir, tempDir, threadCount, connectTimeout, overwrite);
        downloadTasks.put(taskId, task);
        
        return taskId;
    }
    
    public static DownloadTask getTask(String taskId) {
        return downloadTasks.get(taskId);
    }
    
    public static void removeTask(String taskId) {
        downloadTasks.remove(taskId);
    }
    
    public static ExecutorService getDownloadExecutor() {
        return downloadExecutor;
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
    }
    
    private int toInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private boolean toBoolean(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        return Boolean.parseBoolean(value.toString());
    }
    
    @Override
    public boolean isSupported() {
        return true;
    }
    
    @Override
    public String getUnsupportedReason() {
        return null;
    }
}
