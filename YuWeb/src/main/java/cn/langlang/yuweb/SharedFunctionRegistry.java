package cn.langlang.yuweb;

import cn.langlang.iapp.runtime.FunctionRegistry;
import cn.langlang.iapp.runtime.IFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SharedFunctionRegistry {
    private static final Logger logger = LoggerFactory.getLogger(SharedFunctionRegistry.class);
    
    private static final FunctionRegistry sharedRegistry = new FunctionRegistry();
    private static final List<Supplier<IFunction>> functionSuppliers = new ArrayList<>();
    private static boolean initialized = false;
    
    static {
        initializeFunctionSuppliers();
    }
    
    private static void initializeFunctionSuppliers() {
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.output.SysoFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.output.TwFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.string.SsFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.string.SrFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.string.SjFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.string.SlFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.string.SsgFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.string.SlgFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.string.StrimFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.string.SlowerFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.string.SupperFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.string.SiofFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.string.SlofFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.string.StobmFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.string.Sutf8toFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.math.SAddFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.math.SSubFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.math.SMulFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.math.SDivFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.math.SModFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.math.SFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.math.S2Function());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.math.SnFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.math.SranFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.array.NszFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.array.SgszFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.array.SsszFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.array.SgszlFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.file.FdFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.file.FeFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.file.FsFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.file.FrFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.file.FwFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.file.FcFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.file.FlFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.file.FtFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.file.FdirFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.file.FuzFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.file.FuzsFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.file.FjFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.file.FoFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.file.FiFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.net.HsFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.net.HdFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.net.HdflFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.net.HufFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.net.HwFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.net.HwsFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.time.TimeFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.other.StopFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.java.JavaFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.java.JavaxFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.java.JavanewFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.java.JavagsFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.java.JavassFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.java.ClsFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.other.CallFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.list.AslistFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.list.SslistFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.list.GslistFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.list.GslistlFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.list.DslistFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.list.GslistszFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.list.GslistisFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.list.GslistiofFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.list.GslistlofFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.clipboard.SxbFunction());
        functionSuppliers.add(() -> new cn.langlang.iapp.functions.clipboard.ShbFunction());
        
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.MethodFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.GetFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.GetsFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.PostFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.PostsFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.FormFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.FormsFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.BodyFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.PathFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.UrlFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.HeaderFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.ClientIpFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.UserAgentFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.IsJsonFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.IsAjaxFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.GetCookieFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.SetCookieFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.DelCookieFunction(null));
        
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.response.JsonFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.response.TextFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.response.HtmlFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.response.ErrorFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.response.StatusFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.response.SetHeaderFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.response.RedirectFunction(null));
        
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.util.MapFunction());
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.util.MgetFunction());
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.util.MsetFunction());
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.util.MkeysFunction());
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.util.MhasFunction());
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.util.ArrFunction());
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.util.ArrPushFunction());
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.util.LengthFunction());
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.util.JsonEncodeFunction());
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.util.JsonDecodeFunction());
        
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.database.condition.InFunction());
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.database.condition.LikeFunction());
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.database.condition.BetweenFunction());
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.database.condition.IsNullFunction());
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.database.condition.NotNullFunction());
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.database.condition.AndFunction());
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.database.condition.OrFunction());
        
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.auth.HashPasswordFunction());
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.auth.VerifyPasswordFunction());
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.auth.VerifyFunction());
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.auth.LogoutFunction());
    }
    
    public static synchronized void initialize(YuWebServer server, DatabaseManager dbManager) {
        if (initialized) {
            return;
        }
        
        logger.info("Initializing shared function registry...");
        long startTime = System.currentTimeMillis();
        
        for (Supplier<IFunction> supplier : functionSuppliers) {
            IFunction function = supplier.get();
            sharedRegistry.registerFunction(function);
        }
        
        sharedRegistry.registerFunction(new cn.langlang.yuweb.functions.server.config.PortFunction(server));
        
        sharedRegistry.registerFunction(new cn.langlang.yuweb.functions.database.DbFunction(dbManager));
        sharedRegistry.registerFunction(new cn.langlang.yuweb.functions.database.DbOneFunction(dbManager));
        sharedRegistry.registerFunction(new cn.langlang.yuweb.functions.database.DbAllFunction(dbManager));
        sharedRegistry.registerFunction(new cn.langlang.yuweb.functions.database.DbInsertFunction(dbManager));
        sharedRegistry.registerFunction(new cn.langlang.yuweb.functions.database.DbUpdateFunction(dbManager));
        sharedRegistry.registerFunction(new cn.langlang.yuweb.functions.database.DbDeleteFunction(dbManager));
        sharedRegistry.registerFunction(new cn.langlang.yuweb.functions.database.DbPageFunction(dbManager));
        sharedRegistry.registerFunction(new cn.langlang.yuweb.functions.database.DbCountFunction(dbManager));
        sharedRegistry.registerFunction(new cn.langlang.yuweb.functions.database.DbExecFunction(dbManager));
        
        sharedRegistry.registerFunction(new cn.langlang.yuweb.functions.auth.RegisterFunction(dbManager));
        sharedRegistry.registerFunction(new cn.langlang.yuweb.functions.auth.LoginFunction(dbManager));
        
        initialized = true;
        
        long elapsed = System.currentTimeMillis() - startTime;
        logger.info("Shared function registry initialized with {} functions in {}ms", 
                sharedRegistry.getFunctionNames().size(), elapsed);
    }
    
    public static FunctionRegistry getSharedRegistry() {
        return sharedRegistry;
    }
    
    public static boolean isInitialized() {
        return initialized;
    }
    
    public static int getFunctionCount() {
        return sharedRegistry.getFunctionNames().size();
    }
}
