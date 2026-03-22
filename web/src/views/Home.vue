<template>
  <div class="home">
    <section class="hero">
      <div class="container">
        <div class="hero-content">
          <h1 class="hero-title">YuWeb</h1>
          <p class="hero-subtitle">基于裕语言(IApp)的轻量级 Web 后端框架</p>
          <p class="hero-desc">简洁的语法，强大的功能，快速构建 Web 应用</p>
          <div class="hero-actions">
            <router-link to="/quickstart" class="btn btn-primary">快速开始</router-link>
            <router-link to="/api" class="btn btn-outline">查看文档</router-link>
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
            icon="</>"
            title="简洁语法"
            description="采用iAppV3++的简洁语法，降低学习成本，快速上手开发"
          />
          <FeatureCard
            icon="{}"
            title="内置数据库"
            description="支持 SQLite 和 MySQL，提供便捷的 CRUD 操作函数"
          />
          <FeatureCard
            icon="#"
            title="认证系统"
            description="内置用户注册、登录、JWT 认证等常用功能"
          />
          <FeatureCard
            icon="*"
            title="文件上传"
            description="支持文件上传、类型限制、大小限制等功能"
          />
          <FeatureCard
            icon="@"
            title="邮件发送"
            description="内置 SMTP 邮件发送功能，支持 HTML 格式"
          />
          <FeatureCard
            icon=">"
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
            <span class="stack-name">Java</span>
            <span class="stack-desc">运行环境</span>
          </div>
          <div class="stack-item">
            <span class="stack-name">SQLite / MySQL</span>
            <span class="stack-desc">数据库支持</span>
          </div>
          <div class="stack-item">
            <span class="stack-name">JWT</span>
            <span class="stack-desc">身份认证</span>
          </div>
          <div class="stack-item">
            <span class="stack-name">BCrypt</span>
            <span class="stack-desc">密码加密</span>
          </div>
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
  padding: 100px 0 80px;
  text-align: center;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -2px;
  margin-bottom: 0.5rem;
}

.hero-subtitle {
  font-size: 1.5rem;
  color: var(--primary);
  font-weight: 500;
  margin-bottom: 1rem;
}

.hero-desc {
  font-size: 1.125rem;
  color: var(--text-secondary);
  margin-bottom: 2rem;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1px;
  background-color: var(--border);
  border: 1px solid var(--border);
}

.features-grid > * {
  background-color: var(--bg-primary);
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
  text-align: center;
}

.stack-name {
  display: block;
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.25rem;
}

.stack-desc {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

@media (max-width: 1024px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .quick-example .example-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .hero {
    padding: 60px 0 40px;
  }
  
  .hero-title {
    font-size: 2.5rem;
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
    gap: 2rem;
  }
}
</style>
