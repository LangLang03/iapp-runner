# YuWeb 配置指南与 FAQ

YuWeb 是一个基于裕语言(IApp)的轻量级 Web 后端框架，本文档详细介绍框架的配置方法和常见问题解答。

***

## 目录

1. [启动参数](#启动参数)
2. [配置文件 app.iapp](#配置文件-appiapp)
3. [config 配置函数](#config-配置函数)
4. [port 端口配置函数](#port-端口配置函数)
5. [upc 上传配置函数](#upc-上传配置函数)
6. [环境变量配置](#环境变量配置)
7. [FAQ 常见问题](#faq-常见问题)

***

## 启动参数

### 基本用法

```bash
java -jar yuweb.jar [选项] [项目路径]
```

### 命令行参数

| 参数 | 简写 | 说明 |
|------|------|------|
| `--debug` | `-d` | 启用调试模式，显示详细错误信息 |
| `--safe` | `-s` | 启用安全模式，仅执行预加载的脚本（自动启用 --preload） |
| `--preload` | `-p` | 启动时预加载所有 .iapp 脚本 |
| `--no-static` | 无 | 禁用静态文件服务 |
| `--port <端口>` | 无 | 指定服务端口（默认: 8080） |
| `--help` | `-h` | 显示帮助信息 |

### 启动示例

```bash
# 基本启动（默认端口 8080）
java -jar yuweb.jar /path/to/project

# 指定端口启动
java -jar yuweb.jar --port 3000 /path/to/project

# 调试模式启动
java -jar yuweb.jar -d /path/to/project

# 安全模式启动（生产环境推荐）
java -jar yuweb.jar -s --port 80 /path/to/project

# 预加载脚本
java -jar yuweb.jar --preload /path/to/project

# 禁用静态文件服务
java -jar yuweb.jar --no-static /path/to/project

# 组合参数
java -jar yuweb.jar -d -p --port 9000 /path/to/project
```

### 参数详解

#### --debug / -d（调试模式）

启用后，当脚本执行出错时会显示详细的错误堆栈信息，便于开发调试。

**开发环境推荐开启，生产环境必须关闭。**

#### --safe / -s（安全模式）

启用后，服务器仅执行启动时预加载的脚本，拒绝执行新的或修改过的脚本。

**生产环境强烈推荐开启。**

特性：
- 自动启用 `--preload`
- 防止恶意脚本注入
- 提高运行时安全性

#### --preload / -p（脚本预加载）

启动时将 `webroot` 目录下所有 `.iapp` 脚本预加载到内存缓存中，提高首次访问速度。

适用场景：
- 生产环境部署
- 脚本数量较多
- 需要快速响应首次请求

#### --no-static（禁用静态文件）

禁用静态文件服务功能，所有请求都将由 `.iapp` 脚本处理。

适用场景：
- 纯 API 服务
- 静态文件由 CDN 或其他服务器提供

***

## 配置文件 app.iapp

在项目根目录创建 `app.iapp` 文件，服务器启动时会自动执行该文件进行初始化配置。

### 文件位置

```
project/
├── app.iapp           # 应用配置文件（服务器启动时自动执行）
├── webroot/           # 网站根目录
│   ├── index.iapp
│   └── ...
└── data/
    └── app.db
```

### 配置示例

```iapp
# 设置服务器端口
port(8080)

# 启用调试模式
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
```

### 执行顺序

1. 服务器启动
2. 解析命令行参数
3. 初始化配置
4. 执行 `app.iapp`（如果存在）
5. 预加载脚本（如果启用）
6. 开始监听端口

***

## config 配置函数

`config(key, value)` 函数用于设置服务器的各项配置参数。

### 函数签名

```iapp
config(key, value)
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| key | String | 是 | 配置项名称（不区分大小写） |
| value | Object | 是 | 配置值 |

**返回值**: `Boolean` - 是否设置成功

### 支持的配置项

#### 基本配置

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `port` | Number | 8080 | 服务端口（1-65535） |
| `debug` | Boolean | false | 调试模式 |
| `safe` | Boolean | false | 安全模式 |
| `preload` | Boolean | false | 脚本预加载 |
| `static` | Boolean | true | 静态文件服务 |

#### 连接池配置

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `poolsize` | Number | 100 | 连接池最大大小 |
| `poolinit` | Number | 10 | 连接池初始大小 |
| `pooltimeout` | Number | 30000 | 连接超时时间（毫秒） |
| `usepool` | Boolean | true | 是否使用连接池 |

#### 异步处理配置

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `asyncpool` | Number | 50 | 异步线程池大小 |
| `asynctimeout` | Number | 30000 | 异步超时时间（毫秒） |

#### HTTP 配置

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `http2` | Boolean | true | 启用 HTTP/2 优化 |
| `compression` | Boolean | true | 启用响应压缩 |
| `compression_min` | Number | 1024 | 最小压缩大小（字节） |

#### 上传配置

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `max_upload_size` | Number | 10485760 | 最大上传文件大小（字节，默认10MB） |

### 使用示例

```iapp
# 设置端口
config("port", 3000)

# 启用调试模式
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
config("safe", true)
```

### 配置项详解

#### port（端口）

设置服务器监听端口。

```iapp
config("port", 8080)
```

- 有效范围：1-65535
- 无效值会使用默认端口 8080
- 建议使用 1024 以上端口（非特权端口）

#### debug（调试模式）

启用调试模式后，错误信息会包含详细堆栈跟踪。

```iapp
config("debug", true)
```

**注意**: 生产环境必须关闭调试模式，避免泄露敏感信息。

#### poolsize（连接池最大大小）

设置数据库连接池的最大连接数。

```iapp
config("poolsize", 200)
```

- 默认值：100
- 建议根据并发量和数据库性能调整
- 过大可能导致数据库连接数超限

#### poolinit（连接池初始大小）

设置数据库连接池的初始连接数。

```iapp
config("poolinit", 20)
```

- 默认值：10
- 建议设置为预期并发量的 50%-80%

#### pooltimeout（连接超时时间）

设置获取数据库连接的超时时间。

```iapp
config("pooltimeout", 60000)
```

- 默认值：30000（30秒）
- 单位：毫秒
- 过短可能导致高并发时获取连接失败

#### asyncpool（异步线程池大小）

设置异步任务处理的线程池大小。

```iapp
config("asyncpool", 100)
```

- 默认值：50
- 建议根据 CPU 核心数和任务类型调整

#### asynctimeout（异步超时时间）

设置异步任务的超时时间。

```iapp
config("asynctimeout", 60000)
```

- 默认值：30000（30秒）
- 单位：毫秒

#### http2（HTTP/2 优化）

启用 HTTP/2 相关优化（如 ETags）。

```iapp
config("http2", true)
```

- 默认值：true
- 启用后可提高静态资源缓存效率

#### compression（响应压缩）

启用响应压缩（GZIP）。

```iapp
config("compression", true)
```

- 默认值：true
- 可减少网络传输数据量

#### compression_min（最小压缩大小）

设置触发压缩的最小响应大小。

```iapp
config("compression_min", 2048)
```

- 默认值：1024（1KB）
- 单位：字节
- 小于此值的响应不进行压缩

#### max_upload_size（最大上传文件大小）

设置上传文件的最大允许大小。

```iapp
# 限制为 20MB
config("max_upload_size", 20 * 1024 * 1024)

# 不限制大小
config("max_upload_size", -1)
```

- 默认值：10485760（10MB）
- 单位：字节
- 设置为负数表示不限制

***

## port 端口配置函数

`port(port)` 函数用于设置服务器监听端口，是 `config("port", value)` 的快捷方式。

### 函数签名

```iapp
port(port)
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| port | Number | 是 | 端口号（1-65535） |

**返回值**: 无

### 使用示例

```iapp
# 设置端口为 3000
port(3000)

# 等同于
config("port", 3000)
```

### 注意事项

- 端口范围：1-65535
- 1024 以下端口需要管理员权限
- 确保端口未被其他程序占用
- 建议在 `app.iapp` 中配置

***

## upc 上传配置函数

`upc(action, value)` 函数用于配置文件上传相关设置。

### 函数签名

```iapp
upc(action, value)
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| action | String | 是 | 操作类型 |
| value | Object | 否 | 配置值（部分操作需要） |

**返回值**: `Boolean` - 是否设置成功

### 支持的操作

| 操作 | 参数类型 | 说明 |
|------|----------|------|
| `extensions` | List | 设置允许的文件扩展名列表（覆盖原有配置） |
| `maxsize` | Number | 设置最大文件大小（字节） |
| `add_extensions` | List | 添加允许的文件扩展名（不覆盖原有配置） |
| `reset` | 无 | 重置为默认配置 |

### 使用示例

#### 设置允许的扩展名

```iapp
# 只允许上传图片
upc("extensions", arr(".jpg", ".jpeg", ".png", ".gif", ".webp"))

# 允许上传文档
upc("extensions", arr(".pdf", ".doc", ".docx", ".xls", ".xlsx"))
```

#### 设置最大文件大小

```iapp
# 限制为 5MB
upc("maxsize", 5 * 1024 * 1024)

# 限制为 100MB
upc("maxsize", 100 * 1024 * 1024)

# 不限制大小
upc("maxsize", -1)
```

#### 添加扩展名

```iapp
# 在现有配置基础上添加扩展名
upc("add_extensions", arr(".zip", ".rar", ".7z"))
```

#### 重置配置

```iapp
# 重置为默认配置
upc("reset")
```

### 默认配置

| 配置项 | 默认值 |
|--------|--------|
| 允许的扩展名 | 无限制（空列表） |
| 最大文件大小 | 10MB |

### 完整示例

```iapp
# 在 app.iapp 中配置上传

# 设置允许的图片类型
upc("extensions", arr(".jpg", ".jpeg", ".png", ".gif", ".webp"))

# 设置最大文件大小为 5MB
upc("maxsize", 5 * 1024 * 1024)

# 后续添加其他类型
upc("add_extensions", arr(".svg", ".ico"))
```

***

## 环境变量配置

### .env 文件

YuWeb 支持通过 `.env` 文件管理环境变量，适合存储敏感配置信息。

#### 文件格式

```env
# 数据库配置
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
APP_PORT=8080
```

#### 语法规则

- 每行一个变量
- 格式：`KEY=VALUE`
- 以 `#` 开头的行为注释
- 值可以用引号包裹（可选）
- 空行会被忽略

#### 加载 .env 文件

```iapp
# 在 app.iapp 中加载

# 自动查找 .env 文件（常见位置）
loadenv()

# 指定文件路径
loadenv("./config/.env")
loadenv("/etc/myapp/.env")
```

#### 使用环境变量

```iapp
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
}
```

### 系统环境变量

YuWeb 也会自动读取系统环境变量。

```bash
# Linux/macOS
export DB_HOST=localhost
export DB_PORT=3306
java -jar yuweb.jar /path/to/project

# Windows PowerShell
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
java -jar yuweb.jar /path/to/project

# Windows CMD
set DB_HOST=localhost
set DB_PORT=3306
java -jar yuweb.jar /path/to/project
```

### 环境变量函数

#### env(key, default?)

获取环境变量值。

```iapp
s value = env("KEY")
s value = env("KEY", "default_value")
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| key | String | 是 | 环境变量名 |
| default | String | 否 | 默认值（变量不存在时返回） |

**返回值**: `String` - 环境变量值或默认值

#### loadenv(path?)

加载 .env 文件。

```iapp
loadenv()
loadenv("./config/.env")
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| path | String | 否 | .env 文件路径，默认自动查找 |

**返回值**: `Boolean` - 是否加载成功

**自动查找路径**:
- `.env`
- `./.env`
- `../.env`
- `env`
- `./env`

***

## FAQ 常见问题

### 启动相关

#### Q: 如何修改默认端口？

**A**: 有三种方式：

1. 命令行参数：
```bash
java -jar yuweb.jar --port 3000 /path/to/project
```

2. app.iapp 配置文件：
```iapp
port(3000)
# 或
config("port", 3000)
```

3. 环境变量：
```bash
export APP_PORT=3000
```
```iapp
port(env("APP_PORT", 8080))
```

#### Q: 启动时提示端口被占用怎么办？

**A**: 
1. 检查端口占用：
```bash
# Linux/macOS
lsof -i :8080

# Windows
netstat -ano | findstr :8080
```

2. 更换端口：
```bash
java -jar yuweb.jar --port 8081 /path/to/project
```

3. 终止占用进程后重新启动

#### Q: 生产环境应该如何启动？

**A**: 推荐使用安全模式：
```bash
java -jar yuweb.jar -s --port 80 /path/to/project
```

安全模式特性：
- 仅执行预加载的脚本
- 防止运行时脚本注入
- 提高安全性

### 配置相关

#### Q: config() 函数的配置项名称区分大小写吗？

**A**: 不区分。以下写法等效：
```iapp
config("PORT", 8080)
config("port", 8080)
config("Port", 8080)
```

#### Q: 如何配置多个数据库连接？

**A**: YuWeb 当前版本支持单一数据库连接。如需操作多个数据库：
1. 使用不同表前缀区分
2. 通过 `dbexec()` 执行跨库 SQL（MySQL）
3. 使用多个 YuWeb 实例

#### Q: 连接池配置如何调优？

**A**: 建议配置：

| 场景 | poolsize | poolinit | pooltimeout |
|------|----------|----------|-------------|
| 开发环境 | 20 | 5 | 30000 |
| 小型应用 | 50 | 10 | 30000 |
| 中型应用 | 100 | 20 | 60000 |
| 大型应用 | 200 | 50 | 60000 |

调优原则：
- `poolinit` 约为 `poolsize` 的 10%-25%
- `pooltimeout` 根据数据库响应时间调整
- 监控连接池使用率进行调整

#### Q: 如何禁用静态文件服务？

**A**: 两种方式：

1. 命令行：
```bash
java -jar yuweb.jar --no-static /path/to/project
```

2. app.iapp：
```iapp
config("static", false)
```

### 文件上传相关

#### Q: 如何限制上传文件类型？

**A**: 使用 `upc()` 函数：
```iapp
# 只允许图片
upc("extensions", arr(".jpg", ".jpeg", ".png", ".gif"))

# 只允许文档
upc("extensions", arr(".pdf", ".doc", ".docx", ".xls", ".xlsx"))
```

#### Q: 上传文件大小限制是多少？如何修改？

**A**: 默认限制为 10MB。修改方式：

1. 使用 config()：
```iapp
# 设置为 50MB
config("max_upload_size", 50 * 1024 * 1024)
```

2. 使用 upc()：
```iapp
# 设置为 50MB
upc("maxsize", 50 * 1024 * 1024)

# 不限制大小
upc("maxsize", -1)
```

#### Q: 上传文件保存在哪里？

**A**: 使用 `sf()` 函数指定保存路径：
```iapp
s file = file("avatar")
s result = sf(file, "./uploads/avatar", true)
```

参数说明：
- `file`: 上传的文件对象
- `"./uploads/avatar"`: 保存目录（相对于项目根目录）
- `true`: 自动生成随机文件名

### 环境变量相关

#### Q: .env 文件应该放在哪里？

**A**: 推荐位置：
1. 项目根目录：`./env` 或 `./.env`
2. config 目录：`./config/.env`
3. 系统配置目录：`/etc/myapp/.env`

加载方式：
```iapp
# 自动查找
loadenv()

# 指定路径
loadenv("./config/.env")
```

#### Q: .env 文件中的敏感信息会被泄露吗？

**A**: 安全建议：
1. 不要将 `.env` 文件提交到版本控制
2. 在 `.gitignore` 中添加 `.env`
3. 生产环境使用系统环境变量或加密存储
4. 设置适当的文件权限

```bash
# 设置文件权限（仅所有者可读写）
chmod 600 .env
```

### 调试相关

#### Q: 如何查看详细错误信息？

**A**: 启用调试模式：

1. 命令行：
```bash
java -jar yuweb.jar -d /path/to/project
```

2. app.iapp：
```iapp
config("debug", true)
```

#### Q: 如何查看服务器运行状态？

**A**: 使用 `info()` 函数：
```iapp
info()
```

显示信息包括：
- 服务器信息
- Java 环境
- 内存使用
- 线程信息
- 性能监控
- 脚本缓存
- 已注册函数

### 性能相关

#### Q: 如何提高服务器性能？

**A**: 优化建议：

1. 启用脚本预加载：
```bash
java -jar yuweb.jar --preload /path/to/project
```

2. 调整连接池：
```iapp
config("poolsize", 200)
config("poolinit", 50)
```

3. 启用压缩：
```iapp
config("compression", true)
config("compression_min", 1024)
```

4. 启用 HTTP/2 优化：
```iapp
config("http2", true)
```

5. 调整异步线程池：
```iapp
config("asyncpool", 100)
```

#### Q: 脚本执行慢怎么办？

**A**: 排查步骤：
1. 启用预加载模式
2. 检查数据库查询是否需要优化
3. 使用 `info()` 查看缓存命中率
4. 检查是否有阻塞操作

### 安全相关

#### Q: 生产环境如何保证安全？

**A**: 安全配置清单：

1. 关闭调试模式：
```iapp
config("debug", false)
```

2. 启用安全模式：
```bash
java -jar yuweb.jar -s /path/to/project
```

3. 配置 CORS：
```iapp
cors(map(
    "origins", arr("https://yourdomain.com"),
    "methods", arr("GET", "POST"),
    "credentials", true
))
```

4. 限制上传文件：
```iapp
upc("extensions", arr(".jpg", ".png"))
upc("maxsize", 5 * 1024 * 1024)
```

5. 使用环境变量存储敏感信息：
```iapp
loadenv()
s jwtSecret = env("JWT_SECRET")
```

#### Q: 如何防止 SQL 注入？

**A**: YuWeb 的数据库函数已内置参数化查询保护：

```iapp
# 安全：使用条件 Map
s user = dbone("users", map("username", username))

# 安全：使用条件 Map
s users = dball("users", map("status", "active"))

# 危险：直接拼接 SQL（不推荐）
s sql = "SELECT * FROM users WHERE username = '" + username + "'"
dbexec(sql)
```

建议：
- 优先使用 `dbone()`、`dball()` 等函数
- 避免使用 `dbexec()` 执行用户输入的 SQL
- 必要时使用参数化查询

***

## 配置速查表

### 命令行参数

| 参数 | 说明 |
|------|------|
| `-d, --debug` | 调试模式 |
| `-s, --safe` | 安全模式 |
| `-p, --preload` | 脚本预加载 |
| `--no-static` | 禁用静态文件 |
| `--port <端口>` | 指定端口 |
| `-h, --help` | 帮助信息 |

### config() 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `port` | Number | 8080 | 服务端口 |
| `debug` | Boolean | false | 调试模式 |
| `safe` | Boolean | false | 安全模式 |
| `preload` | Boolean | false | 脚本预加载 |
| `static` | Boolean | true | 静态文件服务 |
| `poolsize` | Number | 100 | 连接池最大大小 |
| `poolinit` | Number | 10 | 连接池初始大小 |
| `pooltimeout` | Number | 30000 | 连接超时(ms) |
| `usepool` | Boolean | true | 使用连接池 |
| `asyncpool` | Number | 50 | 异步线程池大小 |
| `asynctimeout` | Number | 30000 | 异步超时(ms) |
| `http2` | Boolean | true | HTTP/2 优化 |
| `compression` | Boolean | true | 响应压缩 |
| `compression_min` | Number | 1024 | 最小压缩大小 |
| `max_upload_size` | Number | 10485760 | 最大上传大小 |

### upc() 操作

| 操作 | 说明 |
|------|------|
| `extensions` | 设置允许扩展名 |
| `maxsize` | 设置最大文件大小 |
| `add_extensions` | 添加允许扩展名 |
| `reset` | 重置配置 |

***

*YuWeb v1.0.0 - 配置指南*
