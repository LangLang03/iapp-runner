package cn.langlang.iapp.lsp.registry;

public enum FunctionCategory {
    CORE("核心函数", "cn.langlang.iapp.functions"),
    STRING("字符串函数", "cn.langlang.iapp.functions.string"),
    MATH("数学函数", "cn.langlang.iapp.functions.math"),
    FILE("文件函数", "cn.langlang.iapp.functions.file"),
    NET("网络函数", "cn.langlang.iapp.functions.net"),
    ARRAY("数组函数", "cn.langlang.iapp.functions.array"),
    LIST("列表函数", "cn.langlang.iapp.functions.list"),
    CLIPBOARD("剪贴板函数", "cn.langlang.iapp.functions.clipboard"),
    TIME("时间函数", "cn.langlang.iapp.functions.time"),
    JAVA("Java交互函数", "cn.langlang.iapp.functions.java"),
    OUTPUT("输出函数", "cn.langlang.iapp.functions.output"),
    OTHER("其他函数", "cn.langlang.iapp.functions.other"),
    WEB_REQUEST("Web请求函数", "cn.langlang.yuweb.functions.server.request"),
    WEB_RESPONSE("Web响应函数", "cn.langlang.yuweb.functions.server.response"),
    WEB_CONFIG("Web配置函数", "cn.langlang.yuweb.functions.server.config"),
    DATABASE("数据库函数", "cn.langlang.yuweb.functions.database"),
    DATABASE_CONDITION("数据库条件函数", "cn.langlang.yuweb.functions.database.condition"),
    CRYPTO("加密函数", "cn.langlang.yuweb.functions.crypto"),
    JWT("JWT函数", "cn.langlang.yuweb.functions.jwt"),
    SESSION("会话函数", "cn.langlang.yuweb.functions.session"),
    AUTH("认证函数", "cn.langlang.yuweb.functions.auth"),
    MAIL("邮件函数", "cn.langlang.yuweb.functions.mail"),
    ENV("环境变量函数", "cn.langlang.yuweb.functions.env"),
    UTIL("工具函数", "cn.langlang.yuweb.functions.util"),
    SERVER("服务器函数", "cn.langlang.yuweb.functions.server");

    private final String displayName;
    private final String packagePrefix;

    FunctionCategory(String displayName, String packagePrefix) {
        this.displayName = displayName;
        this.packagePrefix = packagePrefix;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPackagePrefix() {
        return packagePrefix;
    }

    public static FunctionCategory fromClassName(String className) {
        if (className == null) return CORE;
        
        for (FunctionCategory category : values()) {
            if (className.startsWith(category.getPackagePrefix())) {
                return category;
            }
        }
        return CORE;
    }

    public boolean isYuWebCategory() {
        return this.ordinal() >= WEB_REQUEST.ordinal();
    }
}
