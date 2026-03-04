package cn.langlang.iapp.functions.file;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FuzFunction implements IFunction {
    @Override
    public String getName() {
        return "fuz";
    }
    
    @Override
    public int getMinParameters() {
        return 3;
    }
    
    @Override
    public int getMaxParameters() {
        return 6;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String zipPath = toString(arguments.get(0));
        String entryName = toString(arguments.get(1));
        String destPath = toString(arguments.get(2));
        boolean overwrite = true;
        
        if (arguments.size() >= 4) {
            overwrite = toBoolean(arguments.get(3));
        }
        
        zipPath = context.resolvePath(zipPath);
        destPath = context.resolvePath(destPath);
        
        File zipFile = new File(zipPath);
        if (!zipFile.exists()) {
            return 0L;
        }
        
        File destDir = new File(destPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        
        int extractedCount = 0;
        
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals(entryName) || entry.getName().endsWith("/" + entryName)) {
                    File outFile = new File(destDir, new File(entry.getName()).getName());
                    
                    if (outFile.exists() && !overwrite) {
                        zis.closeEntry();
                        continue;
                    }
                    
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                    extractedCount++;
                    break;
                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            throw new FunctionException("Failed to extract zip: " + e.getMessage());
        }
        
        return (long) extractedCount;
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
