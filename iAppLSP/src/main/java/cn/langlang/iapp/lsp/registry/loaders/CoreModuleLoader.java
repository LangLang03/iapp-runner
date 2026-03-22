package cn.langlang.iapp.lsp.registry;

import cn.langlang.iapp.lsp.core.LSContext;
import cn.langlang.iapp.runtime.IFunction;

import java.util.HashMap;
import java.util.Map;

public class CoreModuleLoader implements ModuleLoader {
    
    @Override
    public String getName() {
        return "core";
    }
    
    @Override
    public void load(LSContext context) {
        registerStringFunctions(context);
        registerMathFunctions(context);
        registerArrayFunctions(context);
        registerFileFunctions(context);
        registerNetFunctions(context);
        registerTimeFunctions(context);
        registerJavaFunctions(context);
        registerListFunctions(context);
        registerClipboardFunctions(context);
        registerOutputFunctions(context);
        registerOtherFunctions(context);
    }
    
    private void registerStringFunctions(LSContext context) {
        context.registerFunction(new cn.langlang.iapp.functions.string.SsFunction(), FunctionCategory.STRING);
        context.registerFunction(new cn.langlang.iapp.functions.string.SrFunction(), FunctionCategory.STRING);
        context.registerFunction(new cn.langlang.iapp.functions.string.SjFunction(), FunctionCategory.STRING);
        context.registerFunction(new cn.langlang.iapp.functions.string.SlFunction(), FunctionCategory.STRING);
        context.registerFunction(new cn.langlang.iapp.functions.string.SsgFunction(), FunctionCategory.STRING);
        context.registerFunction(new cn.langlang.iapp.functions.string.SlgFunction(), FunctionCategory.STRING);
        context.registerFunction(new cn.langlang.iapp.functions.string.StrimFunction(), FunctionCategory.STRING);
        context.registerFunction(new cn.langlang.iapp.functions.string.SlowerFunction(), FunctionCategory.STRING);
        context.registerFunction(new cn.langlang.iapp.functions.string.SupperFunction(), FunctionCategory.STRING);
        context.registerFunction(new cn.langlang.iapp.functions.string.SiofFunction(), FunctionCategory.STRING);
        context.registerFunction(new cn.langlang.iapp.functions.string.SlofFunction(), FunctionCategory.STRING);
        context.registerFunction(new cn.langlang.iapp.functions.string.StobmFunction(), FunctionCategory.STRING);
        context.registerFunction(new cn.langlang.iapp.functions.string.Sutf8toFunction(), FunctionCategory.STRING);
    }
    
    private void registerMathFunctions(LSContext context) {
        context.registerFunction(new cn.langlang.iapp.functions.math.SAddFunction(), FunctionCategory.MATH);
        context.registerFunction(new cn.langlang.iapp.functions.math.SSubFunction(), FunctionCategory.MATH);
        context.registerFunction(new cn.langlang.iapp.functions.math.SMulFunction(), FunctionCategory.MATH);
        context.registerFunction(new cn.langlang.iapp.functions.math.SDivFunction(), FunctionCategory.MATH);
        context.registerFunction(new cn.langlang.iapp.functions.math.SModFunction(), FunctionCategory.MATH);
        context.registerFunction(new cn.langlang.iapp.functions.math.SFunction(), FunctionCategory.MATH);
        context.registerFunction(new cn.langlang.iapp.functions.math.S2Function(), FunctionCategory.MATH);
        context.registerFunction(new cn.langlang.iapp.functions.math.SnFunction(), FunctionCategory.MATH);
        context.registerFunction(new cn.langlang.iapp.functions.math.SranFunction(), FunctionCategory.MATH);
    }
    
    private void registerArrayFunctions(LSContext context) {
        context.registerFunction(new cn.langlang.iapp.functions.array.NszFunction(), FunctionCategory.ARRAY);
        context.registerFunction(new cn.langlang.iapp.functions.array.SgszFunction(), FunctionCategory.ARRAY);
        context.registerFunction(new cn.langlang.iapp.functions.array.SsszFunction(), FunctionCategory.ARRAY);
        context.registerFunction(new cn.langlang.iapp.functions.array.SgszlFunction(), FunctionCategory.ARRAY);
    }
    
