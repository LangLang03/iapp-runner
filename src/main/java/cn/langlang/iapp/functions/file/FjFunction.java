package cn.langlang.iapp.functions.file;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FjFunction extends AbstractFunction {
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
        return 4;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) {
        String sourcePath = arguments.get(0) != null ? arguments.get(0).toString() : "";
        String zipPath = arguments.get(1) != null ? arguments.get(1).toString() : "";
        sourcePath = context.resolvePath(sourcePath);
        zipPath = context.resolvePath(zipPath);
        
        boolean includeRoot = true;
        if (arguments.size() > 2 && arguments.get(2) instanceof Boolean) {
            includeRoot = (Boolean) arguments.get(2);
        }
        
        try {
            File sourceFile = new File(sourcePath);
            File zipFile = new File(zipPath);
            
            if (!sourceFile.exists()) {
                return false;
            }
            
            return zipFile(sourceFile, zipFile, includeRoot);
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean zipFile(File source, File zipFile, boolean includeRoot) throws Exception {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            if (source.isDirectory()) {
                String basePath = includeRoot ? source.getName() : "";
                zipDirectory(source, basePath, zos);
            } else {
                zipFile(source, "", zos);
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void zipDirectory(File dir, String basePath, ZipOutputStream zos) throws Exception {
        File[] files = dir.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            String entryPath = basePath.isEmpty() ? file.getName() : basePath + "/" + file.getName();
            if (file.isDirectory()) {
                zipDirectory(file, entryPath, zos);
            } else {
                zipFile(file, entryPath, zos);
            }
        }
    }
    
    private void zipFile(File file, String entryPath, ZipOutputStream zos) throws Exception {
        String name = entryPath.isEmpty() ? file.getName() : entryPath;
        ZipEntry entry = new ZipEntry(name);
        zos.putNextEntry(entry);
        
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, bytesRead);
            }
        }
        zos.closeEntry();
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.STRING, ParamType.STRING, ParamType.OUTPUT);
    }
    
    @Override
    public List<List<ParamType>> getParamTypeLists() {
        return typeLists(
            types(ParamType.STRING, ParamType.STRING, ParamType.OUTPUT),
            types(ParamType.STRING, ParamType.STRING, ParamType.BOOLEAN, ParamType.OUTPUT)
        );
    }
}
