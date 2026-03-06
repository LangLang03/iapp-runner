package cn.langlang.yuweb;

import cn.langlang.iapp.api.IAppScript;
import cn.langlang.iapp.runtime.RuntimeContext;
import io.javalin.http.Context;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class RouteHandler {
    private YuWebServer server;
    private static final DatabaseManager dbManager = new DatabaseManager();
    private static final Map<String, Object> globalVariables = new HashMap<>();
    
    public RouteHandler(YuWebServer server) {
        this.server = server;
    }
    
    public void handle(String scriptPath, Context ctx) throws Exception {
        String source = readFile(scriptPath);
        if (source == null) {
            ctx.status(404).result("Script not found");
            return;
        }
        
        RequestContext requestCtx = new RequestContext(ctx, server);
        server.setCurrentContext(requestCtx);
        
        IAppScript script = IAppScript.create();
        
        RuntimeContext runtimeContext = script.getContext();
        
        registerServerFunctions(runtimeContext, requestCtx);
        registerDatabaseFunctions(runtimeContext);
        registerAuthFunctions(runtimeContext);
        registerUtilFunctions(runtimeContext);
        
        for (Map.Entry<String, Object> entry : globalVariables.entrySet()) {
            runtimeContext.setVariable(entry.getKey(), entry.getValue());
        }
        
        script.loadString(source);
        script.eval();
    }
    
    public void executeAppConfig(String appFile) throws Exception {
        String source = readFile(appFile);
        if (source == null) {
            return;
        }
        
        IAppScript script = IAppScript.create();
        RuntimeContext runtimeContext = script.getContext();
        
        registerServerFunctions(runtimeContext, null);
        registerDatabaseFunctions(runtimeContext);
        registerUtilFunctions(runtimeContext);
        
        script.loadString(source);
        script.eval();
    }
    
    private String readFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }
            return new String(Files.readAllBytes(file.toPath()), java.nio.charset.StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }
    
    private void registerServerFunctions(RuntimeContext context, RequestContext requestCtx) {
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.MethodFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.GetFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.GetsFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.PostFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.PostsFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.FormFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.FormsFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.BodyFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.PathFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.UrlFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.HeaderFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.ClientIpFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.UserAgentFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.IsJsonFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.IsAjaxFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.GetCookieFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.SetCookieFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.request.DelCookieFunction(requestCtx));
        
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.response.JsonFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.response.TextFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.response.HtmlFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.response.ErrorFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.response.StatusFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.response.SetHeaderFunction(requestCtx));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.response.RedirectFunction(requestCtx));
        
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.server.config.PortFunction(server));
    }
    
    private void registerDatabaseFunctions(RuntimeContext context) {
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.database.DbFunction(dbManager));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.database.DbOneFunction(dbManager));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.database.DbAllFunction(dbManager));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.database.DbInsertFunction(dbManager));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.database.DbUpdateFunction(dbManager));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.database.DbDeleteFunction(dbManager));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.database.DbPageFunction(dbManager));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.database.DbCountFunction(dbManager));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.database.DbExecFunction(dbManager));
        
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.database.condition.InFunction());
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.database.condition.LikeFunction());
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.database.condition.BetweenFunction());
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.database.condition.IsNullFunction());
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.database.condition.NotNullFunction());
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.database.condition.AndFunction());
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.database.condition.OrFunction());
    }
    
    private void registerAuthFunctions(RuntimeContext context) {
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.auth.RegisterFunction(dbManager));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.auth.LoginFunction(dbManager));
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.auth.VerifyFunction());
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.auth.LogoutFunction());
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.auth.HashPasswordFunction());
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.auth.VerifyPasswordFunction());
    }
    
    private void registerUtilFunctions(RuntimeContext context) {
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.util.MapFunction());
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.util.MgetFunction());
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.util.MsetFunction());
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.util.MkeysFunction());
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.util.MhasFunction());
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.util.ArrFunction());
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.util.ArrPushFunction());
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.util.LengthFunction());
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.util.JsonEncodeFunction());
        context.getFunctionRegistry().registerFunction(new cn.langlang.yuweb.functions.util.JsonDecodeFunction());
    }
    
    public DatabaseManager getDbManager() {
        return dbManager;
    }
}
