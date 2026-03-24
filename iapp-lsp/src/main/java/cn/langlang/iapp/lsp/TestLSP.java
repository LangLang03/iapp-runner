package cn.langlang.iapp.lsp;

import cn.langlang.iapp.lsp.core.LSContext;
import cn.langlang.iapp.lsp.core.model.FunctionInfo;
import cn.langlang.iapp.lsp.core.model.VariableInfo;
import cn.langlang.iapp.lsp.core.provider.CompletionProvider;
import cn.langlang.iapp.lsp.core.provider.FunctionProvider;
import cn.langlang.iapp.lsp.core.provider.HoverProvider;
import cn.langlang.iapp.lsp.registry.FunctionCategory;
import cn.langlang.iapp.lsp.registry.ModuleRegistry;

import java.util.List;

public class TestLSP {
    public static void main(String[] args) {
        System.out.println("=== iApp LSP 测试 ===\n");
        
        LSContext context = new LSContext();
        ModuleRegistry registry = new ModuleRegistry(context);
        registry.autoDiscover();
        System.out.println("1. 已加载模块: " + context.getLoadedModules());
        System.out.println("2. YuWeb 可用: " + context.isYuWebAvailable());
        System.out.println("3. 已注册函数数量: " + context.getFunctionNames().size());
        
        FunctionProvider funcProvider = new FunctionProvider(context);
        List<FunctionInfo> functions = funcProvider.getAllFunctions();
        
        System.out.println("\n4. 函数列表 (前20个):");
        functions.stream().limit(20).forEach(f -> {
            System.out.println("   - " + f.getName() + " [" + f.getCategory().getDisplayName() + "]");
            System.out.println("     签名: " + f.getSignature());
        });
        
        System.out.println("\n5. 按类别统计:");
        for (FunctionCategory cat : FunctionCategory.values()) {
            List<FunctionInfo> catFuncs = funcProvider.getFunctionsByCategory(cat);
            if (!catFuncs.isEmpty()) {
                System.out.println("   " + cat.getDisplayName() + ": " + catFuncs.size() + " 个");
            }
        }
        
        CompletionProvider compProvider = new CompletionProvider(context);
        System.out.println("\n6. 补全测试 (前缀 's'):");
        List<cn.langlang.iapp.lsp.core.provider.CompletionProvider.CompletionItem> completions = 
            compProvider.getFunctionCompletions("s");
        completions.stream().limit(10).forEach(c -> {
            System.out.println("   - " + c.getLabel() + " (" + c.getDetail() + ")");
        });
        
        HoverProvider hoverProvider = new HoverProvider(context);
        System.out.println("\n7. 悬停测试 (函数 'syso'):");
        String hover = hoverProvider.getHoverText("syso");
        if (hover != null) {
            System.out.println(hover);
        }
        
        System.out.println("\n=== 测试完成 ===");
    }
}
