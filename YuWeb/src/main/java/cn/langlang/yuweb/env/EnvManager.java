package cn.langlang.yuweb.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EnvManager {
    private static final Logger logger = LoggerFactory.getLogger(EnvManager.class);
    
    private static final EnvManager INSTANCE = new EnvManager();
    
    private final Map<String, String> envVars = new HashMap<>();
    private boolean loaded = false;
    
    private EnvManager() {
        // Load system environment variables
        envVars.putAll(System.getenv());
    }
    
    public static EnvManager getInstance() {
        return INSTANCE;
    }
    
    public String get(String key) {
        return envVars.get(key);
    }
    
    public String get(String key, String defaultValue) {
        return envVars.getOrDefault(key, defaultValue);
    }
    
    public boolean has(String key) {
        return envVars.containsKey(key);
    }
    
    public void set(String key, String value) {
        envVars.put(key, value);
    }
    
    public void remove(String key) {
        envVars.remove(key);
    }
    
    public Map<String, String> getAll() {
        return new HashMap<>(envVars);
    }
    
    public boolean loadEnvFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            logger.warn("Env file not found: {}", path);
            return false;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // Parse key=value
                int equalIndex = line.indexOf('=');
                if (equalIndex > 0) {
                    String key = line.substring(0, equalIndex).trim();
                    String value = line.substring(equalIndex + 1).trim();
                    
                    // Remove quotes if present
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    } else if (value.startsWith("'") && value.endsWith("'")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    
                    envVars.put(key, value);
                    logger.debug("Loaded env var: {} = {}", key, "***");
                } else {
                    logger.warn("Invalid env line at {}: {}", lineNumber, line);
                }
            }
            
            loaded = true;
            logger.info("Loaded env file: {}", path);
            return true;
        } catch (IOException e) {
            logger.error("Error loading env file: {}", e.getMessage());
            return false;
        }
    }
    
    public boolean loadEnvFile() {
        // Try common locations
        String[] paths = {".env", "./.env", "../.env", "env", "./env"};
        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
                return loadEnvFile(path);
            }
        }
        logger.debug("No .env file found in common locations");
        return false;
    }
    
    public boolean isLoaded() {
        return loaded;
    }
    
    public void clear() {
        envVars.clear();
        envVars.putAll(System.getenv());
        loaded = false;
    }
    
    public int size() {
        return envVars.size();
    }
}
