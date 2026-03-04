package cn.langlang.iapp.functions.net;

import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HufFunction implements IFunction {
    @Override
    public String getName() {
        return "huf";
    }
    
    @Override
    public int getMinParameters() {
        return 4;
    }
    
    @Override
    public int getMaxParameters() {
        return 6;
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        String urlStr = toString(arguments.get(0));
        String formData = toString(arguments.get(1));
        String filePaths = toString(arguments.get(2));
        String encoding = toString(arguments.get(3));
        String headers = null;
        
        if (arguments.size() >= 5) {
            headers = toString(arguments.get(4));
            if ("null".equals(headers)) headers = null;
        }
        
        String boundary = "----iAppBoundary" + System.currentTimeMillis();
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", encoding);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            
            if (headers != null) {
                String[] headerPairs = headers.split("\\|\\|");
                for (String pair : headerPairs) {
                    int eqIdx = pair.indexOf('=');
                    if (eqIdx > 0) {
                        String key = pair.substring(0, eqIdx).trim();
                        String value = pair.substring(eqIdx + 1).trim();
                        conn.setRequestProperty(key, value);
                    }
                }
            }
            
            try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
                if (formData != null && !formData.isEmpty()) {
                    String[] pairs = formData.split("&");
                    for (String pair : pairs) {
                        int eqIdx = pair.indexOf('=');
                        if (eqIdx > 0) {
                            String key = pair.substring(0, eqIdx);
                            String value = eqIdx < pair.length() - 1 ? pair.substring(eqIdx + 1) : "";
                            
                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                            dos.writeBytes(lineEnd);
                            dos.writeBytes(value);
                            dos.writeBytes(lineEnd);
                        }
                    }
                }
                
                if (filePaths != null && !filePaths.isEmpty()) {
                    String[] files = filePaths.split("\\|");
                    for (String fileInfo : files) {
                        String fileName = "file";
                        String filePath;
                        
                        if (fileInfo.contains("\n")) {
                            String[] parts = fileInfo.split("\n", 2);
                            fileName = parts[0];
                            filePath = parts[1];
                        } else {
                            filePath = fileInfo;
                        }
                        
                        filePath = context.resolvePath(filePath);
                        File file = new File(filePath);
                        if (!file.exists()) continue;
                        
                        String fileDisplayName = file.getName();
                        
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"" + fileName + "\"; filename=\"" + fileDisplayName + "\"" + lineEnd);
                        dos.writeBytes("Content-Type: application/octet-stream" + lineEnd);
                        dos.writeBytes(lineEnd);
                        
                        try (FileInputStream fis = new FileInputStream(file)) {
                            byte[] buffer = new byte[8192];
                            int len;
                            while ((len = fis.read(buffer)) > 0) {
                                dos.write(buffer, 0, len);
                            }
                        }
                        dos.writeBytes(lineEnd);
                    }
                }
                
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                dos.flush();
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                try (InputStream is = conn.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is, encoding))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line).append("\n");
                    }
                    return response.toString().trim();
                }
            } else {
                try (InputStream es = conn.getErrorStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(es, encoding))) {
                    StringBuilder error = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        error.append(line).append("\n");
                    }
                    return "Error " + responseCode + ": " + error.toString().trim();
                }
            }
        } catch (Exception e) {
            throw new FunctionException("Upload failed: " + e.getMessage());
        }
    }
    
    private String toString(Object value) {
        return value != null ? value.toString() : "";
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
