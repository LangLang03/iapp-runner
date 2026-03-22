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
        <p class="section-subtitle">支持大小限制和类型验证的文件上传</p>
        <CodeBlock
          title="webroot/upload.iapp"
          language="iapp"
          :code="uploadCode"
        />
      </div>
    </section>

    <section class="section bg-secondary">
      <div class="container">
        <h2 class="section-title">帖子列表 API</h2>
        <p class="section-subtitle">支持分页和搜索的帖子列表</p>
        <CodeBlock
          title="webroot/post/list.iapp"
          language="iapp"
          :code="postListCode"
        />
      </div>
    </section>

    <section class="section">
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

    <section class="section bg-secondary">
      <div class="container">
        <h2 class="section-title">需要登录验证的页面</h2>
        <p class="section-subtitle">使用 Token 验证用户身份</p>
        <CodeBlock
          title="webroot/user/profile.iapp"
          language="iapp"
          :code="profileCode"
        />
      </div>
    </section>

    <section class="section">
      <div class="container">
        <h2 class="section-title">CORS 跨域配置</h2>
        <p class="section-subtitle">配置跨域访问规则</p>
        <CodeBlock
          title="app.iapp"
          language="iapp"
          :code="corsCode"
        />
      </div>
    </section>

    <section class="section bg-secondary">
      <div class="container">
        <h2 class="section-title">邮件发送示例</h2>
        <p class="section-subtitle">发送 HTML 格式邮件</p>
        <CodeBlock
          title="webroot/mail/send.iapp"
          language="iapp"
          :code="mailCode"
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
    s data = json()
    f(data != null)
    {
        username = mget(data, "username")
        password = mget(data, "password")
        email = mget(data, "email")
    }
}

f(username == null || password == null)
{
    json(map("code", 400, "msg", "用户名和密码不能为空"))
    endcode
}

f(length(username) < 3)
{
    json(map("code", 400, "msg", "用户名至少3个字符"))
    endcode
}

f(length(password) < 6)
{
    json(map("code", 400, "msg", "密码至少6个字符"))
    endcode
}

s extra = map("email", email)
s result = register("users", username, password, extra)