    private void registerFileFunctions(LSContext context) {
        context.registerFunction(new cn.langlang.iapp.functions.file.FdFunction(), FunctionCategory.FILE);
        context.registerFunction(new cn.langlang.iapp.functions.file.FeFunction(), FunctionCategory.FILE);
        context.registerFunction(new cn.langlang.iapp.functions.file.FsFunction(), FunctionCategory.FILE);
        context.registerFunction(new cn.langlang.iapp.functions.file.FrFunction(), FunctionCategory.FILE);
        context.registerFunction(new cn.langlang.iapp.functions.file.FwFunction(), FunctionCategory.FILE);
        context.registerFunction(new cn.langlang.iapp.functions.file.FcFunction(), FunctionCategory.FILE);
        context.registerFunction(new cn.langlang.iapp.functions.file.FlFunction(), FunctionCategory.FILE);
        context.registerFunction(new cn.langlang.iapp.functions.file.FtFunction(), FunctionCategory.FILE);
        context.registerFunction(new cn.langlang.iapp.functions.file.FdirFunction(), FunctionCategory.FILE);
        context.registerFunction(new cn.langlang.iapp.functions.file.FuzFunction(), FunctionCategory.FILE);
        context.registerFunction(new cn.langlang.iapp.functions.file.FuzsFunction(), FunctionCategory.FILE);
        context.registerFunction(new cn.langlang.iapp.functions.file.FjFunction(), FunctionCategory.FILE);
        context.registerFunction(new cn.langlang.iapp.functions.file.FoFunction(), FunctionCategory.FILE);
        context.registerFunction(new cn.langlang.iapp.functions.file.FiFunction(), FunctionCategory.FILE);
    }
    
    private void registerNetFunctions(LSContext context) {
        context.registerFunction(new cn.langlang.iapp.functions.net.HsFunction(), FunctionCategory.NET);
        context.registerFunction(new cn.langlang.iapp.functions.net.HdFunction(), FunctionCategory.NET);
        context.registerFunction(new cn.langlang.iapp.functions.net.HdflFunction(), FunctionCategory.NET);
        context.registerFunction(new cn.langlang.iapp.functions.net.HufFunction(), FunctionCategory.NET);
        context.registerFunction(new cn.langlang.iapp.functions.net.HwFunction(), FunctionCategory.NET);
        context.registerFunction(new cn.langlang.iapp.functions.net.HwsFunction(), FunctionCategory.NET);
    }
    
    private void registerTimeFunctions(LSContext context) {
        context.registerFunction(new cn.langlang.iapp.functions.time.TimeFunction(), FunctionCategory.TIME);
    }
    
    private void registerJavaFunctions(LSContext context) {
        context.registerFunction(new cn.langlang.iapp.functions.java.JavaFunction(), FunctionCategory.JAVA);
        context.registerFunction(new cn.langlang.iapp.functions.java.JavaxFunction(), FunctionCategory.JAVA);
        context.registerFunction(new cn.langlang.iapp.functions.java.JavanewFunction(), FunctionCategory.JAVA);
        context.registerFunction(new cn.langlang.iapp.functions.java.JavagsFunction(), FunctionCategory.JAVA);
        context.registerFunction(new cn.langlang.iapp.functions.java.JavassFunction(), FunctionCategory.JAVA);
        context.registerFunction(new cn.langlang.iapp.functions.java.ClsFunction(), FunctionCategory.JAVA);
    }
    
    private void registerListFunctions(LSContext context) {
        context.registerFunction(new cn.langlang.iapp.functions.list.AslistFunction(), FunctionCategory.LIST);
        context.registerFunction(new cn.langlang.iapp.functions.list.SslistFunction(), FunctionCategory.LIST);
        context.registerFunction(new cn.langlang.iapp.functions.list.GslistFunction(), FunctionCategory.LIST);
        context.registerFunction(new cn.langlang.iapp.functions.list.GslistlFunction(), FunctionCategory.LIST);
        context.registerFunction(new cn.langlang.iapp.functions.list.DslistFunction(), FunctionCategory.LIST);
        context.registerFunction(new cn.langlang.iapp.functions.list.GslistszFunction(), FunctionCategory.LIST);
        context.registerFunction(new cn.langlang.iapp.functions.list.GslistisFunction(), FunctionCategory.LIST);
        context.registerFunction(new cn.langlang.iapp.functions.list.GslistiofFunction(), FunctionCategory.LIST);
        context.registerFunction(new cn.langlang.iapp.functions.list.GslistlofFunction(), FunctionCategory.LIST);
    }
    
