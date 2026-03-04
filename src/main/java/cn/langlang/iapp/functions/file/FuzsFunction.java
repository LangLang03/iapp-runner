package cn.langlang.iapp.functions.file;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FuzsFunction extends AbstractFunction {
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
        return 3;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String zipPath = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String destPath = arguments.get(1) != null ? arguments.get(1).toString() : "";
        zipPath = context.resolvePath(zipPath);
        destPath = context.resolvePath(destPath);
        
        try {
            File zipFile = new File(zipPath);
            File destDir = new File(destPath);
            
            if (!zipFile.exists()) {
                return false;
            }
            
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            
            return unzipAll(zipFile, destDir);
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean unzipAll(File zipFile, File destDir) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File destFile = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    destFile.mkdirs();
                } else {
                    destFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(destFile)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                }
                zis.closeEntry();
            }
            return true;
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.OUTPUT);
    }
}
