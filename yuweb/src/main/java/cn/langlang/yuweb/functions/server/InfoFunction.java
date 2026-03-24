package cn.langlang.yuweb.functions.server;

import cn.langlang.iapp.runtime.AbstractFunction;
import cn.langlang.iapp.runtime.FunctionException;
import cn.langlang.iapp.runtime.ParamType;
import cn.langlang.iapp.runtime.RuntimeContext;
import cn.langlang.yuweb.server.RouteHandler;
import cn.langlang.yuweb.server.YuWebServer;
import cn.langlang.yuweb.web.RequestContext;
import cn.langlang.yuweb.cache.ScriptCache;
import cn.langlang.yuweb.cache.ScriptPreloader;
import cn.langlang.yuweb.monitor.PerformanceMonitor;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class InfoFunction extends AbstractFunction {
    
    @Override
    public String getName() {
        return "info";
    }
    
    @Override
    public int getMinParameters() {
        return 0;
    }
    
    @Override
    public int getMaxParameters() {
        return 0;
    }
    
    @Override
    public List<ParamType> getParamTypes() {
        return types();
    }
    
    @Override
    public Object call(RuntimeContext context, List<Object> arguments) throws FunctionException {
        RequestContext requestContext = context.getRequestContext();
        if (requestContext == null) {
            return null;
        }
        
        YuWebServer server = requestContext.getServer();
        String html = generateInfoHtml(context, server);
        requestContext.html(html);
        return null;
    }
    
    private String generateInfoHtml(RuntimeContext context, YuWebServer server) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"zh-CN\">\n");
        sb.append("<head>\n");
        sb.append("  <meta charset=\"UTF-8\">\n");
        sb.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        sb.append("  <title>YuWeb Info</title>\n");
        sb.append("  <style>\n");
        sb.append(getStyles());
        sb.append("  </style>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        
        sb.append("<div class=\"header\">\n");
        sb.append("  <h1>YuWeb Server Info</h1>\n");
        sb.append("  <p class=\"version\">").append(server.getConfig().getServerSignature()).append("</p>\n");
        sb.append("</div>\n");
        
        sb.append("<div class=\"content\">\n");
        
        appendSection(sb, "服务器信息", getServerInfo(server));
        appendSection(sb, "Java 环境", getJavaInfo());
        appendSection(sb, "内存使用", getMemoryInfo());
        appendSection(sb, "线程信息", getThreadInfo());
        appendSection(sb, "性能监控", getPerformanceInfo());
        appendSection(sb, "脚本缓存", getCacheInfo());
        appendSection(sb, "已注册函数", getFunctionInfo(context));
        appendSection(sb, "系统属性", getSystemProperties());
        
        sb.append("</div>\n");
        
        sb.append("<div class=\"footer\">\n");
        sb.append("  <p>Generated at: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("</p>\n");
        sb.append("</div>\n");
        
        sb.append("</body>\n");
        sb.append("</html>\n");
        
        return sb.toString();
    }
    
    private String getStyles() {
        return """
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background: #f5f5f5; color: #333; line-height: 1.6; }
            .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px 20px; text-align: center; }
            .header h1 { font-size: 2.5em; margin-bottom: 10px; }
            .header .version { font-size: 1.2em; opacity: 0.9; }
            .content { max-width: 1200px; margin: 0 auto; padding: 20px; }
            .section { background: white; border-radius: 8px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); overflow: hidden; }
            .section-header { background: #f8f9fa; padding: 15px 20px; border-bottom: 1px solid #e9ecef; cursor: pointer; display: flex; justify-content: space-between; align-items: center; }
            .section-header h2 { font-size: 1.3em; color: #495057; }
            .section-header .toggle { font-size: 1.2em; color: #6c757d; }
            .section-body { padding: 20px; }
            table { width: 100%; border-collapse: collapse; }
            th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #e9ecef; }
            th { background: #f8f9fa; font-weight: 600; width: 200px; color: #495057; }
            tr:hover { background: #f8f9fa; }
            .value { font-family: 'Consolas', 'Monaco', monospace; color: #e83e8c; word-break: break-all; }
            .good { color: #28a745; }
            .warning { color: #ffc107; }
            .danger { color: #dc3545; }
            .progress-bar { background: #e9ecef; border-radius: 4px; height: 20px; overflow: hidden; margin-top: 5px; }
            .progress-fill { height: 100%; transition: width 0.3s ease; }
            .progress-fill.green { background: #28a745; }
            .progress-fill.yellow { background: #ffc107; }
            .progress-fill.red { background: #dc3545; }
            .function-list { display: flex; flex-wrap: wrap; gap: 8px; }
            .function-tag { background: #e9ecef; padding: 4px 10px; border-radius: 4px; font-family: monospace; font-size: 0.9em; }
            .footer { text-align: center; padding: 20px; color: #6c757d; }
            .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; }
            .stat-card { background: white; border-radius: 8px; padding: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
            .stat-card h3 { color: #6c757d; font-size: 0.9em; margin-bottom: 10px; text-transform: uppercase; }
            .stat-card .value { font-size: 2em; font-weight: bold; color: #333; }
            .stat-card .unit { font-size: 0.5em; color: #6c757d; }
            """;
    }
    
    private Map<String, String> getServerInfo(YuWebServer server) {
        Map<String, String> info = new TreeMap<>();
        
        info.put("服务器名称", server.getConfig().getServerName());
        info.put("服务器版本", server.getConfig().getServerVersion());
        info.put("调试模式", server.getConfig().isDebugMode() ? "开启" : "关闭");
        info.put("安全模式", server.getConfig().isSafeMode() ? "开启" : "关闭");
        info.put("脚本预加载", server.getConfig().isPreloadScripts() ? "开启" : "关闭");
        info.put("静态文件服务", server.getConfig().isServeStaticFiles() ? "开启" : "关闭");
        info.put("服务端口", String.valueOf(server.getPort()));
        info.put("项目路径", server.getProjectPath());
        info.put("服务器启动时间", getStartTime());
        
        return info;
    }
    
    private Map<String, String> getJavaInfo() {
        Map<String, String> info = new TreeMap<>();
        Runtime runtime = Runtime.getRuntime();
        
        info.put("Java 版本", System.getProperty("java.version"));
        info.put("Java 供应商", System.getProperty("java.vendor"));
        info.put("Java 主目录", System.getProperty("java.home"));
        info.put("JVM 名称", System.getProperty("java.vm.name"));
        info.put("JVM 版本", System.getProperty("java.vm.version"));
        info.put("操作系统", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        info.put("系统架构", System.getProperty("os.arch"));
        info.put("用户目录", System.getProperty("user.dir"));
        info.put("可用处理器", String.valueOf(runtime.availableProcessors()));
        
        return info;
    }
    
    private Map<String, Object> getMemoryInfo() {
        Map<String, Object> info = new TreeMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        info.put("最大可用内存", formatBytes(maxMemory));
        info.put("已分配内存", formatBytes(totalMemory));
        info.put("已使用内存", formatBytes(usedMemory));
        info.put("空闲内存", formatBytes(freeMemory));
        info.put("内存使用率", String.format("%.1f%%", (double) usedMemory / maxMemory * 100));
        info.put("内存使用率(数值)", (double) usedMemory / maxMemory);
        
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        info.put("堆内存使用", formatBytes(memoryBean.getHeapMemoryUsage().getUsed()));
        info.put("非堆内存使用", formatBytes(memoryBean.getNonHeapMemoryUsage().getUsed()));
        
        return info;
    }
    
    private Map<String, String> getThreadInfo() {
        Map<String, String> info = new TreeMap<>();
        
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        
        info.put("活动线程数", String.valueOf(threadBean.getThreadCount()));
        info.put("峰值线程数", String.valueOf(threadBean.getPeakThreadCount()));
        info.put("守护线程数", String.valueOf(threadBean.getDaemonThreadCount()));
        info.put("已启动线程总数", String.valueOf(threadBean.getTotalStartedThreadCount()));
        
        return info;
    }
    
    private Map<String, Object> getPerformanceInfo() {
        Map<String, Object> info = new TreeMap<>();
        
        PerformanceMonitor monitor = RouteHandler.getPerformanceMonitor();
        
        info.put("监控状态", monitor.isEnabled() ? "启用" : "禁用");
        
        long requestCount = monitor.getCounter("request.count");
        long cacheHit = monitor.getCounter("cache.hit");
        long cacheMiss = monitor.getCounter("cache.miss");
        
        info.put("请求总数", requestCount);
        info.put("缓存命中次数", cacheHit);
        info.put("缓存未命中次数", cacheMiss);
        
        if (cacheHit + cacheMiss > 0) {
            double hitRate = (double) cacheHit / (cacheHit + cacheMiss) * 100;
            info.put("缓存命中率", String.format("%.2f%%", hitRate));
        }
        
        info.put("平均请求时间", String.format("%.2f ms", monitor.getAverageTime("request.time")));
        info.put("最大请求时间", monitor.getMaxTime("request.time") + " ms");
        info.put("最小请求时间", monitor.getMinTime("request.time") + " ms");
        info.put("平均编译时间", String.format("%.2f ms", monitor.getAverageTime("script.compile")));
        info.put("平均执行时间", String.format("%.2f ms", monitor.getAverageTime("script.execute")));
        
        return info;
    }
    
    private Map<String, Object> getCacheInfo() {
        Map<String, Object> info = new TreeMap<>();
        
        ScriptCache.CacheStats stats = RouteHandler.getCacheStats();
        if (stats != null) {
            info.put("缓存脚本数", stats.getSize());
            info.put("命中次数", stats.getHitCount());
            info.put("未命中次数", stats.getMissCount());
            info.put("命中率", String.format("%.2f%%", stats.getHitRate() * 100));
        } else {
            info.put("状态", "未初始化");
        }
        
        ScriptPreloader.PreloadStats preloadStats = RouteHandler.getPreloadStats();
        if (preloadStats != null) {
            info.put("预加载脚本总数", preloadStats.getTotalScripts());
            info.put("预加载成功数", preloadStats.getSuccessCount());
            info.put("预加载失败数", preloadStats.getErrorCount());
            info.put("预加载总耗时", preloadStats.getTotalCompileTime() + " ms");
            info.put("预加载平均耗时", String.format("%.2f ms", preloadStats.getAverageCompileTime()));
        }
        
        return info;
    }
    
    private Map<String, String> getFunctionInfo(RuntimeContext context) {
        Map<String, String> info = new TreeMap<>();
        
        int totalFunctions = context.getFunctionRegistry().getFunctionNames().size();
        int userFunctions = context.getUserFunctions().size();
        
        info.put("内置函数数量", String.valueOf(totalFunctions));
        info.put("用户函数数量", String.valueOf(userFunctions));
        
        StringBuilder sb = new StringBuilder();
        for (String name : context.getFunctionRegistry().getFunctionNames()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(name);
        }
        info.put("函数列表", sb.toString());
        
        return info;
    }
    
    private Map<String, String> getSystemProperties() {
        Map<String, String> props = new TreeMap<>();
        
        props.put("java.version", System.getProperty("java.version"));
        props.put("java.vendor", System.getProperty("java.vendor"));
        props.put("java.vendor.url", System.getProperty("java.vendor.url"));
        props.put("java.home", System.getProperty("java.home"));
        props.put("java.vm.specification.version", System.getProperty("java.vm.specification.version"));
        props.put("java.vm.specification.vendor", System.getProperty("java.vm.specification.vendor"));
        props.put("java.vm.specification.name", System.getProperty("java.vm.specification.name"));
        props.put("java.vm.version", System.getProperty("java.vm.version"));
        props.put("java.vm.vendor", System.getProperty("java.vm.vendor"));
        props.put("java.vm.name", System.getProperty("java.vm.name"));
        props.put("java.specification.version", System.getProperty("java.specification.version"));
        props.put("java.specification.vendor", System.getProperty("java.specification.vendor"));
        props.put("java.specification.name", System.getProperty("java.specification.name"));
        props.put("java.class.version", System.getProperty("java.class.version"));
        props.put("java.class.path", System.getProperty("java.class.path"));
        props.put("java.library.path", System.getProperty("java.library.path"));
        props.put("java.io.tmpdir", System.getProperty("java.io.tmpdir"));
        props.put("java.compiler", System.getProperty("java.compiler"));
        props.put("os.name", System.getProperty("os.name"));
        props.put("os.arch", System.getProperty("os.arch"));
        props.put("os.version", System.getProperty("os.version"));
        props.put("file.separator", System.getProperty("file.separator"));
        props.put("path.separator", System.getProperty("path.separator"));
        props.put("line.separator", System.getProperty("line.separator").replace("\n", "\\n").replace("\r", "\\r"));
        props.put("user.name", System.getProperty("user.name"));
        props.put("user.home", System.getProperty("user.home"));
        props.put("user.dir", System.getProperty("user.dir"));
        
        return props;
    }
    
    private void appendSection(StringBuilder sb, String title, Map<String, ?> data) {
        sb.append("<div class=\"section\">\n");
        sb.append("  <div class=\"section-header\">\n");
        sb.append("    <h2>").append(title).append("</h2>\n");
        sb.append("    <span class=\"toggle\">▼</span>\n");
        sb.append("  </div>\n");
        sb.append("  <div class=\"section-body\">\n");
        sb.append("    <table>\n");
        
        for (Map.Entry<String, ?> entry : data.entrySet()) {
            sb.append("      <tr>\n");
            sb.append("        <th>").append(entry.getKey()).append("</th>\n");
            sb.append("        <td>");
            
            Object value = entry.getValue();
            if (value instanceof Double && entry.getKey().contains("内存使用率")) {
                double rate = (Double) value;
                String colorClass = rate < 0.6 ? "green" : (rate < 0.8 ? "yellow" : "red");
                sb.append("<div class=\"progress-bar\"><div class=\"progress-fill ").append(colorClass)
                  .append("\" style=\"width: ").append(rate * 100).append("%\"></div></div>");
            } else {
                sb.append("<span class=\"value\">").append(escapeHtml(String.valueOf(value))).append("</span>");
            }
            
            sb.append("</td>\n");
            sb.append("      </tr>\n");
        }
        
        sb.append("    </table>\n");
        sb.append("  </div>\n");
        sb.append("</div>\n");
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    private String getStartTime() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        long startTime = runtimeBean.getStartTime();
        long uptime = runtimeBean.getUptime();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDate = sdf.format(new Date(startTime));
        
        long hours = uptime / (1000 * 60 * 60);
        long minutes = (uptime % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (uptime % (1000 * 60)) / 1000;
        
        return startDate + " (运行 " + hours + "时" + minutes + "分" + seconds + "秒)";
    }
    
    private String escapeHtml(String text) {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }
}
