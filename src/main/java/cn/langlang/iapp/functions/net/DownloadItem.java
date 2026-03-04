package cn.langlang.iapp.functions.net;

public class DownloadItem {
    private final int id;
    private final String url;
    private final String filename;
    private final int type;
    private final String text;
    private volatile int status;
    
    public DownloadItem(int id, String url, String filename, int type, String text) {
        this.id = id;
        this.url = url;
        this.filename = filename;
        this.type = type;
        this.text = text;
        this.status = 0;
    }
    
    public int getId() {
        return id;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public int getType() {
        return type;
    }
    
    public String getText() {
        return text;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
}