    private void registerClipboardFunctions(LSContext context) {
        context.registerFunction(new cn.langlang.iapp.functions.clipboard.SxbFunction(), FunctionCategory.CLIPBOARD);
        context.registerFunction(new cn.langlang.iapp.functions.clipboard.ShbFunction(), FunctionCategory.CLIPBOARD);
    }
    
    private void registerOutputFunctions(LSContext context) {
        context.registerFunction(new cn.langlang.iapp.functions.output.SysoFunction(), FunctionCategory.OUTPUT);
        context.registerFunction(new cn.langlang.iapp.functions.output.TwFunction(), FunctionCategory.OUTPUT);
    }
    
    private void registerOtherFunctions(LSContext context) {
        context.registerFunction(new cn.langlang.iapp.functions.other.StopFunction(), FunctionCategory.OTHER);
        context.registerFunction(new cn.langlang.iapp.functions.other.CallFunction(), FunctionCategory.OTHER);
    }
    
    @Override
    public Map<String, FunctionCategory> getFunctionCategories() {
        Map<String, FunctionCategory> categories = new HashMap<>();
        categories.put("ss", FunctionCategory.STRING);
        categories.put("sr", FunctionCategory.STRING);
        categories.put("sj", FunctionCategory.STRING);
        categories.put("sl", FunctionCategory.STRING);
        categories.put("ssg", FunctionCategory.STRING);
        categories.put("slg", FunctionCategory.STRING);
        categories.put("sadd", FunctionCategory.MATH);
        categories.put("ssub", FunctionCategory.MATH);
        categories.put("smul", FunctionCategory.MATH);
        categories.put("sdiv", FunctionCategory.MATH);
        categories.put("smod", FunctionCategory.MATH);
        categories.put("nsz", FunctionCategory.ARRAY);
        categories.put("sgsz", FunctionCategory.ARRAY);
        categories.put("sssz", FunctionCategory.ARRAY);
        categories.put("sgszl", FunctionCategory.ARRAY);
        categories.put("fd", FunctionCategory.FILE);
        categories.put("fe", FunctionCategory.FILE);
        categories.put("fs", FunctionCategory.FILE);
        categories.put("fr", FunctionCategory.FILE);
        categories.put("fw", FunctionCategory.FILE);
        categories.put("fc", FunctionCategory.FILE);
        categories.put("fl", FunctionCategory.FILE);
        categories.put("ft", FunctionCategory.FILE);
        categories.put("fdir", FunctionCategory.FILE);
        categories.put("fuz", FunctionCategory.FILE);
        categories.put("fuzs", FunctionCategory.FILE);
        categories.put("fj", FunctionCategory.FILE);
        categories.put("fo", FunctionCategory.FILE);
        categories.put("fi", FunctionCategory.FILE);
        categories.put("hs", FunctionCategory.NET);
        categories.put("hd", FunctionCategory.NET);
        categories.put("hdfl", FunctionCategory.NET);
        categories.put("huf", FunctionCategory.NET);
        categories.put("hw", FunctionCategory.NET);
        categories.put("hws", FunctionCategory.NET);
        categories.put("time", FunctionCategory.TIME);
        categories.put("java", FunctionCategory.JAVA);
        categories.put("javax", FunctionCategory.JAVA);
        categories.put("javanew", FunctionCategory.JAVA);
        categories.put("javags", FunctionCategory.JAVA);
        categories.put("javass", FunctionCategory.JAVA);
        categories.put("cls", FunctionCategory.JAVA);
        categories.put("aslist", FunctionCategory.LIST);
        categories.put("sslist", FunctionCategory.LIST);
        categories.put("gslist", FunctionCategory.LIST);
        categories.put("gslistl", FunctionCategory.LIST);
        categories.put("dslist", FunctionCategory.LIST);
        categories.put("gslistsz", FunctionCategory.LIST);
        categories.put("gslistis", FunctionCategory.LIST);
        categories.put("gslistiof", FunctionCategory.LIST);
        categories.put("gslistlof", FunctionCategory.LIST);
        categories.put("sxb", FunctionCategory.CLIPBOARD);
        categories.put("shb", FunctionCategory.CLIPBOARD);
        categories.put("syso", FunctionCategory.OUTPUT);
        categories.put("tw", FunctionCategory.OUTPUT);
        categories.put("stop", FunctionCategory.OTHER);
        categories.put("call", FunctionCategory.OTHER);
        return categories;
    }
    
    @Override
    public int getPriority() {
        return 0;
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
}
