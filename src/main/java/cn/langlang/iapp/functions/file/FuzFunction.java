package cn.langlang.iapp.functions.file;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FuzFunction extends AbstractFunction {
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
        return 5;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) {
        String zipPath = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String entryName = arguments.get(1) != null ? arguments.get(1).toString() : "";
        String destPath = arguments.get(2) != null ? arguments.get(2).toString() : "";
        zipPath = context.resolvePath(zipPath);
        destPath = context.resolvePath(destPath);
        
        boolean overwrite = true;
        if (arguments.size() > 3 && arguments.get(3) instanceof Boolean) {
            overwrite = (Boolean) arguments.get(3);
        }
        
        try {
            File zipFile = new File(zipPath);
            File destFile = new File(destPath);
            
            if (!zipFile.exists()) {
                return false;
            }
            
            if (destFile.exists() && !overwrite) {
                return false;
            }
            
            return unzipEntry(zipFile, entryName, destFile);
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean unzipEntry(File zipFile, String entryName, File destFile) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals(entryName)) {
                    destFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(destFile)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                    return true;
                }
                zis.closeEntry();
            }
            return false;
        }
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.STRING, ParamType.OUTPUT);
    }
    
    @Override
    public List<List<ParamType>> getParamTypeLists() {
        return typeLists(
            types(ParamType.STRING, ParamType.STRING, ParamType.STRING, ParamType.OUTPUT),
            types(ParamType.STRING, ParamType.STRING, ParamType.STRING, ParamType.BOOLEAN, ParamType.OUTPUT)
        );
    }
}
