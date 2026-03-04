package cn.langlang.iapp;

import cn.langlang.iapp.lexer.Lexer;
import cn.langlang.iapp.lexer.LexerException;
import cn.langlang.iapp.lexer.Token;
import cn.langlang.iapp.parser.Parser;
import cn.langlang.iapp.parser.ParserException;
import cn.langlang.iapp.ast.Program;
import cn.langlang.iapp.interpreter.Interpreter;
import cn.langlang.iapp.interpreter.InterpreterException;
import cn.langlang.iapp.runtime.RuntimeContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    void main(String[] args) {
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
            System.err.println("File not found: " + filePath);
            return;
        }
        
        String source = readFile(file);
        if (source == null) {
            System.err.println("Failed to read file: " + filePath);
            return;
        }
        
        RuntimeContext context = new RuntimeContext();
        context.setCurrentDirectory(file.getParent());
        
        File mjavaDir = new File(file.getParent(), "mjava");
        if (mjavaDir.exists() && mjavaDir.isDirectory()) {
            context.loadMjavaModules(mjavaDir.getAbsolutePath());
        }
        
        try {
            Lexer lexer = new Lexer(source);
            List<Token> tokens = lexer.tokenizeInternal();
            
            Parser parser = new Parser(tokens);
            parser.setFunctionRegistry(context.getFunctionRegistry());
            Program program = parser.parse();
            
            Interpreter interpreter = new Interpreter(context);
            interpreter.execute(program, context);
            
        } catch (LexerException e) {
            System.err.println("Lexer Error: " + e.getMessage());
            e.printStackTrace();
        } catch (ParserException e) {
            System.err.println("Parser Error: " + e.getMessage());
            e.printStackTrace();
        } catch (InterpreterException e) {
            System.err.println("Runtime Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String readFile(File file) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static void printUsage() {
        System.out.println("iAppV3 Interpreter for PC");
        System.out.println("Usage: java -jar iapp.jar <script_file>");
        System.out.println();
        System.out.println("Supported features:");
        System.out.println("  - Variable declarations (s, ss, sss)");
        System.out.println("  - Control flow (f/else, w, for, break, endcode)");
        System.out.println("  - String functions (ss, sr, sj, sl, ssg, slg, etc.)");
        System.out.println("  - Math functions (s+, s-, s*, s/, s%, s, s2, sn, sran)");
        System.out.println("  - Array functions (nsz, sgsz, sssz, sgszl)");
        System.out.println("  - File functions (fd, fe, fs, fr, fw, fc, fl, ft, fdir)");
        System.out.println("  - Time function (time)");
        System.out.println("  - Java interaction (java, javax, javanew, javags, javass, cls)");
        System.out.println("  - Output functions (syso, tw)");
        System.out.println("  - mjava module loading");
        System.out.println();
        System.out.println("Note: Android-specific functions are not supported.");
    }
}