f(mget(result, "success") == true)
{
    json(map("code", 0, "msg", "注册成功", "userId", mget(result, "userId")))
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

f(username == null || password == null)
{
    s data = json()
    f(data != null)
    {
        username = mget(data, "username")
        password = mget(data, "password")
    }
}

f(username == null || password == null)
{
    json(map("code", 400, "msg", "用户名和密码不能为空"))
    endcode
}

s result = login("users", username, password)

f(mget(result, "success") == true)
{
    setcookie("token", mget(result, "token"), 86400)
    json(map("code", 0, "msg", "登录成功", "data", result))
}
else
{
    json(map("code", 401, "msg", mget(result, "msg")))
}`

const uploadCode = `# webroot/upload.iapp

f(method() != "POST")
{
    json(map("code", 405, "msg", "仅支持POST请求"))
    endcode
}

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

s ext = gfe(avatar)
s allowedExts = arr(".jpg", ".jpeg", ".png", ".gif", ".webp")

s isAllowed = false
n i = 0
w(i < length(allowedExts))
{
    f(ext == mget(allowedExts, i))
    {
        isAllowed = true
    }
    i = i + 1
}

f(isAllowed == false)
{
    json(map("code", 400, "msg", "只允许上传图片文件"))
    endcode
}

s result = sf(avatar, "./uploads/avatar", true)

f(mget(result, "success") == true)
{
    json(map("code", 0, "msg", "上传成功", "path", mget(result, "path")))
}
else
{
    json(map("code", 500, "msg", mget(result, "msg")))
}`

const postListCode = `# webroot/post/list.iapp

s page = get("page", "1")
s size = get("size", "10")
s keyword = get("keyword")

s result = ""

f(keyword != null && length(keyword) > 0)
{
    result = dbsearch("posts", "title,content", keyword, page, size)
}
else
{
    result = dbpage("posts", map("status", "published"), page, size)
}

json(map(
    "code", 0,
    "msg", "获取成功",
    "data", mget(result, "data"),
    "total", mget(result, "total"),
    "page", mget(result, "page"),
    "totalPages", mget(result, "totalPages")
))`

const appConfigCode = `# app.iapp - 应用配置文件

# 设置端口
port(8080)

# 启用调试模式（生产环境请关闭）
config("debug", true)

# 连接数据库
db("sqlite", "data/app.db")

# 配置 CORS 跨域
cors(map(
    "origins", arr("http://localhost:3000"),
    "methods", arr("GET", "POST", "PUT", "DELETE"),
    "credentials", true
))

# 配置文件上传
upc("extensions", arr(".jpg", ".png", ".gif", ".pdf"))
upc("maxsize", 10 * 1024 * 1024)

# 配置连接池
config("poolsize", 100)
config("poolinit", 10)

# 创建数据表
dbexec("CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    email TEXT,
    created_at INTEGER
)")

dbexec("CREATE TABLE IF NOT EXISTS posts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    content TEXT,
    user_id INTEGER,
    status TEXT DEFAULT 'draft',
    created_at INTEGER
)")`

const profileCode = `# webroot/user/profile.iapp

# 获取 Token
s token = getcookie("token")

f(token == null)
{
    json(map("code", 401, "msg", "未登录"))
    endcode
}

# 验证 Token
s v = verify(token)

f(mget(v, "valid") != true)
{
    delcookie("token")
    json(map("code", 401, "msg", "登录已过期，请重新登录"))
    endcode
}

# 获取用户信息
s userId = mget(v, "userId")
s user = dbone("users", map("id", userId))

f(user == null)
{
    json(map("code", 404, "msg", "用户不存在"))
    endcode
}

# 返回用户信息（不包含密码）
json(map(
    "code", 0,
    "data", map(
        "id", mget(user, "id"),
        "username", mget(user, "username"),
        "email", mget(user, "email"),
        "created_at", mget(user, "created_at")
    )
))`

const corsCode = `# 在 app.iapp 中配置 CORS

# 允许单个源
cors(map(
    "origins", arr("http://localhost:3000"),
    "methods", arr("GET", "POST"),
    "credentials", true
))

# 允许多个源
cors(map(
    "origins", arr("http://localhost:3000", "https://example.com"),
    "methods", arr("GET", "POST", "PUT", "DELETE"),
    "headers", arr("Content-Type", "Authorization"),
    "exposedHeaders", arr("X-Custom-Header"),
    "credentials", true,
    "maxAge", 3600
))

# 允许所有源（不推荐生产环境使用）
cors(map(
    "origins", arr("*"),
    "methods", arr("GET", "POST", "PUT", "DELETE"),
    "headers", arr("Content-Type", "Authorization")
))`

const mailCode = `# webroot/mail/send.iapp

# 配置邮件服务器（通常在 app.iapp 中配置）
mailconfig("smtp.example.com", 587, "user@example.com", "password", true)

f(method() != "POST")
{
    json(map("code", 405, "msg", "仅支持POST请求"))
    endcode
}

s to = post("to")
s subject = post("subject")
s content = post("content")

f(to == null || subject == null || content == null)
{
    json(map("code", 400, "msg", "参数不完整"))
    endcode
}

# 发送 HTML 格式邮件
s htmlBody = "<h1>" + subject + "</h1><p>" + content + "</p>"
s result = sendmail(to, subject, htmlBody, true)

f(result == true)
{
    json(map("code", 0, "msg", "邮件发送成功"))
}
else
{
    json(map("code", 500, "msg", "邮件发送失败"))
}`

const projectStructure = `project/
├── app.iapp                 # 应用配置文件
├── webroot/                 # 网站根目录
│   ├── index.iapp           # 首页
│   ├── user/
│   │   ├── login.iapp       # 用户登录
│   │   ├── register.iapp    # 用户注册
│   │   └── profile.iapp     # 用户信息（需登录）
│   ├── post/
│   │   ├── list.iapp        # 帖子列表
│   │   ├── detail.iapp      # 帖子详情
│   │   └── create.iapp      # 创建帖子
│   ├── upload.iapp          # 文件上传
│   ├── mail/
│   │   └── send.iapp        # 发送邮件
│   └── static/              # 静态文件目录
│       ├── css/
│       ├── js/
│       └── images/
├── uploads/                 # 上传文件目录
│   └── avatar/
├── data/
│   └── app.db               # SQLite 数据库
└── .env                     # 环境变量配置`
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

@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }
}
</style>
