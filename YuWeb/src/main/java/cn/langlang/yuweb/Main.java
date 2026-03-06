package cn.langlang.yuweb;

public class Main {
    public static void main(String[] args) {
        String projectPath = ".";
        boolean debugMode = false;
        
        for (int i = 0; i < args.length; i++) {
            if ("--debug".equals(args[i]) || "-d".equals(args[i])) {
                debugMode = true;
            } else if (!args[i].startsWith("-")) {
                projectPath = args[i];
            }
        }
        
        System.out.println("YuWeb Server " + YuWebServer.VERSION);
        System.out.println("端口: 8080");
        System.out.println("访问: http://localhost:8080");
        if (debugMode) {
            System.out.println("调试模式: 开启");
        }
        
        YuWebServer server = new YuWebServer(projectPath, debugMode);
        server.start();
    }
}
