<template>
  <div class="config-page">
    <section class="page-header">
      <div class="container">
        <h1 class="page-title">配置指南</h1>
        <p class="page-desc">服务器配置与环境变量管理</p>
      </div>
    </section>

    <section class="section">
      <div class="container">
        <h2 class="section-title">启动参数</h2>
        <div class="table-container">
          <table>
            <thead>
              <tr>
                <th>参数</th>
                <th>简写</th>
                <th>说明</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td><code>--debug</code></td>
                <td><code>-d</code></td>
                <td>启用调试模式，显示详细错误信息</td>
              </tr>
              <tr>
                <td><code>--safe</code></td>
                <td><code>-s</code></td>
                <td>启用安全模式，仅执行预加载的脚本（自动启用 --preload）</td>
              </tr>
              <tr>
                <td><code>--preload</code></td>
                <td><code>-p</code></td>
                <td>启动时预加载所有 .iapp 脚本</td>
              </tr>
              <tr>
                <td><code>--no-static</code></td>
                <td>-</td>
                <td>禁用静态文件服务</td>
              </tr>
              <tr>
                <td><code>--port &lt;端口&gt;</code></td>
                <td>-</td>
                <td>指定服务端口（默认: 8080）</td>
              </tr>
              <tr>
                <td><code>--help</code></td>
                <td><code>-h</code></td>
                <td>显示帮助信息</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </section>

    <section class="section bg-secondary">
      <div class="container">
        <h2 class="section-title">config() 配置项</h2>
        <p class="section-subtitle">通过 config(key, value) 函数设置服务器各项参数</p>
        
        <h3 class="config-category">基本配置</h3>
        <div class="table-container">
          <table>
            <thead>
              <tr>
                <th>配置项</th>
                <th>类型</th>
                <th>默认值</th>
                <th>说明</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td><code>port</code></td>
                <td>Number</td>
                <td>8080</td>
                <td>服务端口（1-65535）</td>
              </tr>
              <tr>
                <td><code>debug</code></td>
                <td>Boolean</td>
                <td>false</td>
                <td>调试模式</td>
              </tr>
              <tr>
                <td><code>safe</code></td>
                <td>Boolean</td>
                <td>false</td>
                <td>安全模式</td>
              </tr>
              <tr>
                <td><code>preload</code></td>
                <td>Boolean</td>
                <td>false</td>
                <td>脚本预加载</td>
              </tr>
              <tr>
                <td><code>static</code></td>
                <td>Boolean</td>
                <td>true</td>
                <td>静态文件服务</td>
              </tr>
            </tbody>
          </table>
        </div>

        <h3 class="config-category">连接池配置</h3>
        <div class="table-container">
          <table>
            <thead>
              <tr>
                <th>配置项</th>
                <th>类型</th>
                <th>默认值</th>
                <th>说明</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td><code>poolsize</code></td>
                <td>Number</td>
                <td>100</td>
                <td>连接池最大大小</td>
              </tr>
              <tr>
                <td><code>poolinit</code></td>
                <td>Number</td>
                <td>10</td>
                <td>连接池初始大小</td>
              </tr>
              <tr>
                <td><code>pooltimeout</code></td>
                <td>Number</td>
                <td>30000</td>
                <td>连接超时时间（毫秒）</td>
              </tr>
              <tr>
                <td><code>usepool</code></td>
                <td>Boolean</td>
                <td>true</td>
                <td>是否使用连接池</td>
              </tr>
            </tbody>
          </table>
        </div>

        <h3 class="config-category">HTTP 配置</h3>
        <div class="table-container">
          <table>
            <thead>
              <tr>
                <th>配置项</th>
                <th>类型</th>
                <th>默认值</th>
                <th>说明</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td><code>http2</code></td>
                <td>Boolean</td>
                <td>true</td>
                <td>启用 HTTP/2 优化</td>
              </tr>
              <tr>
                <td><code>compression</code></td>
                <td>Boolean</td>
                <td>true</td>
                <td>启用响应压缩</td>
              </tr>
              <tr>
                <td><code>compression_min</code></td>
                <td>Number</td>
                <td>1024</td>
                <td>最小压缩大小（字节）</td>
              </tr>
              <tr>
                <td><code>max_upload_size</code></td>
                <td>Number</td>
                <td>10485760</td>
                <td>最大上传文件大小（字节，默认10MB）</td>
              </tr>
            </tbody>
          </table>
        </div>

        <h3 class="config-category">异步处理配置</h3>
        <div class="table-container">
          <table>
            <thead>
              <tr>
                <th>配置项</th>
                <th>类型</th>
                <th>默认值</th>
                <th>说明</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td><code>asyncpool</code></td>
                <td>Number</td>
                <td>50</td>
                <td>异步线程池大小</td>
              </tr>
              <tr>
                <td><code>asynctimeout</code></td>
                <td>Number</td>
                <td>30000</td>
                <td>异步超时时间（毫秒）</td>
              </tr>
            </tbody>
          </table>
        </div>

        <CodeBlock
          title="配置示例"
          language="iapp"
          :code="configExample"
        />
      </div>
    </section>

    <section class="section">
      <div class="container">
        <h2 class="section-title">upc() 上传配置</h2>
        <p class="section-subtitle">配置文件上传相关设置</p>
        <div class="table-container">
          <table>
            <thead>
              <tr>
                <th>操作</th>
                <th>参数类型</th>
                <th>说明</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td><code>extensions</code></td>
                <td>List</td>
                <td>设置允许的文件扩展名列表（覆盖原有配置）</td>
              </tr>
              <tr>
                <td><code>maxsize</code></td>
                <td>Number</td>
                <td>设置最大文件大小（字节）</td>
              </tr>
              <tr>
                <td><code>add_extensions</code></td>
                <td>List</td>
                <td>添加允许的文件扩展名（不覆盖原有配置）</td>
              </tr>
              <tr>
                <td><code>reset</code></td>
                <td>无</td>
                <td>重置为默认配置</td>
              </tr>
            </tbody>
          </table>
        </div>

        <CodeBlock
          title="上传配置示例"
          language="iapp"
          :code="upcExample"
        />
      </div>
    </section>

    <section class="section bg-secondary">
      <div class="container">
        <h2 class="section-title">环境变量</h2>
        <p class="section-subtitle">通过 .env 文件管理敏感配置信息</p>
        
        <h3>.env 文件格式</h3>
        <CodeBlock
          language="env"
          :code="envExample"
        />

        <h3 class="mt-4">加载和使用环境变量</h3>
        <CodeBlock
          language="iapp"
          :code="envUsage"
        />
      </div>
    </section>

    <section class="section">
      <div class="container">
        <h2 class="section-title">FAQ 常见问题</h2>
        
        <div class="faq-list">
          <div class="faq-item">
            <h3 class="faq-question">如何修改默认端口？</h3>
            <div class="faq-answer">
              <p>有三种方式：</p>
              <ol>
                <li>命令行参数：<code>java -jar yuweb.jar --port 3000 /path/to/project</code></li>
                <li>app.iapp 配置文件：<code>port(3000)</code> 或 <code>config("port", 3000)</code></li>
                <li>环境变量：<code>port(env("APP_PORT", 8080))</code></li>
              </ol>
            </div>
          </div>

          <div class="faq-item">
            <h3 class="faq-question">生产环境应该如何启动？</h3>
            <div class="faq-answer">
              <p>推荐使用安全模式：</p>
              <CodeBlock
                language="bash"
                code="java -jar yuweb.jar -s --port 80 /path/to/project"
              />
              <p class="mt-2">安全模式特性：仅执行预加载的脚本，防止运行时脚本注入，提高安全性。</p>
            </div>
          </div>

          <div class="faq-item">
            <h3 class="faq-question">连接池配置如何调优？</h3>
            <div class="faq-answer">
              <div class="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>场景</th>
                      <th>poolsize</th>
                      <th>poolinit</th>
                      <th>pooltimeout</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td>开发环境</td>
                      <td>20</td>
                      <td>5</td>
                      <td>30000</td>
                    </tr>
                    <tr>
                      <td>小型应用</td>
                      <td>50</td>
                      <td>10</td>
                      <td>30000</td>
                    </tr>
                    <tr>
                      <td>中型应用</td>
                      <td>100</td>
                      <td>20</td>
                      <td>60000</td>
                    </tr>
                    <tr>
                      <td>大型应用</td>
                      <td>200</td>
                      <td>50</td>
                      <td>60000</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <div class="faq-item">
            <h3 class="faq-question">如何限制上传文件类型？</h3>
            <div class="faq-answer">
              <CodeBlock
                language="iapp"
                :code="uploadTypeExample"
              />
            </div>
          </div>

          <div class="faq-item">
            <h3 class="faq-question">如何提高服务器性能？</h3>
            <div class="faq-answer">
              <ol>
                <li>启用脚本预加载：<code>java -jar yuweb.jar --preload /path/to/project</code></li>
                <li>调整连接池：<code>config("poolsize", 200)</code> <code>config("poolinit", 50)</code></li>
                <li>启用压缩：<code>config("compression", true)</code></li>
                <li>启用 HTTP/2 优化：<code>config("http2", true)</code></li>
                <li>调整异步线程池：<code>config("asyncpool", 100)</code></li>
              </ol>
            </div>
          </div>

          <div class="faq-item">
            <h3 class="faq-question">如何防止 SQL 注入？</h3>
            <div class="faq-answer">
              <p>YuWeb 的数据库函数已内置参数化查询保护：</p>
              <CodeBlock
                language="iapp"
                :code="sqlInjectionExample"
              />
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import CodeBlock from '../components/CodeBlock.vue'

