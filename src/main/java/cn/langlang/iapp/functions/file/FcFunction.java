package cn.langlang.iapp.functions.file;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FcFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "fc";
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
        String destPath = arguments.get(1) != null ? arguments.get(1).toString() : "";
        sourcePath = context.resolvePath(sourcePath);
        destPath = context.resolvePath(destPath);
        
        boolean overwrite = true;
        if (arguments.size() > 2 && arguments.get(2) instanceof Boolean) {
            overwrite = (Boolean) arguments.get(2);
        }
        
        try {
            File sourceFile = new File(sourcePath);
            File destFile = new File(destPath);
            
            if (!sourceFile.exists()) {
                return false;
            }
            
            if (destFile.exists() && !overwrite) {
                return false;
            }
            
            return copyFile(sourceFile, destFile);
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean copyFile(File source, File dest) throws Exception {
        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(dest)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
