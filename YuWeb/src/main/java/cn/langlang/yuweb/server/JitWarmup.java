package cn.langlang.yuweb.server;

import cn.langlang.iapp.api.IAppScript;
import cn.langlang.iapp.lexer.Lexer;
import cn.langlang.iapp.parser.Parser;
import cn.langlang.iapp.runtime.FunctionRegistry;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.functions.SharedFunctionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class JitWarmup {
    private static final Logger logger = LoggerFactory.getLogger(JitWarmup.class);
    
    private static volatile boolean warmedUp = false;
    
    public static synchronized void warmup() {
        if (warmedUp) {
            return;
        }
        
        logger.info("Starting JIT warmup...");
        long startTime = System.currentTimeMillis();
        
        try {
            FunctionRegistry registry = SharedFunctionRegistry.getSharedRegistry();
            
            warmupLexer();
            warmupParser(registry);
            warmupInterpreter(registry);
            warmupRuntimeContext(registry);
            
            warmedUp = true;
            long elapsed = System.currentTimeMillis() - startTime;
            logger.info("JIT warmup completed in {}ms", elapsed);
            
        } catch (Exception e) {
            logger.warn("JIT warmup encountered an error: {}", e.getMessage());
        }
    }
    
    private static void warmupLexer() {
        String[] testScripts = {
            "syso(\"hello\")",
            "a = 1 + 2 * 3",
            "if(a > 0) { syso(a) }",
            "for(i = 0; i < 10; i = i + 1) { syso(i) }",
            "func test() { return 1 }",
            "arr = [1, 2, 3, 4, 5]",
            "obj = {\"name\": \"test\", \"value\": 123}"
        };
        
        for (int i = 0; i < 100; i++) {
            for (String script : testScripts) {
                try {
                    Lexer lexer = new Lexer(script);
                    lexer.tokenizeInternal();
                } catch (Exception ignored) {}
            }
        }
    }
    
    private static void warmupParser(FunctionRegistry registry) {
        String[] testScripts = {
            "syso(\"hello\")",
            "a = 1 + 2",
            "if(a > 0) { b = 1 } else { b = 2 }",
            "while(a < 10) { a = a + 1 }",
            "func add(a, b) { return a + b }",
            "arr[0] = 1",
            "obj.name = \"test\""
        };
        
        for (int i = 0; i < 100; i++) {
            for (String script : testScripts) {
                try {
                    Lexer lexer = new Lexer(script);
                    Parser parser = new Parser(lexer.tokenizeInternal());
                    parser.setFunctionRegistry(registry);
                    parser.parse();
                } catch (Exception ignored) {}
            }
        }
    }
    
    private static void warmupInterpreter(FunctionRegistry registry) {
        String[] testScripts = {
            "a = 1",
            "b = 2",
            "c = a + b",
            "d = c * 2",
            "e = \"hello\" + \" world\"",
            "arr = [1, 2, 3]",
            "obj = {}",
            "obj.a = 1",
            "if(true) { x = 1 }",
            "for(i = 0; i < 5; i = i + 1) { x = i }"
        };
        
        for (int i = 0; i < 50; i++) {
            for (String script : testScripts) {
                try {
                    RuntimeContext ctx = new RuntimeContext(registry);
                    IAppScript iapp = IAppScript.createWithContext(ctx);
                    iapp.loadString(script);
                    iapp.eval();
                } catch (Exception ignored) {}
            }
        }
    }
    
    private static void warmupRuntimeContext(FunctionRegistry registry) {
        for (int i = 0; i < 100; i++) {
            RuntimeContext ctx = new RuntimeContext(registry);
            
            ctx.setVariable("test" + i, i);
            ctx.getVariable("test" + i);
            
            Map<String, Object> map = new HashMap<>();
            map.put("key", "value");
            ctx.setVariable("map", map);
            
            ctx.getVariableManager().pushScope();
            ctx.setVariable("scoped", "value");
            ctx.getVariableManager().popScope();
            
            ctx.reset();
        }
    }
    
    public static boolean isWarmedUp() {
        return warmedUp;
    }
}