const configExample = `# 基本配置
config("port", 3000)
config("debug", true)

# 配置连接池
config("poolsize", 200)
config("poolinit", 20)
config("pooltimeout", 60000)

# 配置异步处理
config("asyncpool", 100)
config("asynctimeout", 60000)

# 配置 HTTP
config("http2", true)
config("compression", true)
config("compression_min", 2048)

# 配置上传大小限制（50MB）
config("max_upload_size", 50 * 1024 * 1024)

# 禁用静态文件服务
config("static", false)

# 启用安全模式
config("safe", true)`

const upcExample = `# 设置允许的图片类型
upc("extensions", arr(".jpg", ".jpeg", ".png", ".gif", ".webp"))

# 设置最大文件大小为 5MB
upc("maxsize", 5 * 1024 * 1024)

# 后续添加其他类型
upc("add_extensions", arr(".svg", ".ico"))

# 不限制大小
upc("maxsize", -1)

# 重置为默认配置
upc("reset")`

const envExample = `# 数据库配置
DB_TYPE=sqlite
DB_PATH=data/app.db

# MySQL 配置示例
# DB_TYPE=mysql
# DB_HOST=localhost
# DB_PORT=3306
# DB_NAME=mydb
# DB_USER=root
# DB_PASSWORD=secret

# JWT 密钥
JWT_SECRET=your_jwt_secret_key_here

# 邮件配置
SMTP_HOST=smtp.example.com
SMTP_PORT=587
SMTP_USER=user@example.com
SMTP_PASSWORD=your_password

# 应用配置
APP_DEBUG=false
APP_PORT=8080`

