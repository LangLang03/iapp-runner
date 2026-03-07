package cn.langlang.yuweb.functions;

import cn.langlang.iapp.functions.array.NszFunction;
import cn.langlang.iapp.functions.array.SgszFunction;
import cn.langlang.iapp.functions.array.SgszlFunction;
import cn.langlang.iapp.functions.array.SsszFunction;
import cn.langlang.iapp.functions.clipboard.ShbFunction;
import cn.langlang.iapp.functions.clipboard.SxbFunction;
import cn.langlang.iapp.functions.file.*;
import cn.langlang.iapp.functions.java.*;
import cn.langlang.iapp.functions.list.*;
import cn.langlang.iapp.functions.math.*;
import cn.langlang.iapp.functions.net.*;
import cn.langlang.iapp.functions.other.CallFunction;
import cn.langlang.iapp.functions.other.StopFunction;
import cn.langlang.iapp.functions.output.SysoFunction;
import cn.langlang.iapp.functions.output.TwFunction;
import cn.langlang.iapp.functions.string.*;
import cn.langlang.iapp.functions.time.TimeFunction;
import cn.langlang.iapp.runtime.FunctionRegistry;
import cn.langlang.iapp.runtime.IFunction;
import cn.langlang.yuweb.database.DatabaseManager;
import cn.langlang.yuweb.functions.auth.HashPasswordFunction;
import cn.langlang.yuweb.functions.auth.LogoutFunction;
import cn.langlang.yuweb.functions.auth.VerifyFunction;
import cn.langlang.yuweb.functions.auth.VerifyPasswordFunction;
import cn.langlang.yuweb.functions.database.condition.*;
import cn.langlang.yuweb.functions.server.request.*;
import cn.langlang.yuweb.functions.util.*;
import cn.langlang.yuweb.server.YuWebServer;
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
        functionSuppliers.add(SysoFunction::new);
        functionSuppliers.add(TwFunction::new);
        functionSuppliers.add(SsFunction::new);
        functionSuppliers.add(SrFunction::new);
        functionSuppliers.add(SjFunction::new);
        functionSuppliers.add(SlFunction::new);
        functionSuppliers.add(SsgFunction::new);
        functionSuppliers.add(SlgFunction::new);
        functionSuppliers.add(StrimFunction::new);
        functionSuppliers.add(SlowerFunction::new);
        functionSuppliers.add(SupperFunction::new);
        functionSuppliers.add(SiofFunction::new);
        functionSuppliers.add(SlofFunction::new);
        functionSuppliers.add(StobmFunction::new);
        functionSuppliers.add(Sutf8toFunction::new);
        functionSuppliers.add(SAddFunction::new);
        functionSuppliers.add(SSubFunction::new);
        functionSuppliers.add(SMulFunction::new);
        functionSuppliers.add(SDivFunction::new);
        functionSuppliers.add(SModFunction::new);
        functionSuppliers.add(SFunction::new);
        functionSuppliers.add(S2Function::new);
        functionSuppliers.add(SnFunction::new);
        functionSuppliers.add(SranFunction::new);
        functionSuppliers.add(NszFunction::new);
        functionSuppliers.add(SgszFunction::new);
        functionSuppliers.add(SsszFunction::new);
        functionSuppliers.add(SgszlFunction::new);
        functionSuppliers.add(FdFunction::new);
        functionSuppliers.add(FeFunction::new);
        functionSuppliers.add(FsFunction::new);
        functionSuppliers.add(FrFunction::new);
        functionSuppliers.add(FwFunction::new);
        functionSuppliers.add(FcFunction::new);
        functionSuppliers.add(FlFunction::new);
        functionSuppliers.add(FtFunction::new);
        functionSuppliers.add(FdirFunction::new);
        functionSuppliers.add(FuzFunction::new);
        functionSuppliers.add(FuzsFunction::new);
        functionSuppliers.add(FjFunction::new);
        functionSuppliers.add(FoFunction::new);
        functionSuppliers.add(FiFunction::new);
        functionSuppliers.add(HsFunction::new);
        functionSuppliers.add(HdFunction::new);
        functionSuppliers.add(HdflFunction::new);
        functionSuppliers.add(HufFunction::new);
        functionSuppliers.add(HwFunction::new);
        functionSuppliers.add(HwsFunction::new);
        functionSuppliers.add(TimeFunction::new);
        functionSuppliers.add(StopFunction::new);
        functionSuppliers.add(JavaFunction::new);
        functionSuppliers.add(JavaxFunction::new);
        functionSuppliers.add(JavanewFunction::new);
        functionSuppliers.add(JavagsFunction::new);
        functionSuppliers.add(JavassFunction::new);
        functionSuppliers.add(ClsFunction::new);
        functionSuppliers.add(CallFunction::new);
        functionSuppliers.add(AslistFunction::new);
        functionSuppliers.add(SslistFunction::new);
        functionSuppliers.add(GslistFunction::new);
        functionSuppliers.add(GslistlFunction::new);
        functionSuppliers.add(DslistFunction::new);
        functionSuppliers.add(GslistszFunction::new);
        functionSuppliers.add(GslistisFunction::new);
        functionSuppliers.add(GslistiofFunction::new);
        functionSuppliers.add(GslistlofFunction::new);
        functionSuppliers.add(SxbFunction::new);
        functionSuppliers.add(ShbFunction::new);
        
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
        
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.FileFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.request.FilesFunction(null));
        functionSuppliers.add(GfnFunction::new);
        functionSuppliers.add(GfsFunction::new);
        functionSuppliers.add(GftFunction::new);
        functionSuppliers.add(GfeFunction::new);
        functionSuppliers.add(SfFunction::new);
        
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.response.JsonFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.response.TextFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.response.HtmlFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.response.ErrorFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.response.StatusFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.response.SetHeaderFunction(null));
        functionSuppliers.add(() -> new cn.langlang.yuweb.functions.server.response.RedirectFunction(null));
        
        functionSuppliers.add(MapFunction::new);
        functionSuppliers.add(MgetFunction::new);
        functionSuppliers.add(MsetFunction::new);
        functionSuppliers.add(MkeysFunction::new);
        functionSuppliers.add(MhasFunction::new);
        functionSuppliers.add(ArrFunction::new);
        functionSuppliers.add(ArrPushFunction::new);
        functionSuppliers.add(LengthFunction::new);
        functionSuppliers.add(JsonEncodeFunction::new);
        functionSuppliers.add(JsonDecodeFunction::new);
        
        functionSuppliers.add(InFunction::new);
        functionSuppliers.add(LikeFunction::new);
        functionSuppliers.add(BetweenFunction::new);
        functionSuppliers.add(IsNullFunction::new);
        functionSuppliers.add(NotNullFunction::new);
        functionSuppliers.add(AndFunction::new);
        functionSuppliers.add(OrFunction::new);
        
        functionSuppliers.add(HashPasswordFunction::new);
        functionSuppliers.add(VerifyPasswordFunction::new);
        functionSuppliers.add(VerifyFunction::new);
        functionSuppliers.add(LogoutFunction::new);
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
        sharedRegistry.registerFunction(new cn.langlang.yuweb.functions.database.DbSearchFunction(dbManager));
        sharedRegistry.registerFunction(new cn.langlang.yuweb.functions.database.DbQueryFunction(dbManager));
        
        sharedRegistry.registerFunction(new cn.langlang.yuweb.functions.auth.RegisterFunction(dbManager));
        sharedRegistry.registerFunction(new cn.langlang.yuweb.functions.auth.LoginFunction(dbManager));
        
        sharedRegistry.registerFunction(new cn.langlang.yuweb.functions.server.config.UploadConfigFunction());
        
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
