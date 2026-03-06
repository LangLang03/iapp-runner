package cn.langlang.yuweb;

public class Main {
    public static void main(String[] args) {
        String projectPath = args.length > 0 ? args[0] : ".";
        YuWebServer server = new YuWebServer(projectPath);
        server.start();
    }
}
