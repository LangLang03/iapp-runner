package cn.langlang.iapp.functions.file;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FjFunction implements IFunction {
    @Override
    public String getName() {
        return "fj";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return 5;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String sourcePath = toString(arguments.get(0));
        String zipPath = toString(arguments.get(1));
        boolean removeRoot = true;
        
        if (arguments.size() >= 3) {
            removeRoot = toBoolean(arguments.get(2));
        }
        
        sourcePath = context.resolvePath(sourcePath);
        zipPath = context.resolvePath(zipPath);
        
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            return false;
        }
        
        File zipFile = new File(zipPath);
        zipFile.getParentFile().mkdirs();
        
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            if (sourceFile.isDirectory()) {
                String baseName = removeRoot ? "" : sourceFile.getName() + "/";
                compressDirectory(sourceFile, zos, baseName, removeRoot);
            } else {
                compressFile(sourceFile, zos, sourceFile.getName());
            }
        } catch (IOException e) {
            throw new FunctionException("Failed to compress: " + e.getMessage());
        }
        
        return true;
    }
    
    private void compressDirectory(File dir, ZipOutputStream zos, String basePath, boolean removeRoot) throws IOException {
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            zos.putNextEntry(new ZipEntry(basePath));
            zos.closeEntry();
            return;
        }
        
        for (File file : files) {
            String entryName = basePath + file.getName();
            if (file.isDirectory()) {
                compressDirectory(file, zos, entryName + "/", false);
            } else {
                compressFile(file, zos, entryName);
            }
        }
    }
    
    private void compressFile(File file, ZipOutputStream zos, String entryName) throws IOException {
        zos.putNextEntry(new ZipEntry(entryName));
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
        }
        zos.closeEntry();
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
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
