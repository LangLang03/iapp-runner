package cn.langlang.iapp.functions.file;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FuzsFunction implements IFunction {
    @Override
    public String getName() {
        return "fuzs";
    }
    
    @Override
    public int getMinParameters() {
        return 2;
    }
    
    @Override
    public int getMaxParameters() {
        return 4;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String zipPath = toString(arguments.get(0));
        String destPath = toString(arguments.get(1));
        boolean overwrite = true;
        
        if (arguments.size() >= 3) {
            overwrite = toBoolean(arguments.get(2));
        }
        
        zipPath = context.resolvePath(zipPath);
        destPath = context.resolvePath(destPath);
        
        File zipFile = new File(zipPath);
        if (!zipFile.exists()) {
            return false;
        }
        
        File destDir = new File(destPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(destDir, entry.getName());
                
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    if (outFile.exists() && !overwrite) {
                        zis.closeEntry();
                        continue;
                    }
                    
                    outFile.getParentFile().mkdirs();
                    
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            throw new FunctionException("Failed to extract zip: " + e.getMessage());
        }
        
        return true;
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
