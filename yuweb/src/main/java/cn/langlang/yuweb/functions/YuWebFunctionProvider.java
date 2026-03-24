package cn.langlang.yuweb.functions;

import cn.langlang.iapp.api.FunctionRegistryProvider;
import cn.langlang.iapp.runtime.FunctionRegistry;
import cn.langlang.yuweb.functions.auth.HashPasswordFunction;
import cn.langlang.yuweb.functions.auth.LogoutFunction;
import cn.langlang.yuweb.functions.auth.VerifyFunction;
import cn.langlang.yuweb.functions.auth.VerifyPasswordFunction;
import cn.langlang.yuweb.functions.crypto.*;
import cn.langlang.yuweb.functions.database.condition.*;
import cn.langlang.yuweb.functions.env.EnvFunction;
import cn.langlang.yuweb.functions.env.LoadEnvFunction;
import cn.langlang.yuweb.functions.jwt.JwtDecodeFunction;
import cn.langlang.yuweb.functions.jwt.JwtEncodeFunction;
import cn.langlang.yuweb.functions.jwt.JwtVerifyFunction;
import cn.langlang.yuweb.functions.mail.MailConfigFunction;
import cn.langlang.yuweb.functions.mail.SendMailFunction;
import cn.langlang.yuweb.functions.server.InfoFunction;
import cn.langlang.yuweb.functions.server.request.*;
import cn.langlang.yuweb.functions.server.response.*;
import cn.langlang.yuweb.functions.session.DelSessionFunction;
import cn.langlang.yuweb.functions.session.HasSessionFunction;
import cn.langlang.yuweb.functions.session.SessionFunction;
import cn.langlang.yuweb.functions.session.SetSessionFunction;
import cn.langlang.yuweb.functions.util.*;
import cn.langlang.yuweb.functions.cors.CorsFunction;

import java.util.HashMap;
import java.util.Map;

public class YuWebFunctionProvider implements FunctionRegistryProvider {
    
    @Override
    public String getProviderName() {
        return "YuWeb";
    }
    
    @Override
    public void registerFunctions(FunctionRegistry registry) {
        registerRequestFunctions(registry);
        registerResponseFunctions(registry);
        registerSessionFunctions(registry);
        registerCryptoFunctions(registry);
        registerJwtFunctions(registry);
        registerEnvFunctions(registry);
        registerMailFunctions(registry);
        registerUtilFunctions(registry);
        registerConditionFunctions(registry);
        registerAuthFunctions(registry);
        registerServerFunctions(registry);
    }
    
    private void registerRequestFunctions(FunctionRegistry registry) {
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
        registry.registerFunction(new ParamFunction());
        registry.registerFunction(new ParamsFunction());
    }
    
    private void registerResponseFunctions(FunctionRegistry registry) {
        registry.registerFunction(new JsonFunction());
        registry.registerFunction(new TextFunction());
        registry.registerFunction(new HtmlFunction());
        registry.registerFunction(new ErrorFunction());
        registry.registerFunction(new StatusFunction());
        registry.registerFunction(new SetHeaderFunction());
        registry.registerFunction(new RedirectFunction());
    }
    
    private void registerSessionFunctions(FunctionRegistry registry) {
        registry.registerFunction(new SessionFunction());
        registry.registerFunction(new SetSessionFunction());
        registry.registerFunction(new DelSessionFunction());
        registry.registerFunction(new HasSessionFunction());
    }
    
    private void registerCryptoFunctions(FunctionRegistry registry) {
        registry.registerFunction(new Md5Function());
        registry.registerFunction(new Sha256Function());
        registry.registerFunction(new Sha1Function());
        registry.registerFunction(new Base64EncodeFunction());
        registry.registerFunction(new Base64DecodeFunction());
        registry.registerFunction(new AesEncryptFunction());
        registry.registerFunction(new AesDecryptFunction());
        registry.registerFunction(new HmacSha256Function());
    }
    
    private void registerJwtFunctions(FunctionRegistry registry) {
        registry.registerFunction(new JwtEncodeFunction());
        registry.registerFunction(new JwtDecodeFunction());
        registry.registerFunction(new JwtVerifyFunction());
    }
    
