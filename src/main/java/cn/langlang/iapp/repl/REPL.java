package cn.langlang.iapp.repl;

import cn.langlang.iapp.api.IAppScript;
import cn.langlang.iapp.api.IAppVariable;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Set;

public class REPL {
    private static final String PROMPT = "> ";
    private static final String INDENT = "..";
    
    private final IAppScript script;
    private final BufferedReader reader;
    private final StringBuilder inputBuffer;
    private boolean running;
    
    public REPL() {
        this.script = IAppScript.create();
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.inputBuffer = new StringBuilder();
        this.running = false;
    }
    
    public REPL(IAppScript script) {
        this.script = script;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.inputBuffer = new StringBuilder();
        this.running = false;
    }
    
    public void start() {
        running = true;
        printWelcome();
        
        while (running) {
            try {
                String prompt = getPrompt();
                System.out.print(prompt);
                
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                
                line = line.trim();
                
                if (line.isEmpty()) {
                    continue;
                }
                
                if (inputBuffer.length() == 0 && line.startsWith("/")) {
                    processCommand(line);
                    continue;
                }
                
                if (inputBuffer.length() > 0) {
                    inputBuffer.append("\n");
                }
                inputBuffer.append(line);
                
                InputCompletenessChecker.CheckResult result = 
                    InputCompletenessChecker.check(inputBuffer.toString());
                
                if (result.isComplete()) {
                    executeBuffer();
                }
                
            } catch (Exception e) {
                System.err.println("错误: " + e.getMessage());
                inputBuffer.setLength(0);
            }
        }
    }
    
