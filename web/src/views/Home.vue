<template>
  <div class="home">
    <section class="hero">
      <div class="container">
        <div class="hero-content">
          <div class="hero-badge">
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"></polygon>
            </svg>
            轻量级 Web 框架
          </div>
          <h1 class="hero-title">
            <span class="hero-title-main">YuWeb</span>
          </h1>
          <p class="hero-subtitle">基于裕语言(IApp)的轻量级 Web 后端框架</p>
          <p class="hero-desc">简洁的语法，强大的功能，快速构建 Web 应用</p>
          <div class="hero-actions">
            <router-link to="/quickstart" class="btn btn-primary">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <polygon points="10 8 16 12 10 16 10 8"></polygon>
              </svg>
              快速开始
            </router-link>
            <router-link to="/api" class="btn btn-outline">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14 2 14 8 20 8"></polyline>
                <line x1="16" y1="13" x2="8" y2="13"></line>
                <line x1="16" y1="17" x2="8" y2="17"></line>
                <polyline points="10 9 9 9 8 9"></polyline>
              </svg>
              查看文档
            </router-link>
          </div>
        </div>
        <div class="hero-demo">
          <div class="demo-window">
            <div class="demo-header">
              <div class="demo-dots">
                <span class="dot red"></span>
                <span class="dot yellow"></span>
                <span class="dot green"></span>
              </div>
              <div class="demo-title">webroot/index.iapp</div>
            </div>
            <div class="demo-content">
              <pre><code class="hljs"><span class="hljs-comment"># Hello World 示例</span>
<span class="hljs-built_in">json</span>(<span class="hljs-built_in">map</span>(<span class="hljs-string">"code"</span>, <span class="hljs-number">0</span>, <span class="hljs-string">"msg"</span>, <span class="hljs-string">"Hello World"</span>))</code></pre>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="section features">
      <div class="container">
        <h2 class="section-title text-center">核心特性</h2>
        <p class="section-subtitle text-center">简单、高效、安全的 Web 开发体验</p>
        <div class="features-grid">
          <FeatureCard
            icon="code"
            title="简洁语法"
            description="采用iAppV3++的简洁语法，降低学习成本，快速上手开发"
          />
          <FeatureCard
            icon="database"
            title="内置数据库"
            description="支持 SQLite 和 MySQL，提供便捷的 CRUD 操作函数"
          />
          <FeatureCard
            icon="shield"
            title="认证系统"
            description="内置用户注册、登录、JWT 认证等常用功能"
          />
          <FeatureCard
            icon="upload"
            title="文件上传"
            description="支持文件上传、类型限制、大小限制等功能"
          />
          <FeatureCard
            icon="mail"
            title="邮件发送"
            description="内置 SMTP 邮件发送功能，支持 HTML 格式"
          />
          <FeatureCard
            icon="zap"
            title="异步处理"
            description="支持异步任务执行，提高并发处理能力"
          />
        </div>
      </div>
    </section>

    <section class="section bg-secondary quick-example">
      <div class="container">
        <h2 class="section-title text-center">快速示例</h2>
        <p class="section-subtitle text-center">只需几行代码即可创建 API</p>
        <div class="example-content">
          <CodeBlock
            title="Hello World"
            language="iapp"
            :code="helloWorldCode"
          />
          <CodeBlock
            title="用户登录"
            language="iapp"
            :code="loginCode"
          />
        </div>
      </div>
    </section>

    <section class="section tech-stack">
      <div class="container">
        <h2 class="section-title text-center">技术栈</h2>
        <div class="stack-list">
          <div class="stack-item">
            <div class="stack-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M17 18a5 5 0 0 0-10 0"></path>
                <line x1="12" y1="9" x2="12" y2="2"></line>
                <line x1="4.22" y1="10.22" x2="5.64" y2="11.64"></line>
                <line x1="1" y1="18" x2="3" y2="18"></line>
                <line x1="21" y1="18" x2="23" y2="18"></line>
                <line x1="18.36" y1="11.64" x2="19.78" y2="10.22"></line>
                <line x1="23" y1="22" x2="1" y2="22"></line>
              </svg>
            </div>
            <span class="stack-name">Java</span>
            <span class="stack-desc">运行环境</span>
          </div>
          <div class="stack-item">
            <div class="stack-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <ellipse cx="12" cy="5" rx="9" ry="3"></ellipse>
                <path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3"></path>
                <path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"></path>
              </svg>
            </div>
            <span class="stack-name">SQLite / MySQL</span>
            <span class="stack-desc">数据库支持</span>
          </div>
          <div class="stack-item">
            <div class="stack-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
              </svg>
            </div>
            <span class="stack-name">JWT</span>
            <span class="stack-desc">身份认证</span>
          </div>
          <div class="stack-item">
            <div class="stack-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
              </svg>
            </div>
            <span class="stack-name">BCrypt</span>
            <span class="stack-desc">密码加密</span>
          </div>
        </div>
      </div>
    </section>

    <section class="section cta-section">
      <div class="container">
        <div class="cta-content">
          <h2 class="cta-title">准备好开始了吗?</h2>
          <p class="cta-desc">几分钟内启动你的第一个 YuWeb 项目</p>
          <router-link to="/quickstart" class="btn btn-primary btn-lg">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="5" y1="12" x2="19" y2="12"></line>
              <polyline points="12 5 19 12 12 19"></polyline>
            </svg>
            开始使用
          </router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import FeatureCard from '../components/FeatureCard.vue'
