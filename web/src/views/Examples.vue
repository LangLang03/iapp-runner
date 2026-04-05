<template>
  <div class="examples-page">
    <section class="page-header">
      <div class="container">
        <h1 class="page-title">示例代码</h1>
        <p class="page-desc">完整的示例项目代码</p>
      </div>
    </section>

    <section class="section">
      <div class="container">
        <h2 class="section-title">用户注册 API</h2>
        <p class="section-subtitle">完整的用户注册功能实现</p>
        <CodeBlock
          title="webroot/user/register.iapp"
          language="iapp"
          :code="registerCode"
        />
      </div>
    </section>

    <section class="section bg-secondary">
      <div class="container">
        <h2 class="section-title">用户登录 API</h2>
        <p class="section-subtitle">完整的用户登录功能实现</p>
        <CodeBlock
          title="webroot/user/login.iapp"
          language="iapp"
          :code="loginCode"
        />
      </div>
    </section>

    <section class="section">
      <div class="container">
        <h2 class="section-title">文件上传 API</h2>
        <p class="section-subtitle">支持大小限制和类型验证</p>
        <CodeBlock
          title="webroot/upload.iapp"
          language="iapp"
          :code="uploadCode"
        />
      </div>
    </section>

    <section class="section bg-secondary">
      <div class="container">
        <h2 class="section-title">应用配置文件</h2>
        <p class="section-subtitle">项目初始化配置示例</p>
        <CodeBlock
          title="app.iapp"
          language="iapp"
          :code="appConfigCode"
        />
      </div>
    </section>

    <section class="section">
      <div class="container">
        <h2 class="section-title">完整项目结构</h2>
        <CodeBlock
          language="text"
          :code="projectStructure"
        />
      </div>
    </section>
  </div>
</template>

<script setup>
import CodeBlock from '../components/CodeBlock.vue'

const registerCode = `# webroot/user/register.iapp

f(method() != "POST")
{
    json(map("code", 405, "msg", "仅支持POST请求"))
    endcode
}

s username = post("username")
s password = post("password")
s email = post("email")

f(username == null || password == null)
{
    json(map("code", 400, "msg", "用户名和密码不能为空"))
    endcode
}

s extra = map("email", email)
s result = register("users", username, password, extra)

f(mget(result, "success") == true)
{
    json(map("code", 0, "msg", "注册成功"))
}
else
{
    json(map("code", 400, "msg", mget(result, "msg")))
}`

const loginCode = `# webroot/user/login.iapp

f(method() != "POST")
{
    json(map("code", 405, "msg", "仅支持POST请求"))
    endcode
}

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

const uploadCode = `# webroot/upload.iapp

s avatar = file("avatar")

f(avatar == null)
{
    json(map("code", 400, "msg", "请选择文件"))
    endcode
}

s maxSize = 2 * 1024 * 1024
n size = gfs(avatar)

f(size > maxSize)
{
    json(map("code", 400, "msg", "文件大小不能超过2MB"))
    endcode
}

s result = sf(avatar, "./uploads/avatar", true)

f(mget(result, "success") == true)
{
    json(map("code", 0, "msg", "上传成功", "path", mget(result, "path")))
}`

const appConfigCode = `# app.iapp - 应用配置文件

# 设置端口
port(8080)

# 启用调试模式
config("debug", true)

# 连接数据库
db("sqlite", "data/app.db")

# 配置 CORS
cors(map(
    "origins", arr("http://localhost:3000"),
    "methods", arr("GET", "POST", "PUT", "DELETE"),
    "credentials", true
))

# 创建数据表
dbexec("CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    email TEXT
)")`

const projectStructure = `project/
├── app.iapp                 # 应用配置文件
├── webroot/                 # 网站根目录
│   ├── index.iapp           # 首页
│   ├── user/
│   │   ├── login.iapp       # 用户登录
│   │   └── register.iapp    # 用户注册
│   ├── upload.iapp          # 文件上传
│   └── static/              # 静态文件目录
├── uploads/                 # 上传文件目录
└── data/
    └── app.db               # SQLite 数据库`
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

@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }
}
</style>