    private String getPrompt() {
        if (inputBuffer.length() == 0) {
            return PROMPT;
        }
        
        int indentLevel = InputCompletenessChecker.getIndentLevel(inputBuffer.toString());
        StringBuilder prompt = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            prompt.append(INDENT);
        }
        if (prompt.length() > 0) {
            prompt.append(" ");
        }
        return prompt.toString();
    }
    
    public void stop() {
        running = false;
    }
    
    public Object eval(String input) {
        return script.eval(input);
    }
    
    private void executeBuffer() {
        String code = inputBuffer.toString();
        inputBuffer.setLength(0);
        
        try {
            Object result = script.eval(code);
            if (result != null) {
                System.out.println(formatResult(result));
            }
        } catch (Exception e) {
            System.err.println("执行错误: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("原因: " + e.getCause().getMessage());
            }
        }
    }
    
    private void processCommand(String command) {
        String[] parts = command.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String arg = parts.length > 1 ? parts[1] : null;
        
        switch (cmd) {
            case "/help":
                printHelp();
                break;
            case "/vars":
                printVariables();
                break;
            case "/funcs":
                printFunctions();
                break;
            case "/reset":
                script.reset();
                System.out.println("环境已重置");
                break;
            case "/load":
                if (arg != null) {
                    loadFile(arg);
                } else {
                    System.err.println("用法: /load <文件路径>");
                }
                break;
            case "/mjava":
                if (arg != null) {
                    loadMjava(arg);
                } else {
                    System.err.println("用法: /mjava <目录路径>");
                }
                break;
            case "/cd":
                if (arg != null) {
                    changeDirectory(arg);
                } else {
                    System.err.println("用法: /cd <目录路径>");
                }
                break;
            case "/pwd":
                printCurrentDirectory();
                break;
            case "/exit":
            case "/quit":
                running = false;
                System.out.println("再见!");
                break;
            case "/clear":
                clearScreen();
                break;
            default:
                System.err.println("未知命令: " + cmd);
                System.err.println("输入 /help 查看可用命令");
        }
    }
    
    private void loadFile(String path) {
        try {
            File file = new File(path);
            if (!file.isAbsolute()) {
                String currentDir = script.getCurrentDirectory();
                if (currentDir != null) {
                    file = new File(currentDir, path);
                }
            }
            
            if (!file.exists()) {
                System.err.println("文件未找到: " + file.getAbsolutePath());
                return;
            }
            
            script.loadFile(file).eval();
            System.out.println("已加载: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("加载文件错误: " + e.getMessage());
        }
    }
    
    private void loadMjava(String path) {
        try {
            File dir = new File(path);
            if (!dir.isAbsolute()) {
                String currentDir = script.getCurrentDirectory();
                if (currentDir != null) {
                    dir = new File(currentDir, path);
                }
            }
            
            if (!dir.exists() || !dir.isDirectory()) {
                System.err.println("目录未找到: " + dir.getAbsolutePath());
                return;
            }
            
            script.loadMjava(dir.getAbsolutePath());
            System.out.println("已加载 mjava 模块: " + dir.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("加载 mjava 模块错误: " + e.getMessage());
        }
    }
    
    private void changeDirectory(String path) {
        File dir = new File(path);
        if (!dir.isAbsolute()) {
            String currentDir = script.getCurrentDirectory();
            if (currentDir != null) {
                dir = new File(currentDir, path);
            }
        }
        
        if (dir.exists() && dir.isDirectory()) {
            script.setCurrentDirectory(dir.getAbsolutePath());
            System.out.println("当前目录: " + dir.getAbsolutePath());
        } else {
            System.err.println("目录未找到: " + dir.getAbsolutePath());
        }
    }
    
    private void printCurrentDirectory() {
        String dir = script.getCurrentDirectory();
        if (dir != null) {
            System.out.println(dir);
        } else {
            System.out.println("未设置当前目录");
        }
    }
    
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    private String formatResult(Object result) {
        if (result == null) {
            return "null";
        }
        if (result instanceof Object[]) {
            return Arrays.toString((Object[]) result);
        }
        if (result instanceof boolean[]) {
            return Arrays.toString((boolean[]) result);
        }
        if (result instanceof int[]) {
            return Arrays.toString((int[]) result);
        }
        if (result instanceof long[]) {
            return Arrays.toString((long[]) result);
        }
        if (result instanceof double[]) {
            return Arrays.toString((double[]) result);
        }
        if (result instanceof char[]) {
            return Arrays.toString((char[]) result);
        }
        if (result instanceof byte[]) {
            return Arrays.toString((byte[]) result);
        }
        if (result instanceof short[]) {
            return Arrays.toString((short[]) result);
        }
        if (result instanceof float[]) {
            return Arrays.toString((float[]) result);
        }
        return result.toString();
    }
    
    private void printWelcome() {
        System.out.println();
        System.out.println("iAppV3 交互式解释器");
        System.out.println();
        System.out.println("输入 /help 查看帮助");
        System.out.println("输入 /exit 退出");
        System.out.println();
    }
    
    private void printHelp() {
        System.out.println();
        System.out.println("命令:");
        System.out.println("  /help           显示帮助信息");
        System.out.println("  /vars           显示所有变量");
        System.out.println("  /funcs          显示所有函数");
        System.out.println("  /reset          重置环境");
        System.out.println("  /load <file>    加载并执行脚本文件");
        System.out.println("  /mjava <dir>    加载 mjava 模块目录");
        System.out.println("  /cd <dir>       切换当前目录");
        System.out.println("  /pwd            显示当前目录");
        System.out.println("  /clear          清屏");
        System.out.println("  /exit           退出 REPL");
        System.out.println();
        System.out.println("多行输入:");
        System.out.println("  - fn ... end fn  函数定义");
        System.out.println("  - { ... }        代码块");
        System.out.println("  - f(...) { ... } if 语句");
        System.out.println("  - w(...) { ... } while 循环");
        System.out.println("  - for(...) { ... } for 循环");
        System.out.println("  - t() { ... }    线程语句");
        System.out.println();
    }
    
    private void printVariables() {
        Set<String> varNames = script.getVariableNames();
        if (varNames.isEmpty()) {
            System.out.println("没有定义变量");
            return;
        }
        
        System.out.println("变量:");
        for (String name : varNames) {
            IAppVariable var = script.getVariable(name);
            Object value = var.value();
            String valueStr = formatResult(value);
            if (valueStr.length() > 100) {
                valueStr = valueStr.substring(0, 97) + "...";
            }
            System.out.println("  " + name + " = " + valueStr);
        }
    }
    
    private void printFunctions() {
        Set<String> builtInFuncs = script.getFunctionNames();
        Set<String> userFuncs = script.getUserFunctionNames();
        
        if (!builtInFuncs.isEmpty()) {
            System.out.println("内置函数:");
            for (String name : builtInFuncs) {
                System.out.println("  " + name);
            }
        }
        
        if (!userFuncs.isEmpty()) {
            System.out.println("用户函数:");
            for (String name : userFuncs) {
                System.out.println("  " + name);
            }
        }
        
        if (builtInFuncs.isEmpty() && userFuncs.isEmpty()) {
            System.out.println("没有可用函数");
        }
    }
    
    public IAppScript getScript() {
        return script;
    }
}
