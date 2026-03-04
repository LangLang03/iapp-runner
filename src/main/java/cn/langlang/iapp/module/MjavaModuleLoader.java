package cn.langlang.iapp.module;

import bsh.Interpreter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MjavaModuleLoader {
    private final Map<String, String> loadedModules;
    
    public MjavaModuleLoader() {
        this.loadedModules = new HashMap<>();
    }
    
    public void loadModules(String directory, Interpreter interpreter) {
        File dir = new File(directory);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        
        File[] files = dir.listFiles((d, name) -> name.endsWith(".mjava"));
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            loadModule(file, interpreter);
        }
    }
    
    public void loadModule(File file, Interpreter interpreter) {
        String moduleName = getModuleName(file.getName());
        try {
            String content = readFileContent(file);
            loadedModules.put(moduleName, content);
            
            String wrappedCode = wrapModuleCode(moduleName, content);
            interpreter.eval(wrappedCode);
            
        } catch (Exception e) {
            System.err.println("Failed to load mjava module: " + moduleName + " - " + e.getMessage());
        }
    }
    
    private String readFileContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, java.nio.charset.StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    
    private String getModuleName(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(0, dotIndex);
        }
        return fileName;
    }
    
    private String wrapModuleCode(String moduleName, String content) {
        StringBuilder wrapped = new StringBuilder();
        wrapped.append("// Module: ").append(moduleName).append("\n");
        wrapped.append(content);
        return wrapped.toString();
    }
    
    public Object executeMethod(String moduleName, String methodName, Object[] args, Interpreter interpreter) throws Exception {
        StringBuilder call = new StringBuilder();
        call.append(methodName).append("(");
        
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    call.append(", ");
                }
                
                String paramName = "_arg" + i;
                Object argValue = args[i];
                interpreter.set(paramName, argValue);
                call.append(paramName);
            }
        }
        
        call.append(")");
        
        return interpreter.eval(call.toString());
    }
    
    public boolean hasModule(String moduleName) {
        return loadedModules.containsKey(moduleName);
    }
    
    public String getModuleContent(String moduleName) {
        return loadedModules.get(moduleName);
    }
    
    public java.util.Set<String> getLoadedModuleNames() {
        return loadedModules.keySet();
    }
}
