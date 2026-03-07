package cn.langlang.yuweb.server;

import cn.langlang.yuweb.YuWebConfig;

public class Main {
    public static void main(String[] args) {
        String projectPath = ".";
        boolean debugMode = false;
        boolean safeMode = false;
        boolean preloadScripts = false;
        boolean serveStaticFiles = true;
        int port = 8080;
        
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--debug":
                case "-d":
                    debugMode = true;
                    break;
                case "--safe":
                case "-s":
                    safeMode = true;
                    preloadScripts = true;
                    break;
                case "--preload":
                case "-p":
                    preloadScripts = true;
                    break;
                case "--no-static":
                    serveStaticFiles = false;
                    break;
                case "--port":
                    if (i + 1 < args.length) {
                        try {
                            port = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid port number: " + args[i]);
                            return;
                        }
                    }
                    break;
                case "--help":
                case "-h":
                    printUsage();
                    return;
                default:
                    if (!args[i].startsWith("-")) {
                        projectPath = args[i];
                    }
                    break;
            }
        }
        
        System.out.println("YuWeb Server " + YuWebServer.VERSION);
        System.out.println("端口: " + port);
        System.out.println("访问: http://localhost:" + port);
        System.out.println("项目路径: " + projectPath);
        
        if (debugMode) {
            System.out.println("调试模式: 开启");
        }
        if (safeMode) {
            System.out.println("安全模式: 开启 (仅执行预加载脚本)");
        }
        if (preloadScripts) {
            System.out.println("脚本预加载: 开启");
        }
        if (!serveStaticFiles) {
            System.out.println("静态文件服务: 关闭");
        }
        
        YuWebConfig config = new YuWebConfig();
        config.setDebugMode(debugMode);
        config.setSafeMode(safeMode);
        config.setPreloadScripts(preloadScripts);
        config.setServeStaticFiles(serveStaticFiles);
        
        YuWebServer server = new YuWebServer(projectPath, config);
        server.setPort(port);
        server.start();
    }
    
    private static void printUsage() {
        System.out.println("YuWeb Server - iApp Web Framework");
        System.out.println();
        System.out.println("用法: java -jar yuweb.jar [选项] [项目路径]");
        System.out.println();
        System.out.println("选项:");
        System.out.println("  -d, --debug      启用调试模式，显示详细错误信息");
        System.out.println("  -s, --safe       启用安全模式，仅执行预加载的脚本");
        System.out.println("                   (自动启用 --preload)");
        System.out.println("  -p, --preload    启动时预加载所有 .iapp 脚本");
        System.out.println("  --no-static      禁用静态文件服务");
        System.out.println("  --port <端口>    指定服务端口 (默认: 8080)");
        System.out.println("  -h, --help       显示帮助信息");
        System.out.println();
        System.out.println("示例:");
        System.out.println("  java -jar yuweb.jar                          # 默认模式");
        System.out.println("  java -jar yuweb.jar --safe                   # 安全模式");
        System.out.println("  java -jar yuweb.jar --debug --preload        # 开发模式");
        System.out.println("  java -jar yuweb.jar --port 3000 myproject    # 自定义端口和路径");
        System.out.println();
        System.out.println("模式说明:");
        System.out.println("  默认模式:    动态编译脚本，支持静态文件");
        System.out.println("  安全模式:    仅执行启动时预加载的脚本，防止恶意上传");
        System.out.println("  预加载模式:  启动时编译所有脚本，提高运行时性能");
    }
}
