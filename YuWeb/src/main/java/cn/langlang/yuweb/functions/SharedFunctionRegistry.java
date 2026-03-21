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
import cn.langlang.yuweb.functions.server.response.*;
import cn.langlang.yuweb.functions.server.InfoFunction;
import cn.langlang.yuweb.functions.util.*;
import cn.langlang.yuweb.server.YuWebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SharedFunctionRegistry {
    private static final Logger logger = LoggerFactory.getLogger(SharedFunctionRegistry.class);
    
    private static FunctionRegistry sharedRegistry;
    private static boolean initialized = false;
    
    public static synchronized void initialize(YuWebServer server, DatabaseManager dbManager) {
        if (initialized) {
            return;
        }
        
        sharedRegistry = new FunctionRegistry();
        logger.info("Initializing shared function registry...");
        long startTime = System.currentTimeMillis();
        
        registerBuiltinFunctions(sharedRegistry);
        registerWebFunctions(sharedRegistry);
        registerDatabaseFunctions(sharedRegistry, dbManager);
        registerAuthFunctions(sharedRegistry, dbManager);
        registerServerFunctions(sharedRegistry, server);
        
        initialized = true;
        
        long elapsed = System.currentTimeMillis() - startTime;
        logger.info("Shared function registry initialized with {} functions in {}ms", 
                sharedRegistry.getFunctionNames().size(), elapsed);
    }
    
    private static void registerBuiltinFunctions(FunctionRegistry registry) {
        registry.registerFunction(new SysoFunction());
        registry.registerFunction(new TwFunction());
        registry.registerFunction(new SsFunction());
        registry.registerFunction(new SrFunction());
        registry.registerFunction(new SjFunction());
        registry.registerFunction(new SlFunction());
        registry.registerFunction(new SsgFunction());
        registry.registerFunction(new SlgFunction());
        registry.registerFunction(new StrimFunction());
        registry.registerFunction(new SlowerFunction());
        registry.registerFunction(new SupperFunction());
        registry.registerFunction(new SiofFunction());
        registry.registerFunction(new SlofFunction());
        registry.registerFunction(new StobmFunction());
        registry.registerFunction(new Sutf8toFunction());
        registry.registerFunction(new SAddFunction());
        registry.registerFunction(new SSubFunction());
        registry.registerFunction(new SMulFunction());
        registry.registerFunction(new SDivFunction());
        registry.registerFunction(new SModFunction());
        registry.registerFunction(new SFunction());
        registry.registerFunction(new S2Function());
        registry.registerFunction(new SnFunction());
        registry.registerFunction(new SranFunction());
        registry.registerFunction(new NszFunction());
        registry.registerFunction(new SgszFunction());
        registry.registerFunction(new SsszFunction());
        registry.registerFunction(new SgszlFunction());
        registry.registerFunction(new FdFunction());
        registry.registerFunction(new FeFunction());
        registry.registerFunction(new FsFunction());
        registry.registerFunction(new FrFunction());
        registry.registerFunction(new FwFunction());
        registry.registerFunction(new FcFunction());
        registry.registerFunction(new FlFunction());
        registry.registerFunction(new FtFunction());
        registry.registerFunction(new FdirFunction());
        registry.registerFunction(new FuzFunction());
        registry.registerFunction(new FuzsFunction());
        registry.registerFunction(new FjFunction());
        registry.registerFunction(new FoFunction());
        registry.registerFunction(new FiFunction());
        registry.registerFunction(new HsFunction());
        registry.registerFunction(new HdFunction());
        registry.registerFunction(new HdflFunction());
        registry.registerFunction(new HufFunction());
        registry.registerFunction(new HwFunction());
        registry.registerFunction(new HwsFunction());
        registry.registerFunction(new TimeFunction());
        registry.registerFunction(new StopFunction());
        registry.registerFunction(new JavaFunction());
        registry.registerFunction(new JavaxFunction());
        registry.registerFunction(new JavanewFunction());
        registry.registerFunction(new JavagsFunction());
        registry.registerFunction(new JavassFunction());
        registry.registerFunction(new ClsFunction());
        registry.registerFunction(new CallFunction());
        registry.registerFunction(new AslistFunction());
        registry.registerFunction(new SslistFunction());
        registry.registerFunction(new GslistFunction());
        registry.registerFunction(new GslistlFunction());
        registry.registerFunction(new DslistFunction());
        registry.registerFunction(new GslistszFunction());
        registry.registerFunction(new GslistisFunction());
        registry.registerFunction(new GslistiofFunction());
        registry.registerFunction(new GslistlofFunction());
        registry.registerFunction(new SxbFunction());
        registry.registerFunction(new ShbFunction());
    }
    
    private static void registerWebFunctions(FunctionRegistry registry) {
        registry.registerFunction(new MethodFunction());
        registry.registerFunction(new GetFunction());
        registry.registerFunction(new GetsFunction());
        registry.registerFunction(new PostFunction());
        registry.registerFunction(new PostsFunction());
        registry.registerFunction(new FormFunction());
        registry.registerFunction(new FormsFunction());
        registry.registerFunction(new BodyFunction());
        registry.registerFunction(new PathFunction());
        registry.registerFunction(new UrlFunction());
        registry.registerFunction(new HeaderFunction());
        registry.registerFunction(new ClientIpFunction());
        registry.registerFunction(new UserAgentFunction());
        registry.registerFunction(new IsJsonFunction());
        registry.registerFunction(new IsAjaxFunction());
        registry.registerFunction(new GetCookieFunction());
        registry.registerFunction(new SetCookieFunction());
        registry.registerFunction(new DelCookieFunction());
        registry.registerFunction(new FileFunction());
        registry.registerFunction(new FilesFunction());
        
        registry.registerFunction(new JsonFunction());
        registry.registerFunction(new TextFunction());
        registry.registerFunction(new HtmlFunction());
        registry.registerFunction(new ErrorFunction());
        registry.registerFunction(new StatusFunction());
        registry.registerFunction(new SetHeaderFunction());
        registry.registerFunction(new RedirectFunction());
        
        registry.registerFunction(new MapFunction());
        registry.registerFunction(new MgetFunction());
        registry.registerFunction(new MsetFunction());
        registry.registerFunction(new MkeysFunction());
        registry.registerFunction(new MhasFunction());
        registry.registerFunction(new ArrFunction());
        registry.registerFunction(new ArrPushFunction());
        registry.registerFunction(new LengthFunction());
        registry.registerFunction(new JsonEncodeFunction());
        registry.registerFunction(new JsonDecodeFunction());
    }
    
    private static void registerDatabaseFunctions(FunctionRegistry registry, DatabaseManager dbManager) {
        registry.registerFunction(new cn.langlang.yuweb.functions.database.DbFunction(dbManager));
        registry.registerFunction(new cn.langlang.yuweb.functions.database.DbOneFunction(dbManager));
        registry.registerFunction(new cn.langlang.yuweb.functions.database.DbAllFunction(dbManager));
        registry.registerFunction(new cn.langlang.yuweb.functions.database.DbInsertFunction(dbManager));
        registry.registerFunction(new cn.langlang.yuweb.functions.database.DbUpdateFunction(dbManager));
        registry.registerFunction(new cn.langlang.yuweb.functions.database.DbDeleteFunction(dbManager));
        registry.registerFunction(new cn.langlang.yuweb.functions.database.DbPageFunction(dbManager));
        registry.registerFunction(new cn.langlang.yuweb.functions.database.DbCountFunction(dbManager));
        registry.registerFunction(new cn.langlang.yuweb.functions.database.DbExecFunction(dbManager));
        registry.registerFunction(new cn.langlang.yuweb.functions.database.DbSearchFunction(dbManager));
        registry.registerFunction(new cn.langlang.yuweb.functions.database.DbQueryFunction(dbManager));
        
        registry.registerFunction(new InFunction());
        registry.registerFunction(new LikeFunction());
        registry.registerFunction(new BetweenFunction());
        registry.registerFunction(new IsNullFunction());
        registry.registerFunction(new NotNullFunction());
        registry.registerFunction(new AndFunction());
        registry.registerFunction(new OrFunction());
    }
    
    private static void registerAuthFunctions(FunctionRegistry registry, DatabaseManager dbManager) {
        registry.registerFunction(new HashPasswordFunction());
        registry.registerFunction(new VerifyPasswordFunction());
        registry.registerFunction(new VerifyFunction());
        registry.registerFunction(new LogoutFunction());
        registry.registerFunction(new cn.langlang.yuweb.functions.auth.RegisterFunction(dbManager));
        registry.registerFunction(new cn.langlang.yuweb.functions.auth.LoginFunction(dbManager));
    }
    
    private static void registerServerFunctions(FunctionRegistry registry, YuWebServer server) {
        registry.registerFunction(new InfoFunction());
        registry.registerFunction(new cn.langlang.yuweb.functions.server.config.PortFunction(server));
        registry.registerFunction(new cn.langlang.yuweb.functions.server.config.UploadConfigFunction());
        registry.registerFunction(new cn.langlang.yuweb.functions.server.config.ConfigFunction(server));
        
        cn.langlang.yuweb.functions.server.AsyncFunction.setServer(server);
        registry.registerFunction(new cn.langlang.yuweb.functions.server.AsyncFunction());
        registry.registerFunction(new cn.langlang.yuweb.functions.server.AsyncWaitFunction());
    }
    
    public static FunctionRegistry getSharedRegistry() {
        return sharedRegistry;
    }
    
    public static boolean isInitialized() {
        return initialized;
    }
    
    public static int getFunctionCount() {
        return sharedRegistry != null ? sharedRegistry.getFunctionNames().size() : 0;
    }
}