const envUsage = `# 加载 .env 文件
loadenv()

# 获取环境变量
s dbHost = env("DB_HOST", "localhost")
s dbPort = env("DB_PORT", "3306")
s jwtSecret = env("JWT_SECRET")

# 使用环境变量配置
s dbType = env("DB_TYPE", "sqlite")
s dbPath = env("DB_PATH", "data/app.db")

f(dbType == "mysql")
{
    s connStr = "mysql://" + env("DB_USER") + ":" + env("DB_PASSWORD") + "@" + dbHost + ":" + dbPort + "/" + env("DB_NAME")
    db("mysql", connStr)
}
else
{
    db("sqlite", dbPath)
}`

const uploadTypeExample = `# 只允许图片
upc("extensions", arr(".jpg", ".jpeg", ".png", ".gif"))

# 只允许文档
upc("extensions", arr(".pdf", ".doc", ".docx", ".xls", ".xlsx"))`

const sqlInjectionExample = `# 安全：使用条件 Map
s user = dbone("users", map("username", username))

# 安全：使用条件 Map
s users = dball("users", map("status", "active"))

# 危险：直接拼接 SQL（不推荐）
s sql = "SELECT * FROM users WHERE username = '" + username + "'"
dbexec(sql)`
</script>

<style scoped>
.page-header {
  padding: 100px 0 60px;
  background-color: var(--bg-secondary);
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
  letter-spacing: -1px;
}

.page-desc {
  font-size: 1.125rem;
  color: var(--text-secondary);
}

.config-category {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 2rem 0 1rem;
}

.faq-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.faq-item {
  padding: 1.5rem;
  background-color: var(--bg-secondary);
  border: 1px solid var(--border);
  border-radius: 12px;
}

.faq-question {
  font-size: 1.0625rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.75rem;
}

.faq-answer {
  color: var(--text-secondary);
  line-height: 1.7;
}

.faq-answer ol {
  margin: 0.5rem 0;
  padding-left: 1.5rem;
}

.faq-answer li {
  margin-bottom: 0.25rem;
}

@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }
}
</style>
