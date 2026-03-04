package cn.langlang.iapp.functions.net;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.*;

public class DownloadTask {
    private final String taskId;
    private final String saveDir;
    private final String tempDir;
    private final int threadCount;
    private final int connectTimeout;
    private final boolean overwrite;
    private final ConcurrentMap<Integer, DownloadItem> items = new ConcurrentHashMap<>();
    private int itemCounter = 0;
    private final ExecutorService executor;
    
    public DownloadTask(String taskId, String saveDir, String tempDir, int threadCount, int connectTimeout, boolean overwrite) {
        this.taskId = taskId;
        this.saveDir = saveDir;
        this.tempDir = tempDir;
        this.threadCount = threadCount;
        this.connectTimeout = connectTimeout;
        this.overwrite = overwrite;
        this.executor = Executors.newFixedThreadPool(threadCount);
    }
    
    public int addItem(String url, String filename, int type, String text) {
        int id = ++itemCounter;
        DownloadItem item = new DownloadItem(id, url, filename, type, text);
        items.put(id, item);
        return id;
    }
    
    public void startAll() {
        for (DownloadItem item : items.values()) {
            if (item.getStatus() == 0) {
                item.setStatus(1);
                executor.submit(() -> downloadItem(item));
            }
        }
    }
    
    private void downloadItem(DownloadItem item) {
        try {
            File saveFile = new File(saveDir, item.getFilename());
            if (saveFile.exists() && !overwrite) {
                item.setStatus(2);
                return;
            }
            
            URL url = new URL(item.getUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(connectTimeout * 2);
            
            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                try (InputStream is = conn.getInputStream();
                     FileOutputStream fos = new FileOutputStream(saveFile)) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                item.setStatus(2);
            } else {
                item.setStatus(-1);
            }
        } catch (Exception e) {
            item.setStatus(-1);
        }
    }
    
    public DownloadItem getItem(int id) {
        return items.get(id);
    }
    
    public Map<Integer, DownloadItem> getItems() {
        return items;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public void shutdown() {
        executor.shutdown();
    }
}
