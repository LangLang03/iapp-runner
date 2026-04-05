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
                <td>启用安全模式，仅执行预加载的脚本</td>
              </tr>
              <tr>
                <td><code>--preload</code></td>
                <td><code>-p</code></td>
                <td>启动时预加载所有 .iapp 脚本</td>
              </tr>
              <tr>
                <td><code>--port</code></td>
                <td>-</td>
                <td>指定服务端口（默认: 8080）</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </section>

    <section class="section bg-secondary">
      <div class="container">
        <h2 class="section-title">config() 配置项</h2>
        <p class="section-subtitle">通过 config(key, value) 函数设置服务器参数</p>
        
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
                <td>服务端口</td>
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
        <h2 class="section-title">环境变量</h2>
        <p class="section-subtitle">通过 .env 文件管理敏感配置</p>
        
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

    <section class="section bg-secondary">
      <div class="container">
        <h2 class="section-title">FAQ 常见问题</h2>
        
        <div class="faq-list">
          <div class="faq-item">
            <h3 class="faq-question">如何修改默认端口？</h3>
            <div class="faq-answer">
              <p>有三种方式：</p>
              <ol>
                <li>命令行参数：<code>java -jar yuweb.jar --port 3000 /path/to/project</code></li>
                <li>app.iapp 配置文件：<code>port(3000)</code></li>
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
            </div>
          </div>

          <div class="faq-item">
            <h3 class="faq-question">如何提高服务器性能？</h3>
            <div class="faq-answer">
              <ol>
                <li>启用脚本预加载：<code>java -jar yuweb.jar --preload</code></li>
                <li>调整连接池：<code>config("poolsize", 200)</code></li>
                <li>启用压缩：<code>config("compression", true)</code></li>
              </ol>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import CodeBlock from '../components/CodeBlock.vue'

const configExample = `# 设置端口
config("port", 3000)

# 启用调试模式
config("debug", true)

# 配置连接池
config("poolsize", 200)
config("poolinit", 20)`

const envExample = `# 数据库配置
DB_TYPE=sqlite
DB_PATH=data/app.db

# JWT 密钥
JWT_SECRET=your_jwt_secret_key

# 应用配置
APP_DEBUG=false
APP_PORT=8080`

const envUsage = `# 加载 .env 文件
loadenv()

# 获取环境变量
s dbHost = env("DB_HOST", "localhost")
s dbPort = env("DB_PORT", "3306")`
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
  background-color: var(--bg-primary);
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