import CodeBlock from '../components/CodeBlock.vue'

const helloWorldCode = `# webroot/index.iapp
json(map("code", 0, "msg", "Hello World"))`

const loginCode = `# webroot/user/login.iapp
s username = post("username")
s password = post("password")

s result = login("users", username, password)

f(mget(result, "success") == true)
{
    setcookie("token", mget(result, "token"), 86400)
    json(map("code", 0, "msg", "登录成功"))
}
else
{
    json(map("code", 401, "msg", mget(result, "msg")))
}`
</script>

<style scoped>
.hero {
  padding: 120px 0 80px;
  position: relative;
  overflow: hidden;
}

.hero::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(ellipse at top, var(--bg-tertiary) 0%, var(--bg-primary) 70%);
  z-index: -1;
}

.hero .container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  align-items: center;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.375rem 0.875rem;
  background-color: var(--bg-tertiary);
  border: 1px solid var(--border);
  border-radius: 100px;
  font-size: 0.8125rem;
  font-weight: 500;
  color: var(--cta);
  margin-bottom: 1.5rem;
}

.hero-title {
  margin-bottom: 1rem;
}

.hero-title-main {
  font-size: 4rem;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -3px;
  background: linear-gradient(135deg, var(--text-primary) 0%, var(--cta) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 1.5rem;
  color: var(--cta);
  font-weight: 500;
  margin-bottom: 0.75rem;
}

.hero-desc {
  font-size: 1.125rem;
  color: var(--text-secondary);
  margin-bottom: 2rem;
}

.hero-actions {
  display: flex;
  gap: 1rem;
}

.hero-demo {
  display: flex;
  justify-content: center;
}

.demo-window {
  width: 100%;
  max-width: 480px;
  background-color: var(--code-bg);
  border: 1px solid var(--border);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.demo-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.75rem 1rem;
  background-color: var(--code-header-bg);
  border-bottom: 1px solid var(--border);
}

.demo-dots {
  display: flex;
  gap: 0.5rem;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.dot.red { background-color: #ff5f56; }
.dot.yellow { background-color: #ffbd2e; }
.dot.green { background-color: #27c93f; }

.demo-title {
  font-size: 0.8125rem;
  color: var(--code-text);
  opacity: 0.7;
}

.demo-content {
  padding: 1.25rem;
}

.demo-content pre {
  margin: 0;
  font-family: var(--font-mono);
  font-size: 0.875rem;
  line-height: 1.7;
}

.demo-content code {
  color: var(--code-text);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
}

.quick-example .example-content {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.5rem;
}

.stack-list {
  display: flex;
  justify-content: center;
  gap: 3rem;
  flex-wrap: wrap;
}

.stack-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.75rem;
  padding: 1.5rem 2rem;
  background-color: var(--bg-secondary);
  border: 1px solid var(--border);
  border-radius: 12px;
  transition: border-color var(--transition-normal) ease;
}

.stack-item:hover {
  border-color: var(--cta);
}

.stack-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background-color: var(--bg-tertiary);
  border-radius: 10px;
  color: var(--cta);
}

.stack-name {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-primary);
}

.stack-desc {
  font-size: 0.8125rem;
  color: var(--text-secondary);
}

.cta-section {
  text-align: center;
}

.cta-content {
  max-width: 600px;
  margin: 0 auto;
}

.cta-title {
  font-size: 2rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 0.75rem;
}

.cta-desc {
  font-size: 1.125rem;
  color: var(--text-secondary);
  margin-bottom: 2rem;
}

.btn-lg {
  padding: 1rem 2rem;
  font-size: 1.0625rem;
}

@media (max-width: 1024px) {
  .hero .container {
    grid-template-columns: 1fr;
    text-align: center;
  }
  
  .hero-actions {
    justify-content: center;
  }
  
  .hero-demo {
    margin-top: 2rem;
  }
  
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .quick-example .example-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .hero {
    padding: 100px 0 60px;
  }
  
  .hero-title-main {
    font-size: 3rem;
  }
  
  .hero-subtitle {
    font-size: 1.25rem;
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .stack-list {
    gap: 1rem;
  }
  
  .stack-item {
    padding: 1rem 1.5rem;
  }
}
</style>
