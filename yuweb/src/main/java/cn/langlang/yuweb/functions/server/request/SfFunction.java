package cn.langlang.yuweb.functions.server.request;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.web.UploadedFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SfFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "sf";
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
        Object fileObj = arguments.get(0);
        String savePath = arguments.get(1) != null ? arguments.get(1).toString() : "";
        boolean generateName = false;
        
        if (arguments.size() > 2 && arguments.get(2) != null) {
            if (arguments.get(2) instanceof Boolean) {
                generateName = (Boolean) arguments.get(2);
            } else if (arguments.get(2) instanceof Number) {
                generateName = ((Number) arguments.get(2)).intValue() == 1;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        
        if (fileObj == null) {
            result.put("success", false);
            result.put("msg", "文件对象为空");
            return result;
        }
        
        if (!(fileObj instanceof UploadedFile)) {
            result.put("success", false);
            result.put("msg", "参数不是有效的文件对象");
            return result;
        }
        
        UploadedFile uploadedFile = (UploadedFile) fileObj;
        
        try {
            File dir = new File(savePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            String filename = uploadedFile.getFilename();
            if (generateName) {
                String extension = uploadedFile.getExtension();
                if (extension == null) extension = "";
                filename = UUID.randomUUID().toString().replace("-", "") + extension;
            }
            
            String fullPath = savePath;
            if (!fullPath.endsWith(File.separator) && !fullPath.endsWith("/")) {
                fullPath += File.separator;
            }
            fullPath += filename;
            
            File outputFile = new File(fullPath);
            
            try (InputStream is = uploadedFile.getInputStream();
                 BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
            }
            
            result.put("success", true);
            result.put("msg", "保存成功");
            result.put("filename", filename);
            result.put("path", fullPath);
            result.put("size", uploadedFile.getSize());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("msg", "保存失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types(ParamType.OBJECT, ParamType.STRING, ParamType.BOOLEAN);
    }
}
