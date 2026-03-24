package cn.langlang.yuweb.web;

import java.io.InputStream;

public class UploadedFile {
    private String name;
    private String filename;
    private String contentType;
    private long size;
    private InputStream inputStream;
    private String extension;
    
    public UploadedFile() {}
    
    public UploadedFile(String name, String filename, String contentType, long size, InputStream inputStream) {
        this.name = name;
        this.filename = filename;
        this.contentType = contentType;
        this.size = size;
        this.inputStream = inputStream;
        if (filename != null && filename.contains(".")) {
            this.extension = filename.substring(filename.lastIndexOf("."));
        }
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
        if (filename != null && filename.contains(".")) {
            this.extension = filename.substring(filename.lastIndexOf("."));
        }
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public long getSize() {
        return size;
    }
    
    public void setSize(long size) {
        this.size = size;
    }
    
    public InputStream getInputStream() {
        return inputStream;
    }
    
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    
    public String getExtension() {
        return extension;
    }
    
    public void setExtension(String extension) {
        this.extension = extension;
    }
    
    public boolean isImage() {
        if (contentType == null) return false;
        return contentType.startsWith("image/");
    }
    
    public boolean isVideo() {
        if (contentType == null) return false;
        return contentType.startsWith("video/");
    }
    
    public boolean isAudio() {
        if (contentType == null) return false;
        return contentType.startsWith("audio/");
    }
    
    public boolean isDocument() {
        if (contentType == null) return false;
        return contentType.contains("pdf") || 
               contentType.contains("document") || 
               contentType.contains("text") ||
               contentType.contains("msword") ||
               contentType.contains("spreadsheet");
    }
}
