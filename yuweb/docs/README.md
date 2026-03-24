# YuWeb 框架文档

YuWeb 是一个基于裕语言(IApp)的轻量级 Web 后端框架，提供简洁的语法来构建 Web 应用。

***

## 目录

1. [快速开始](#快速开始)
2. [语法说明](#语法说明)
3. [请求函数](#请求函数)
4. [响应函数](#响应函数)
5. [数据库函数](#数据库函数)
6. [文件上传函数](#文件上传函数)
7. [工具函数](#工具函数)
8. [认证函数](#认证函数)
9. [条件查询函数](#条件查询函数)
10. [加密函数](#加密函数)
11. [JWT函数](#jwt函数)
12. [Session函数](#session函数)
13. [邮件函数](#邮件函数)
14. [环境变量函数](#环境变量函数)
15. [CORS函数](#cors函数)
16. [服务器配置函数](#服务器配置函数)
17. [异步函数](#异步函数)
18. [服务器信息函数](#服务器信息函数)
19. [完整示例](#完整示例)

***

## 快速开始

### 项目结构

```
project/
├── app.iapp           # 应用配置文件（可选）
├── webroot/           # 网站根目录
│   ├── index.iapp     # 首页
│   ├── user/
│   │   ├── login.iapp
│   │   └── register.iapp
│   └── static/        # 静态文件目录
└── data/
    └── app.db         # SQLite 数据库
```

### 启动服务器

```bash
java -jar yuweb.jar /path/to/project
```

### Hello World

创建 `webroot/index.iapp`:

```iapp
json(map("code", 0, "msg", "Hello World"))
```

***

## 语法说明

### 变量声明

```iapp
s name = "张三"           # 字符串变量
s age = 25               # 数值变量
s active = true          # 布尔变量
```

### 条件判断

```iapp
f(age >= 18)
{
    json(map("msg", "已成年"))
}
else
{
    json(map("msg", "未成年"))
}
```

### 循环

```iapp
w(i < 10)
{
    syso(i)
    i = i + 1
}
```

### 函数调用

```iapp
s name = get("name")
s result = dbone("users", map("id", 1))
```

### 结束执行

```iapp
endcode
```

***

## 请求函数

### method()

获取请求方法。

| 参数 | 类型 | 说明 |
| -- | -- | -- |
| 无  | -  | -  |

**返回值**: `String` - GET/POST/PUT/DELETE 等

**示例**:

```iapp
f(method() != "POST")
{
    json(map("code", 405, "msg", "仅支持POST请求"))
    endcode
}
```

***

### get(name, default?)

获取 URL 查询参数。

| 参数      | 类型     | 必填 | 说明  |
| ------- | ------ | -- | --- |
| name    | String | 是  | 参数名 |
| default | String | 否  | 默认值 |

**返回值**: `String` - 参数值或默认值

**示例**:

```iapp
s name = get("name")
s page = get("page", "1")
```

***

### gets()

获取所有 URL 查询参数。

| 参数 | 类型 | 说明 |
| -- | -- | -- |
| 无  | -  | -  |

**返回值**: `Map<String, String>` - 所有参数的键值对

**示例**:

```iapp
s params = gets()
s name = mget(params, "name")
```

***

### post(name, default?)

获取 POST 表单参数或 JSON 字段。

| 参数      | 类型     | 必填 | 说明  |
| ------- | ------ | -- | --- |
| name    | String | 是  | 参数名 |
| default | String | 否  | 默认值 |

**返回值**: `String` - 参数值

**示例**:

```iapp
s username = post("username")
s password = post("password")
```

***

### posts()

获取所有 POST 表单参数。

| 参数 | 类型 | 说明 |
| -- | -- | -- |
| 无  | -  | -  |

**返回值**: `Map<String, String>` - 所有参数的键值对

***

### body()

获取原始请求体。

| 参数 | 类型 | 说明 |
| -- | -- | -- |
| 无  | -  | -  |

**返回值**: `String` - 原始请求体内容

**示例**:

```iapp
s rawBody = body()
```

***

### json()

获取 JSON 请求体或返回 JSON 响应。

| 参数   | 类型     | 必填 | 说明     |
| ---- | ------ | -- | ------ |
| data | Object | 否  | 要返回的数据 |

**返回值**:

- 无参数时返回 `Map<String, Object>` - 解析后的 JSON 对象
- 有参数时无返回值，直接输出 JSON 响应

**示例**:

```iapp
# 获取 JSON 请求体
s data = json()
s username = mget(data, "username")

# 返回 JSON 响应
json(map("code", 0, "msg", "成功"))
```

***

### path()

获取请求路径。

| 参数 | 类型 | 说明 |
| -- | -- | -- |
| 无  | -  | -  |

**返回值**: `String` - 如 `/user/login`

**示例**:

```iapp
s p = path()
```

***

### url()

获取完整请求 URL。

| 参数 | 类型 | 说明 |
| -- | -- | -- |
| 无  | -  | -  |

**返回值**: `String` - 完整 URL

**示例**:

```iapp
s u = url()
```

***

### header(name)

获取请求头。

| 参数   | 类型     | 必填 | 说明    |
| ---- | ------ | -- | ----- |
| name | String | 是  | 请求头名称 |

**返回值**: `String` - 请求头值

**示例**:

```iapp
s auth = header("Authorization")
```

***

### clientip()

获取客户端 IP 地址。

| 参数 | 类型 | 说明 |
| -- | -- | -- |
| 无  | -  | -  |

**返回值**: `String` - 客户端 IP

**示例**:

```iapp
s ip = clientip()
```

***

### useragent()

获取 User-Agent。

| 参数 | 类型 | 说明 |
|------|------|------|
| 无 | - | - |

**返回值**: `String` - User-Agent 字符串

***

### isajax()

判断是否为 AJAX 请求。

| 参数 | 类型 | 说明 |
|------|------|------|
| 无 | - | - |

**返回值**: `Boolean` - 是否 AJAX 请求

**示例**:

```iapp
f(isajax())
{
    json(map("code", 0))
}
```

***

### isjson()

判断请求是否为 JSON 格式。

| 参数 | 类型 | 说明 |
|------|------|------|
| 无 | - | - |

**返回值**: `Boolean` - 是否 JSON 请求

***

### getcookie(name)

获取 Cookie 值。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | Cookie 名称 |

**返回值**: `String` - Cookie 值

**示例**:

```iapp
s token = getcookie("token")
```

***

### setcookie(name, value, maxAge)

设置 Cookie。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | Cookie 名称 |
| value | String | 是 | Cookie 值 |
| maxAge | Number | 是 | 有效期（秒） |

**返回值**: 无

**示例**:

```iapp
setcookie("token", "abc123", 86400)
```

***

### delcookie(name)

删除 Cookie。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | Cookie 名称 |

**返回值**: 无

**示例**:

```iapp
delcookie("token")
```

***

## 响应函数

### json(data)

返回 JSON 响应。

| 参数   | 类型     | 必填 | 说明     |
| ---- | ------ | -- | ------ |
| data | Object | 是  | 要返回的数据 |

**返回值**: 无

**示例**:

```iapp
json(map("code", 0, "msg", "成功", "data", result))
```

***

### text(content)

返回纯文本响应。

| 参数      | 类型     | 必填 | 说明   |
| ------- | ------ | -- | ---- |
| content | String | 是  | 文本内容 |

**返回值**: 无

**示例**:

```iapp
text("Hello World")
```

***

### html(content)

返回 HTML 响应。

| 参数      | 类型     | 必填 | 说明      |
| ------- | ------ | -- | ------- |
| content | String | 是  | HTML 内容 |

**返回值**: 无

**示例**:

```iapp
html("<h1>标题</h1><p>内容</p>")
```

***

### status(code)

设置 HTTP 状态码。

| 参数   | 类型     | 必填 | 说明       |
| ---- | ------ | -- | -------- |
| code | Number | 是  | HTTP 状态码 |

**返回值**: 无

**示例**:

```iapp
status(404)
json(map("msg", "资源不存在"))
```

***

### error(code, message)

返回错误响应。

| 参数      | 类型     | 必填 | 说明       |
| ------- | ------ | -- | -------- |
| code    | Number | 是  | HTTP 状态码 |
| message | String | 是  | 错误信息     |

**返回值**: 无

**示例**:

```iapp
error(500, "服务器内部错误")
```

***

### redirect(location)

重定向。

| 参数       | 类型     | 必填 | 说明     |
| -------- | ------ | -- | ------ |
| location | String | 是  | 目标 URL |

**返回值**: 无

**示例**:

```iapp
redirect("/login")
```

***

### setHeader(name, value)

设置响应头。

| 参数    | 类型     | 必填 | 说明    |
| ----- | ------ | -- | ----- |
| name  | String | 是  | 响应头名称 |
| value | String | 是  | 响应头值  |

**返回值**: 无

**示例**:

```iapp
setHeader("Content-Type", "application/json")
```

***

## 数据库函数

### db(type, path)

连接数据库。

| 参数   | 类型     | 必填 | 说明                        |
| ---- | ------ | -- | ------------------------- |
| type | String | 是  | 数据库类型: `sqlite` 或 `mysql` |
| path | String | 是  | SQLite 文件路径 或 MySQL 连接字符串 |

**返回值**: `Boolean` - 是否连接成功

**示例**:

```iapp
# SQLite
db("sqlite", "data/app.db")

# MySQL
db("mysql", "mysql://root:password@localhost:3306/mydb")
```

***

### dbone(table, condition)

查询单条记录。

| 参数        | 类型     | 必填 | 说明   |
| --------- | ------ | -- | ---- |
| table     | String | 是  | 表名   |
| condition | Object | 是  | 查询条件 |

**返回值**: `Map<String, Object>` - 单条记录或 null

**示例**:

```iapp
s user = dbone("users", map("id", 1))
s user2 = dbone("users", map("username =", "admin"))
```

***

### dball(table, condition)

查询所有记录。

| 参数        | 类型     | 必填 | 说明   |
| --------- | ------ | -- | ---- |
| table     | String | 是  | 表名   |
| condition | Object | 是  | 查询条件 |

**返回值**: `List<Map<String, Object>>` - 记录列表

**示例**:

```iapp
s users = dball("users", map("status", "active"))
```

***

### dbpage(table, condition, page, size)

分页查询。

| 参数        | 类型     | 必填 | 说明       |
| --------- | ------ | -- | -------- |
| table     | String | 是  | 表名       |
| condition | Object | 是  | 查询条件     |
| page      | Number | 是  | 页码（从1开始） |
| size      | Number | 是  | 每页数量     |

**返回值**: `Map` - 分页结果

```json
{
  "total": 100,
  "page": 1,
  "size": 10,
  "totalPages": 10,
  "data": [...]
}
```

**示例**:

```iapp
s result = dbpage("posts", map("status", "published"), 1, 10)
s total = mget(result, "total")
s data = mget(result, "data")
```

***

### dbcount(table, condition)

统计记录数。

| 参数        | 类型     | 必填 | 说明   |
| --------- | ------ | -- | ---- |
| table     | String | 是  | 表名   |
| condition | Object | 是  | 查询条件 |

**返回值**: `Number` - 记录数量

**示例**:

```iapp
n count = dbcount("users", map("status", "active"))
```

***

### dbinsert(table, data)

插入记录。

| 参数    | 类型     | 必填 | 说明     |
| ----- | ------ | -- | ------ |
| table | String | 是  | 表名     |
| data  | Map    | 是  | 要插入的数据 |

**返回值**: `Number` - 插入记录的 ID

**示例**:

```iapp
s userId = dbinsert("users", map(
    "username", "test",
    "password", "hashed_password",
    "email", "test@example.com"
))
```

***

### dbupdate(table, data, condition)

更新记录。

| 参数        | 类型     | 必填 | 说明     |
| --------- | ------ | -- | ------ |
| table     | String | 是  | 表名     |
| data      | Map    | 是  | 要更新的数据 |
| condition | Object | 是  | 更新条件   |

**返回值**: `Number` - 影响的行数

**示例**:

```iapp
n rows = dbupdate("users", map("status", "inactive"), map("id", 1))
```

***

### dbdelete(table, condition)

删除记录。

| 参数        | 类型     | 必填 | 说明   |
| --------- | ------ | -- | ---- |
| table     | String | 是  | 表名   |
| condition | Object | 是  | 删除条件 |

**返回值**: `Number` - 影响的行数

**示例**:

```iapp
n rows = dbdelete("users", map("id", 1))
```

***

### dbsearch(table, fields, keyword, page, size)

搜索记录。

| 参数      | 类型          | 必填 | 说明               |
| ------- | ----------- | -- | ---------------- |
| table   | String      | 是  | 表名               |
| fields  | String/List | 是  | 搜索字段（逗号分隔字符串或数组） |
| keyword | String      | 是  | 搜索关键词            |
| page    | Number      | 是  | 页码               |
| size    | Number      | 是  | 每页数量             |

**返回值**: `Map` - 分页搜索结果

**示例**:

```iapp
# 在 title 和 content 字段中搜索
s result = dbsearch("posts", "title,content", "关键词", 1, 10)
```

***

### dbexec(sql)

执行原生 SQL。

| 参数  | 类型     | 必填 | 说明     |
| --- | ------ | -- | ------ |
| sql | String | 是  | SQL 语句 |

**返回值**: `Boolean` - 是否执行成功

**示例**:

```iapp
dbexec("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, username TEXT)")
```

***

## 文件上传函数

### file(name)

获取上传的文件。

| 参数   | 类型     | 必填 | 说明    |
| ---- | ------ | -- | ----- |
| name | String | 是  | 表单字段名 |

**返回值**: `UploadedFile` - 文件对象

**示例**:

```iapp
s avatar = file("avatar")
```

***

### files()

获取所有上传的文件。

| 参数 | 类型 | 说明 |
| -- | -- | -- |
| 无  | -  | -  |

**返回值**: `Map<String, UploadedFile>` - 文件对象映射

***

### gfn(fileObj)

获取文件名。

| 参数      | 类型           | 必填 | 说明   |
| ------- | ------------ | -- | ---- |
| fileObj | UploadedFile | 是  | 文件对象 |

**返回值**: `String` - 原始文件名

**示例**:

```iapp
s filename = gfn(avatar)
```

***

### gfs(fileObj)

获取文件大小。

| 参数      | 类型           | 必填 | 说明   |
| ------- | ------------ | -- | ---- |
| fileObj | UploadedFile | 是  | 文件对象 |

**返回值**: `Number` - 文件大小（字节）

**示例**:

```iapp
n size = gfs(avatar)
```

***

### gft(fileObj)

获取文件类型。

| 参数      | 类型           | 必填 | 说明   |
| ------- | ------------ | -- | ---- |
| fileObj | UploadedFile | 是  | 文件对象 |

**返回值**: `String` - MIME 类型

**示例**:

```iapp
s type = gft(avatar)
```

***

### gfe(fileObj)

获取文件扩展名。

| 参数      | 类型           | 必填 | 说明   |
| ------- | ------------ | -- | ---- |
| fileObj | UploadedFile | 是  | 文件对象 |

**返回值**: `String` - 文件扩展名（如 `.jpg`）

**示例**:

```iapp
s ext = gfe(avatar)
```

***

### sf(fileObj, savePath, generateName?)

保存文件。

| 参数           | 类型           | 必填 | 说明        |
| ------------ | ------------ | -- | --------- |
| fileObj      | UploadedFile | 是  | 文件对象      |
| savePath     | String       | 是  | 保存目录      |
| generateName | Boolean      | 否  | 是否生成随机文件名 |

**返回值**: `Map` - 保存结果

```json
{
  "success": true,
  "msg": "保存成功",
  "filename": "avatar.jpg",
  "path": "/uploads/avatar.jpg",
  "size": 1024
}
```

**示例**:

```iapp
s avatar = file("avatar")

f(avatar == null)
{
    json(map("code", 400, "msg", "请选择文件"))
    endcode
}

# 保存文件，自动生成随机文件名
s result = sf(avatar, "./uploads/avatar", true)

f(mget(result, "success") == true)
{
    json(map("code", 0, "msg", "上传成功", "path", mget(result, "path")))
}
else
{
    json(map("code", 500, "msg", mget(result, "msg")))
}
```

***

## 工具函数

### map(key1, value1, key2, value2, ...)

创建 Map 对象。

| 参数         | 类型     | 必填 | 说明        |
| ---------- | ------ | -- | --------- |
| key, value | Object | 否  | 键值对（交替传入） |

**返回值**: `Map<String, Object>` - Map 对象

**示例**:

```iapp
s user = map("name", "张三", "age", 25, "city", "北京")
```

***

### mget(map, key)

获取 Map 中的值。

| 参数  | 类型     | 必填 | 说明     |
| --- | ------ | -- | ------ |
| map | Map    | 是  | Map 对象 |
| key | String | 是  | 键名     |

**返回值**: `Object` - 对应的值

**示例**:

```iapp
s name = mget(user, "name")
```

***

### mset(map, key, value)

设置 Map 中的值。

| 参数    | 类型     | 必填 | 说明     |
| ----- | ------ | -- | ------ |
| map   | Map    | 是  | Map 对象 |
| key   | String | 是  | 键名     |
| value | Object | 是  | 值      |

**返回值**: 无

**示例**:

```iapp
mset(user, "age", 26)
```

***

### mkeys(map)

获取 Map 的所有键。

| 参数  | 类型  | 必填 | 说明     |
| --- | --- | -- | ------ |
| map | Map | 是  | Map 对象 |

**返回值**: `List<String>` - 键名列表

**示例**:

```iapp
s keys = mkeys(user)
```

***

### mhas(map, key)

检查 Map 是否包含某个键。

| 参数  | 类型     | 必填 | 说明     |
| --- | ------ | -- | ------ |
| map | Map    | 是  | Map 对象 |
| key | String | 是  | 键名     |

**返回值**: `Boolean` - 是否包含

**示例**:

```iapp
f(mhas(user, "email"))
{
    syso("有邮箱")
}
```

***

### arr(element1, element2, ...)

创建数组。

| 参数       | 类型     | 必填 | 说明   |
| -------- | ------ | -- | ---- |
| elements | Object | 否  | 数组元素 |

**返回值**: `List<Object>` - 数组

**示例**:

```iapp
s list = arr("a", "b", "c")
```

***

### arrpush(array, element)

向数组添加元素。

| 参数      | 类型     | 必填 | 说明     |
| ------- | ------ | -- | ------ |
| array   | List   | 是  | 数组     |
| element | Object | 是  | 要添加的元素 |

**返回值**: `List` - 新数组

**示例**:

```iapp
s list = arr("a", "b")
s newList = arrpush(list, "c")
```

***

### length(obj)

获取长度。

| 参数  | 类型     | 必填 | 说明         |
| --- | ------ | -- | ---------- |
| obj | Object | 是  | 字符串/数组/Map |

**返回值**: `Number` - 长度

**示例**:

```iapp
n len = length("hello")
n arrLen = length(arr(1, 2, 3))
n mapLen = length(map("a", 1, "b", 2))
```

***

## 认证函数

### register(table, username, password, extra)

用户注册。

| 参数       | 类型     | 必填 | 说明           |
| -------- | ------ | -- | ------------ |
| table    | String | 是  | 用户表名         |
| username | String | 是  | 用户名          |
| password | String | 是  | 密码（明文，会自动加密） |
| extra    | Map    | 是  | 额外字段         |

**返回值**: `Map` - 注册结果

```json
{
  "success": true,
  "userId": 1,
  "msg": "注册成功"
}
```

**示例**:

```iapp
s result = register("users", username, password, map("email", email))
```

***

### login(table, username, password)

用户登录。

| 参数       | 类型     | 必填 | 说明   |
| -------- | ------ | -- | ---- |
| table    | String | 是  | 用户表名 |
| username | String | 是  | 用户名  |
| password | String | 是  | 密码   |

**返回值**: `Map` - 登录结果

```json
{
  "success": true,
  "token": "abc123...",
  "user": {"id": 1, "username": "admin"},
  "msg": "登录成功"
}
```

**示例**:

```iapp
s result = login("users", username, password)

f(mget(result, "success") == true)
{
    setcookie("token", mget(result, "token"), 86400)
    json(map("code", 0, "msg", "登录成功"))
}
else
{
    json(map("code", 401, "msg", mget(result, "msg")))
}
```

***

### verify(token)

验证 Token。

| 参数    | 类型     | 必填 | 说明          |
| ----- | ------ | -- | ----------- |
| token | String | 是  | 登录返回的 Token |

**返回值**: `Map` - 验证结果

```json
{
  "valid": true,
  "userId": 1,
  "username": "admin"
}
```

**示例**:

```iapp
s token = getcookie("token")
s v = verify(token)

f(mget(v, "valid") != true)
{
    redirect("/login")
    endcode
}

s userId = mget(v, "userId")
```

***

### logout(token)

退出登录。

| 参数    | 类型     | 必填 | 说明         |
| ----- | ------ | -- | ---------- |
| token | String | 是  | 要注销的 Token |

**返回值**: `Boolean` - 是否成功

**示例**:

```iapp
s token = getcookie("token")
logout(token)
delcookie("token")
json(map("code", 0, "msg", "已退出"))
```

***

### hashpassword(password)

密码加密。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| password | String | 是 | 明文密码 |

**返回值**: `String` - 加密后的密码

**示例**:

```iapp
s hashed = hashpassword("123456")
```

***

### verifypassword(password, hash)

验证密码。

| 参数       | 类型     | 必填 | 说明     |
| -------- | ------ | -- | ------ |
| password | String | 是  | 明文密码   |
| hash     | String | 是  | 加密后的密码 |

**返回值**: `Boolean` - 是否匹配

**示例**:

```iapp
f(verifypassword(password, storedHash))
{
    syso("密码正确")
}
```

***

## 条件查询函数

### like(field, pattern)

模糊查询条件。

| 参数      | 类型     | 必填 | 说明             |
| ------- | ------ | -- | -------------- |
| field   | String | 是  | 字段名            |
| pattern | String | 是  | 匹配模式（可用 % 通配符） |

**返回值**: `QueryCondition` - 查询条件对象

**示例**:

```iapp
s users = dball("users", like("username", "%admin%"))
```

***

### in(field, values)

IN 查询条件。

| 参数     | 类型     | 必填 | 说明  |
| ------ | ------ | -- | --- |
| field  | String | 是  | 字段名 |
| values | List   | 是  | 值列表 |

**返回值**: `QueryCondition` - 查询条件对象

**示例**:

```iapp
s ids = arr(1, 2, 3)
s users = dball("users", in("id", ids))
```

***

### between(field, value1, value2)

区间查询条件。

| 参数     | 类型     | 必填 | 说明  |
| ------ | ------ | -- | --- |
| field  | String | 是  | 字段名 |
| value1 | Object | 是  | 起始值 |
| value2 | Object | 是  | 结束值 |

**返回值**: `QueryCondition` - 查询条件对象

**示例**:

```iapp
s posts = dball("posts", between("created_at", startTime, endTime))
```

***

### isnull(field)

为空查询条件。

| 参数    | 类型     | 必填 | 说明  |
| ----- | ------ | -- | --- |
| field | String | 是  | 字段名 |

**返回值**: `QueryCondition` - 查询条件对象

**示例**:

```iapp
s users = dball("users", isnull("deleted_at"))
```

***

### notnull(field)

非空查询条件。

| 参数    | 类型     | 必填 | 说明  |
| ----- | ------ | -- | --- |
| field | String | 是  | 字段名 |

**返回值**: `QueryCondition` - 查询条件对象

**示例**:

```iapp
s users = dball("users", notnull("email"))
```

***

### and(condition1, condition2, ...)

AND 组合条件。

| 参数         | 类型     | 必填 | 说明     |
| ---------- | ------ | -- | ------ |
| conditions | Object | 是  | 多个查询条件 |

**返回值**: `QueryCondition` - 组合查询条件

**示例**:

```iapp
s users = dball("users", and(
    map("status", "active"),
    like("email", "%@gmail.com")
))
```

***

### or(condition1, condition2, ...)

OR 组合条件。

| 参数         | 类型     | 必填 | 说明     |
| ---------- | ------ | -- | ------ |
| conditions | Object | 是  | 多个查询条件 |

**返回值**: `QueryCondition` - 组合查询条件

**示例**:

```iapp
s users = dball("users", or(
    map("role", "admin"),
    map("role", "moderator")
))
```

***

## 加密函数

### md5(data)

计算 MD5 哈希值。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| data | String | 是 | 要哈希的数据 |

**返回值**: `String` - 32位十六进制哈希值

**示例**:

```iapp
s hash = md5("hello world")
```

***

### sha1(data)

计算 SHA-1 哈希值（已弃用，建议使用 sha256）。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| data | String | 是 | 要哈希的数据 |

**返回值**: `String` - 40位十六进制哈希值

**注意**: SHA-1 已被证明存在碰撞漏洞，建议使用 sha256() 替代。

**示例**:

```iapp
s hash = sha1("hello world")
```

***

### sha256(data)

计算 SHA-256 哈希值。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| data | String | 是 | 要哈希的数据 |

**返回值**: `String` - 64位十六进制哈希值

**示例**:

```iapp
s hash = sha256("hello world")
```

***

### hmacsha256(data, key)

计算 HMAC-SHA256 哈希值。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| data | String | 是 | 要哈希的数据 |
| key | String | 是 | 密钥 |

**返回值**: `String` - 64位十六进制哈希值

**示例**:

```iapp
s signature = hmacsha256("message", "secret_key")
```

***

### base64encode(data)

Base64 编码。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| data | String | 是 | 要编码的数据 |

**返回值**: `String` - Base64 编码字符串

**示例**:

```iapp
s encoded = base64encode("hello world")
```

***

### base64decode(data)

Base64 解码。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| data | String | 是 | Base64 编码字符串 |

**返回值**: `String` - 解码后的字符串

**示例**:

```iapp
s decoded = base64decode("aGVsbG8gd29ybGQ=")
```

***

### aesencrypt(data, key)

AES 加密。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| data | String | 是 | 要加密的数据 |
| key | String | 是 | 加密密钥（16字节） |

**返回值**: `String` - Base64 编码的加密数据（包含IV）

**示例**:

```iapp
s encrypted = aesencrypt("敏感数据", "mysecretkey12345")
```

***

### aesdecrypt(data, key)

AES 解密。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| data | String | 是 | 加密数据（Base64编码） |
| key | String | 是 | 加密密钥（16字节） |

**返回值**: `String` - 解密后的原始数据

**示例**:

```iapp
s decrypted = aesdecrypt(encrypted, "mysecretkey12345")
```

***

## JWT函数

### jwtencode(payload, secret)

生成 JWT Token。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| payload | Map | 是 | JWT 载荷数据 |
| secret | String | 是 | 签名密钥 |

**返回值**: `String` - JWT Token

**示例**:

```iapp
s token = jwtencode(map("userId", 1, "username", "admin"), "my_secret_key")
```

***

### jwtdecode(token)

解码 JWT Token（不验证签名）。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| token | String | 是 | JWT Token |

**返回值**: `Map` - 解码后的载荷数据

**示例**:

```iapp
s payload = jwtdecode(token)
s userId = mget(payload, "userId")
```

***

### jwtverify(token, secret)

验证并解码 JWT Token。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| token | String | 是 | JWT Token |
| secret | String | 是 | 签名密钥 |

**返回值**: `Map` - 验证成功返回载荷数据，失败抛出异常

**示例**:

```iapp
s payload = jwtverify(token, "my_secret_key")
s userId = mget(payload, "userId")
```

***

## Session函数

### session(key)

获取 Session 值。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| key | String | 是 | Session 键名 |

**返回值**: `Object` - Session 值

**示例**:

```iapp
s userId = session("userId")
```

***

### setsession(key, value, ttl?)

设置 Session 值。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| key | String | 是 | Session 键名 |
| value | Object | 是 | Session 值 |
| ttl | Number | 否 | 有效期（秒），默认1800秒 |

**返回值**: `Boolean` - 是否设置成功

**示例**:

```iapp
setsession("userId", 1)
setsession("cart", map("items", arr()), 3600)
```

***

### delsession(key)

删除 Session 值。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| key | String | 是 | Session 键名 |

**返回值**: `Boolean` - 是否删除成功

**示例**:

```iapp
delsession("userId")
```

***

### hassession(key)

检查 Session 是否存在。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| key | String | 是 | Session 键名 |

**返回值**: `Boolean` - 是否存在

**示例**:

```iapp
f(hassession("userId"))
{
    s userId = session("userId")
}
```

***

## 邮件函数

### mailconfig(host, port, username, password, ssl?)

配置邮件服务器。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| host | String | 是 | SMTP 服务器地址 |
| port | Number | 是 | SMTP 端口 |
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| ssl | Boolean | 否 | 是否启用SSL，默认false |

**返回值**: `Boolean` - 是否配置成功

**示例**:

```iapp
mailconfig("smtp.example.com", 587, "user@example.com", "password", true)
```

***

### sendmail(to, subject, body, html?)

发送邮件。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| to | String | 是 | 收件人邮箱 |
| subject | String | 是 | 邮件主题 |
| body | String | 是 | 邮件内容 |
| html | Boolean | 否 | 是否HTML格式，默认false |

**返回值**: `Boolean` - 是否发送成功

**示例**:

```iapp
s result = sendmail("user@example.com", "欢迎注册", "<h1>欢迎</h1><p>感谢注册</p>", true)
```

***

## 环境变量函数

### env(key, default?)

获取环境变量。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| key | String | 是 | 环境变量名 |
| default | String | 否 | 默认值 |

**返回值**: `String` - 环境变量值或默认值

**示例**:

```iapp
s dbHost = env("DB_HOST", "localhost")
s dbPort = env("DB_PORT", "3306")
```

***

### loadenv(path?)

加载 .env 文件。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| path | String | 否 | .env 文件路径，默认项目根目录 |

**返回值**: `Boolean` - 是否加载成功

**示例**:

```iapp
loadenv()
loadenv("./config/.env")
```

***

## CORS函数

### cors(config)

配置 CORS 跨域。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| config | Map | 是 | CORS 配置 |

**配置项**:

| 参数 | 类型 | 说明 |
|------|------|------|
| origins | String/List | 允许的源 |
| methods | String/List | 允许的方法 |
| headers | String/List | 允许的请求头 |
| exposedHeaders | String/List | 暴露的响应头 |
| credentials | Boolean | 是否允许携带凭证 |
| maxAge | Number | 预检请求缓存时间（秒） |

**返回值**: `Boolean` - 是否配置成功

**示例**:

```iapp
cors(map(
    "origins", arr("http://localhost:3000", "https://example.com"),
    "methods", arr("GET", "POST", "PUT", "DELETE"),
    "headers", arr("Content-Type", "Authorization"),
    "credentials", true,
    "maxAge", 3600
))
```

***

## 服务器配置函数

### port(port)

设置服务器端口。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| port | Number | 是 | 端口号（1-65535） |

**返回值**: 无

**示例**:

```iapp
port(8080)
```

***

### config(key, value)

设置服务器配置。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| key | String | 是 | 配置项名称 |
| value | Object | 是 | 配置值 |

**可用配置项**:

| 配置项 | 类型 | 说明 |
|--------|------|------|
| port | Number | 服务端口 |
| debug | Boolean | 调试模式 |
| safe | Boolean | 安全模式 |
| preload | Boolean | 脚本预加载 |
| static | Boolean | 静态文件服务 |
| poolsize | Number | 连接池最大大小 |
| poolinit | Number | 连接池初始大小 |
| pooltimeout | Number | 连接超时时间（毫秒） |
| usepool | Boolean | 使用连接池 |
| asyncpool | Number | 异步线程池大小 |
| asynctimeout | Number | 异步超时时间（毫秒） |
| http2 | Boolean | 启用HTTP/2 |
| compression | Boolean | 启用响应压缩 |
| compression_min | Number | 最小压缩大小（字节） |
| max_upload_size | Number | 最大上传文件大小（字节） |

**返回值**: `Boolean` - 是否设置成功

**示例**:

```iapp
config("port", 8080)
config("debug", true)
config("poolsize", 100)
config("compression", true)
config("max_upload_size", 50 * 1024 * 1024)
```

***

### upc(action, value?)

配置文件上传。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| action | String | 是 | 操作类型 |
| value | Object | 否 | 配置值 |

**可用操作**:

| 操作 | 说明 |
|------|------|
| extensions | 设置允许的文件扩展名列表 |
| maxsize | 设置最大文件大小（字节） |
| add_extensions | 添加允许的文件扩展名 |
| reset | 重置为默认配置 |

**返回值**: `Boolean` - 是否设置成功

**示例**:

```iapp
upc("extensions", arr(".jpg", ".png", ".gif"))
upc("maxsize", 10 * 1024 * 1024)
upc("add_extensions", arr(".pdf", ".doc"))
upc("reset")
```

***

## 异步函数

### async(task, timeout?)

执行异步任务。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| task | Object | 是 | 任务对象 |
| timeout | Number | 否 | 超时时间（毫秒），默认30000 |

**返回值**: `Number` - 任务ID

**示例**:

```iapp
s taskId = async(task, 60000)
```

***

### asyncwait(taskId, timeout?)

等待异步任务完成。

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| taskId | Number | 是 | 任务ID |
| timeout | Number | 否 | 超时时间（毫秒），默认30000 |

**返回值**: `Object` - 任务结果

**示例**:

```iapp
s result = asyncwait(taskId, 60000)
```

***

## 服务器信息函数

### info()

显示服务器信息页面。

| 参数 | 类型 | 说明 |
|------|------|------|
| 无 | - | - |

**返回值**: 无（直接输出HTML页面）

**示例**:

```iapp
info()
```

显示的信息包括：
- 服务器信息（名称、版本、端口等）
- Java 环境（版本、供应商、主目录等）
- 内存使用（堆内存、非堆内存、使用率等）
- 线程信息（活动线程数、峰值线程数等）
- 性能监控（请求总数、缓存命中率等）
- 脚本缓存（缓存脚本数、命中次数等）
- 已注册函数列表
- 系统属性

***

## 完整示例

### 用户注册 API

```iapp
# webroot/user/register.iapp

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

s extra = map("email", email)
s result = register("users", username, password, extra)

f(mget(result, "success") == true)
{
    json(map("code", 0, "msg", "注册成功", "userId", mget(result, "userId")))
}
else
{
    json(map("code", 400, "msg", mget(result, "msg")))
}
```

### 用户登录 API

```iapp
# webroot/user/login.iapp

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
}
```

### 文件上传 API

```iapp
# webroot/upload.iapp

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

s result = sf(avatar, "./uploads/avatar", true)

f(mget(result, "success") == true)
{
    json(map("code", 0, "msg", "上传成功", "path", mget(result, "path")))
}
else
{
    json(map("code", 500, "msg", mget(result, "msg")))
}
```

### 帖子列表 API

```iapp
# webroot/post/list.iapp

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
))
```

***

## 应用配置 (app.iapp)

在项目根目录创建 `app.iapp` 可以进行初始化配置：

```iapp
# 设置端口
port(8080)

# 连接数据库
db("sqlite", "data/app.db")

# 创建表
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
)")
```

***

## 注意事项

1. **endcode**: 使用 `endcode` 可以立即结束脚本执行，常用于条件判断后返回错误响应
2. **条件表达式**: 在 Map 条件中可以使用 `"字段 操作符"` 格式，如 `map("age >", 18)`
3. **JSON 请求**: 当请求 Content-Type 为 `application/json` 时，`post()` 函数会自动解析 JSON 字段
4. **Token 存储**: 登录 Token 存储在内存中，服务器重启后需要重新登录

***

*YuWeb v1.0.0 - 轻量级 Web 后端框架*
