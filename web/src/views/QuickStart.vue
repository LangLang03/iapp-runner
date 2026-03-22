<template>
  <div class="quickstart">
    <section class="page-header">
      <div class="container">
        <h1 class="page-title">快速开始</h1>
        <p class="page-desc">几分钟内启动你的第一个 YuWeb 项目</p>
      </div>
    </section>

    <section class="section bg-secondary">
      <div class="container">
        <h2 class="section-title">获取 YuWeb</h2>
        <div class="steps">
          <div class="step">
            <div class="step-number">1</div>
            <div class="step-content">
              <h3>下载 YuWeb</h3>
              <p>如果需要收费，请登录后免费不限速下载</p>
              <a href="https://www.123865.com/s/gbWqjv-GmYa3" class="btn btn-primary">立即下载</a>
            </div>
          </div>
          <div class="step">
            <div class="step-number">2</div>
            <div class="step-content">
              <h3>加入交流群</h3>
              <p>QQ群：1067354882，获取帮助与交流</p>
              <a href="https://qm.qq.com/q/xxx" target="_blank" class="btn btn-outline">加入群聊</a>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="section">
      <div class="container">
        <h2 class="section-title">项目结构</h2>
        <CodeBlock
          language="text"
          :code="projectStructure"
        />
      </div>
    </section>

    <section class="section bg-secondary">
      <div class="container">
        <h2 class="section-title">启动服务器</h2>
        <div class="steps">
          <div class="step">
            <div class="step-number">1</div>
            <div class="step-content">
              <h3>准备环境</h3>
              <p>确保已安装 Java 运行环境 (JRE 8+)</p>
              <CodeBlock
                language="bash"
                code="java -version"
              />
            </div>
          </div>
          
          <div class="step">
            <div class="step-number">2</div>
            <div class="step-content">
              <h3>创建项目目录</h3>
              <CodeBlock
                language="bash"
                :code="createProject"
              />
            </div>
          </div>
          
          <div class="step">
            <div class="step-number">3</div>
            <div class="step-content">
              <h3>启动服务器</h3>
              <CodeBlock
                language="bash"
                code="java -jar yuweb.jar /path/to/project"
              />
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="section">
      <div class="container">
        <h2 class="section-title">Hello World</h2>
        <p class="section-subtitle">创建你的第一个 API</p>
        <p>在 <code>webroot/index.iapp</code> 文件中写入：</p>
        <CodeBlock
          title="webroot/index.iapp"
          language="iapp"
          :code="helloWorld"
        />
        <p class="mt-3">访问 <code>http://localhost:8080/</code> 即可看到结果：</p>
        <CodeBlock
          language="json"
          :code='{"code": 0, "msg": "Hello World"}'
        />
      </div>
    </section>

    <section class="section bg-secondary">
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
                <td><code>--no-static</code></td>
                <td>-</td>
                <td>禁用静态文件服务</td>
              </tr>
              <tr>
                <td><code>--port</code></td>
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

    <section class="section">
      <div class="container">
        <h2 class="section-title">启动示例</h2>
        <div class="examples-grid">
          <div class="example-item">
            <h4>基本启动</h4>
            <CodeBlock
              language="bash"
              code="java -jar yuweb.jar /path/to/project"
            />
          </div>
          <div class="example-item">
            <h4>指定端口</h4>
            <CodeBlock
              language="bash"
              code="java -jar yuweb.jar --port 3000 /path/to/project"
            />
          </div>
          <div class="example-item">
            <h4>调试模式</h4>
            <CodeBlock
              language="bash"
              code="java -jar yuweb.jar -d /path/to/project"
            />
          </div>
          <div class="example-item">
            <h4>生产环境</h4>
            <CodeBlock
              language="bash"
              code="java -jar yuweb.jar -s --port 80 /path/to/project"
            />
          </div>
        </div>
      </div>
    </section>

    <section class="section bg-secondary">
      <div class="container">
        <h2 class="section-title">下一步</h2>
        <div class="next-steps">
          <router-link to="/api" class="next-step-card">
            <h3>API 文档</h3>
            <p>了解所有可用的函数和用法</p>
          </router-link>
          <router-link to="/config" class="next-step-card">
            <h3>配置指南</h3>
            <p>学习如何配置服务器和数据库</p>
          </router-link>
          <router-link to="/examples" class="next-step-card">
            <h3>示例代码</h3>
            <p>查看完整的示例项目</p>
          </router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import CodeBlock from '../components/CodeBlock.vue'

const projectStructure = `project/
├── app.iapp           # 应用配置文件（可选）
├── webroot/           # 网站根目录
│   ├── index.iapp     # 首页
│   ├── user/
│   │   ├── login.iapp
│   │   └── register.iapp
│   └── static/        # 静态文件目录
└── data/
    └── app.db         # SQLite 数据库`

const createProject = `mkdir myproject
cd myproject
mkdir webroot data
echo 'json(map("code", 0, "msg", "Hello World"))' > webroot/index.iapp`

const helloWorld = `json(map("code", 0, "msg", "Hello World"))`
</script>

<style scoped>
.page-header {
  padding: 60px 0 40px;
  background-color: var(--bg-secondary);
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

.page-desc {
  font-size: 1.125rem;
  color: var(--text-secondary);
}

.steps {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.step {
  display: flex;
  gap: 1.5rem;
}

.step-number {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--primary);
  color: white;
  font-weight: 600;
  font-size: 1.125rem;
  border-radius: 50%;
}

.step-content h3 {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

.step-content p {
  color: var(--text-secondary);
  margin-bottom: 0.75rem;
}

.examples-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.5rem;
}

.example-item h4 {
  font-size: 0.9375rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.75rem;
}

.next-steps {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
}

.next-step-card {
  display: block;
  padding: 1.5rem;
  background-color: var(--bg-primary);
  border: 1px solid var(--border);
  transition: border-color 0.2s ease;
}

.next-step-card:hover {
  border-color: var(--primary);
}

.next-step-card h3 {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

.next-step-card p {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }
  
  .step {
    flex-direction: column;
    gap: 1rem;
  }
  
  .examples-grid,
  .next-steps {
    grid-template-columns: 1fr;
  }
}
</style>