    private void registerEnvFunctions(FunctionRegistry registry) {
        registry.registerFunction(new EnvFunction());
        registry.registerFunction(new LoadEnvFunction());
    }
    
    private void registerMailFunctions(FunctionRegistry registry) {
        registry.registerFunction(new MailConfigFunction());
        registry.registerFunction(new SendMailFunction());
    }
    
    private void registerUtilFunctions(FunctionRegistry registry) {
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
    
    private void registerConditionFunctions(FunctionRegistry registry) {
        registry.registerFunction(new InFunction());
        registry.registerFunction(new LikeFunction());
        registry.registerFunction(new BetweenFunction());
        registry.registerFunction(new IsNullFunction());
        registry.registerFunction(new NotNullFunction());
        registry.registerFunction(new AndFunction());
        registry.registerFunction(new OrFunction());
    }
    
    private void registerAuthFunctions(FunctionRegistry registry) {
        registry.registerFunction(new HashPasswordFunction());
        registry.registerFunction(new VerifyPasswordFunction());
        registry.registerFunction(new VerifyFunction());
        registry.registerFunction(new LogoutFunction());
    }
    
    private void registerServerFunctions(FunctionRegistry registry) {
        registry.registerFunction(new InfoFunction());
        registry.registerFunction(new CorsFunction());
    }
    
    @Override
    public Map<String, String> getFunctionCategories() {
        Map<String, String> categories = new HashMap<>();
        
        categories.put("method", "request");
        categories.put("get", "request");
        categories.put("gets", "request");
        categories.put("post", "request");
        categories.put("posts", "request");
        categories.put("form", "request");
        categories.put("forms", "request");
        categories.put("body", "request");
        categories.put("path", "request");
        categories.put("url", "request");
        categories.put("header", "request");
        categories.put("clientip", "request");
        categories.put("useragent", "request");
        categories.put("isjson", "request");
        categories.put("isajax", "request");
        categories.put("getcookie", "request");
        categories.put("setcookie", "request");
        categories.put("delcookie", "request");
        categories.put("file", "request");
        categories.put("files", "request");
        categories.put("param", "request");
        categories.put("params", "request");
        
        categories.put("json", "response");
        categories.put("text", "response");
        categories.put("html", "response");
        categories.put("error", "response");
        categories.put("status", "response");
        categories.put("setheader", "response");
        categories.put("redirect", "response");
        
        categories.put("session", "session");
        categories.put("setsession", "session");
        categories.put("delsession", "session");
        categories.put("hassession", "session");
        
        categories.put("md5", "crypto");
        categories.put("sha256", "crypto");
        categories.put("sha1", "crypto");
        categories.put("base64encode", "crypto");
        categories.put("base64decode", "crypto");
        categories.put("aesencrypt", "crypto");
        categories.put("aesdecrypt", "crypto");
        categories.put("hmacsha256", "crypto");
        
        categories.put("jwtencode", "jwt");
        categories.put("jwtdecode", "jwt");
        categories.put("jwtverify", "jwt");
        
        categories.put("env", "env");
        categories.put("loadenv", "env");
        
        categories.put("mailconfig", "mail");
        categories.put("sendmail", "mail");
        
        categories.put("map", "util");
        categories.put("mget", "util");
        categories.put("mset", "util");
        categories.put("mkeys", "util");
        categories.put("mhas", "util");
        categories.put("arr", "util");
        categories.put("arrpush", "util");
        categories.put("length", "util");
        categories.put("jsonencode", "util");
        categories.put("jsondecode", "util");
        
        categories.put("in", "condition");
        categories.put("like", "condition");
        categories.put("between", "condition");
        categories.put("isnull", "condition");
        categories.put("notnull", "condition");
        categories.put("and", "condition");
        categories.put("or", "condition");
        
        categories.put("hashpassword", "auth");
        categories.put("verifypassword", "auth");
        categories.put("verify", "auth");
        categories.put("logout", "auth");
        
        categories.put("info", "server");
        categories.put("cors", "server");
        
        return categories;
    }
    
    @Override
    public int getPriority() {
        return 50;
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
}
