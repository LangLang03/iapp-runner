package cn.langlang.iapp;

import cn.langlang.iapp.api.IAppFunction;
import cn.langlang.iapp.api.IAppScript;
import cn.langlang.iapp.api.IAppVariable;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (args.length == 0) {
            printUsage();
            return;
        }
        
        String filePath = args[0];
        File file = new File(filePath);
        
        if (!file.exists()) {
            System.err.println("文件未找到: " + filePath);
            return;
        }
        
        IAppScript script = IAppScript.create();
        script.setCurrentDirectory(file.getParent());
        
        File mjavaDir = new File(file.getParent(), "mjava");
        if (mjavaDir.exists() && mjavaDir.isDirectory()) {
            script.loadMjava(mjavaDir.getAbsolutePath());
        }
        
        try {
            script.evalFile(filePath);
        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void printUsage() {
        System.out.println("iAppV3 跨平台解释器");
        System.out.println("用法: java -jar iapp.jar <脚本文件>");
        System.out.println();
        System.out.println("API 使用示例:");
        System.out.println();
        System.out.println("  // 创建脚本引擎");
        System.out.println("  IAppScript script = IAppScript.create();");
        System.out.println();
        System.out.println("  // 执行脚本");
        System.out.println("  script.eval(\"s a = 1\\n syso(a)\");");
        System.out.println("  script.evalFile(\"test.iapp\");");
        System.out.println();
        System.out.println("  // 变量操作");
        System.out.println("  script.setVariable(\"x\", 100);");
        System.out.println("  IAppVariable var = script.getVariable(\"x\");");
        System.out.println("  int value = var.asInt();");
        System.out.println();
        System.out.println("  // 函数操作");
        System.out.println("  IAppFunction func = script.getFunction(\"syso\");");
        System.out.println("  func.call(\"Hello World\");");
        System.out.println();
        System.out.println("  // 注册自定义函数");
        System.out.println("  script.registerFunction(\"myFunc\", (s, args) -> {");
        System.out.println("      return args[0];");
        System.out.println("  });");
        System.out.println();
        System.out.println("  // 加载 mjava 模块");
        System.out.println("  script.loadMjava(\"mjava\");");
        System.out.println();
        System.out.println("支持的功能:");
        System.out.println("  - 变量声明 (s, ss, sss)");
        System.out.println("  - 控制流 (f/else, w, for, break, endcode)");
        System.out.println("  - 字符串函数 (ss, sr, sj, sl, ssg, slg 等)");
        System.out.println("  - 数学函数 (s+, s-, s*, s/, s%, s, s2, sn, sran)");
        System.out.println("  - 数组函数 (nsz, sgsz, sssz, sgszl)");
        System.out.println("  - 文件函数 (fd, fe, fs, fr, fw, fc, fl, ft, fdir)");
        System.out.println("  - 时间函数 (time)");
        System.out.println("  - Java 交互 (java, javax, javanew, javags, javass, cls)");
        System.out.println("  - 输出函数 (syso, tw)");
        System.out.println("  - mjava 模块加载");
        System.out.println();
        System.out.println("注意: 不支持 Android 专用函数。");
    }
}
