<template>
  <div class="api-page">
    <section class="page-header">
      <div class="container">
        <h1 class="page-title">API 文档</h1>
        <p class="page-desc">完整的函数参考文档</p>
      </div>
    </section>

    <div class="container">
      <div class="api-layout">
        <nav class="api-nav">
          <ul>
            <li v-for="category in categories" :key="category.id">
              <a :href="'#' + category.id" :class="{ active: activeCategory === category.id }">
                {{ category.name }}
              </a>
            </li>
          </ul>
        </nav>

        <main class="api-content">
          <section :id="category.id" v-for="category in categories" :key="category.id" class="api-section">
            <h2 class="category-title">{{ category.name }}</h2>
            <ApiCard
              v-for="api in category.apis"
              :key="api.name"
              :id="api.name"
              :name="api.name"
              :description="api.description"
              :returnType="api.returnType"
              :params="api.params"
              :example="api.example"
            />
          </section>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import ApiCard from '../components/ApiCard.vue'

const activeCategory = ref('request')

const categories = [
  {
    id: 'request',
    name: '请求函数',
    apis: [
      {
        name: 'method',
        description: '获取请求方法',
        returnType: 'String - GET/POST/PUT/DELETE 等',
        params: [],
        example: `f(method() != "POST")
{
    json(map("code", 405, "msg", "仅支持POST请求"))
    endcode
}`
      },
      {
        name: 'get',
        description: '获取 URL 查询参数',
        returnType: 'String - 参数值或默认值',
        params: [
          { name: 'name', type: 'String', required: true, desc: '参数名' },
          { name: 'default', type: 'String', required: false, desc: '默认值' }
        ],
        example: `s name = get("name")
s page = get("page", "1")`
      },
      {
        name: 'gets',
        description: '获取所有 URL 查询参数',
        returnType: 'Map<String, String> - 所有参数的键值对',
        params: [],
        example: `s params = gets()
s name = mget(params, "name")`
      },
      {
        name: 'post',
        description: '获取 POST 表单参数或 JSON 字段',
        returnType: 'String - 参数值',
        params: [
          { name: 'name', type: 'String', required: true, desc: '参数名' },
          { name: 'default', type: 'String', required: false, desc: '默认值' }
        ],
        example: `s username = post("username")
s password = post("password")`
      },
      {
        name: 'posts',
        description: '获取所有 POST 表单参数',
        returnType: 'Map<String, String> - 所有参数的键值对',
        params: []
      },
      {
        name: 'body',
        description: '获取原始请求体',
        returnType: 'String - 原始请求体内容',
        params: [],
        example: `s rawBody = body()`
      },
      {
        name: 'json',
        description: '获取 JSON 请求体或返回 JSON 响应',
        returnType: '无参数时返回 Map，有参数时无返回值',
        params: [
          { name: 'data', type: 'Object', required: false, desc: '要返回的数据' }
        ],
        example: `# 获取 JSON 请求体
s data = json()
s username = mget(data, "username")

# 返回 JSON 响应
json(map("code", 0, "msg", "成功"))`
      },
      {
        name: 'path',
        description: '获取请求路径',
        returnType: 'String - 如 /user/login',
        params: [],
        example: `s p = path()`
      },
      {
        name: 'url',
        description: '获取完整请求 URL',
        returnType: 'String - 完整 URL',
        params: [],
        example: `s u = url()`
      },
      {
        name: 'header',
        description: '获取请求头',
        returnType: 'String - 请求头值',
        params: [
          { name: 'name', type: 'String', required: true, desc: '请求头名称' }
        ],
        example: `s auth = header("Authorization")`
      },
      {
        name: 'clientip',
        description: '获取客户端 IP 地址',
        returnType: 'String - 客户端 IP',
        params: [],
        example: `s ip = clientip()`
      },
      {
        name: 'useragent',
        description: '获取 User-Agent',
        returnType: 'String - User-Agent 字符串',
        params: []
      },
      {
        name: 'isajax',
        description: '判断是否为 AJAX 请求',
        returnType: 'Boolean - 是否 AJAX 请求',
        params: [],
        example: `f(isajax())
{
    json(map("code", 0))
}`
      },
      {
        name: 'isjson',
        description: '判断请求是否为 JSON 格式',
        returnType: 'Boolean - 是否 JSON 请求',
        params: []
      },
      {
        name: 'getcookie',
        description: '获取 Cookie 值',
        returnType: 'String - Cookie 值',
        params: [
          { name: 'name', type: 'String', required: true, desc: 'Cookie 名称' }
        ],
        example: `s token = getcookie("token")`
      },
      {
        name: 'setcookie',
        description: '设置 Cookie',
        returnType: '无',
        params: [
          { name: 'name', type: 'String', required: true, desc: 'Cookie 名称' },
          { name: 'value', type: 'String', required: true, desc: 'Cookie 值' },
          { name: 'maxAge', type: 'Number', required: true, desc: '有效期（秒）' }
        ],
        example: `setcookie("token", "abc123", 86400)`
      },
      {
        name: 'delcookie',
        description: '删除 Cookie',
        returnType: '无',
        params: [
          { name: 'name', type: 'String', required: true, desc: 'Cookie 名称' }
        ],
        example: `delcookie("token")`
      }
    ]
  },
  {
    id: 'response',
    name: '响应函数',
    apis: [
      {
        name: 'json',
        description: '返回 JSON 响应',
        returnType: '无',
        params: [
          { name: 'data', type: 'Object', required: true, desc: '要返回的数据' }
        ],
        example: `json(map("code", 0, "msg", "成功", "data", result))`
      },
      {
        name: 'text',
        description: '返回纯文本响应',
        returnType: '无',
        params: [
          { name: 'content', type: 'String', required: true, desc: '文本内容' }
        ],
        example: `text("Hello World")`
      },
      {
        name: 'html',
        description: '返回 HTML 响应',
        returnType: '无',
        params: [
          { name: 'content', type: 'String', required: true, desc: 'HTML 内容' }
        ],
        example: `html("<h1>标题</h1><p>内容</p>")`
      },
      {
        name: 'status',
        description: '设置 HTTP 状态码',
        returnType: '无',
        params: [
          { name: 'code', type: 'Number', required: true, desc: 'HTTP 状态码' }
        ],
        example: `status(404)
json(map("msg", "资源不存在"))`
      },
      {
        name: 'error',
        description: '返回错误响应',
        returnType: '无',
        params: [
          { name: 'code', type: 'Number', required: true, desc: 'HTTP 状态码' },
          { name: 'message', type: 'String', required: true, desc: '错误信息' }
        ],
        example: `error(500, "服务器内部错误")`
      },
      {
        name: 'redirect',
        description: '重定向',
        returnType: '无',
        params: [
          { name: 'location', type: 'String', required: true, desc: '目标 URL' }
        ],
        example: `redirect("/login")`
      },
      {
        name: 'setHeader',
        description: '设置响应头',
        returnType: '无',
        params: [
          { name: 'name', type: 'String', required: true, desc: '响应头名称' },
          { name: 'value', type: 'String', required: true, desc: '响应头值' }
        ],
        example: `setHeader("Content-Type", "application/json")`
      }
    ]
  },
  {
    id: 'database',
    name: '数据库函数',
    apis: [
      {
        name: 'db',
        description: '连接数据库',
        returnType: 'Boolean - 是否连接成功',
        params: [
          { name: 'type', type: 'String', required: true, desc: '数据库类型: sqlite 或 mysql' },
          { name: 'path', type: 'String', required: true, desc: 'SQLite 文件路径 或 MySQL 连接字符串' }
        ],
        example: `# SQLite
db("sqlite", "data/app.db")

# MySQL
db("mysql", "mysql://root:password@localhost:3306/mydb")`
      },
      {
        name: 'dbone',
        description: '查询单条记录',
        returnType: 'Map<String, Object> - 单条记录或 null',
        params: [
          { name: 'table', type: 'String', required: true, desc: '表名' },
          { name: 'condition', type: 'Object', required: true, desc: '查询条件' }
        ],
        example: `s user = dbone("users", map("id", 1))
s user2 = dbone("users", map("username =", "admin"))`
      },
      {
        name: 'dball',
        description: '查询所有记录',
        returnType: 'List<Map<String, Object>> - 记录列表',
        params: [
          { name: 'table', type: 'String', required: true, desc: '表名' },
          { name: 'condition', type: 'Object', required: true, desc: '查询条件' }
        ],
        example: `s users = dball("users", map("status", "active"))`
      },
      {
        name: 'dbpage',
        description: '分页查询',
        returnType: 'Map - 分页结果',
        params: [
          { name: 'table', type: 'String', required: true, desc: '表名' },
          { name: 'condition', type: 'Object', required: true, desc: '查询条件' },
          { name: 'page', type: 'Number', required: true, desc: '页码（从1开始）' },
          { name: 'size', type: 'Number', required: true, desc: '每页数量' }
        ],
        example: `s result = dbpage("posts", map("status", "published"), 1, 10)
s total = mget(result, "total")
s data = mget(result, "data")`
      },
      {
        name: 'dbcount',
        description: '统计记录数',
        returnType: 'Number - 记录数量',
        params: [
          { name: 'table', type: 'String', required: true, desc: '表名' },
          { name: 'condition', type: 'Object', required: true, desc: '查询条件' }
        ],
        example: `n count = dbcount("users", map("status", "active"))`
      },
      {
        name: 'dbinsert',
        description: '插入记录',
        returnType: 'Number - 插入记录的 ID',
        params: [
          { name: 'table', type: 'String', required: true, desc: '表名' },
          { name: 'data', type: 'Map', required: true, desc: '要插入的数据' }
        ],
        example: `s userId = dbinsert("users", map(
    "username", "test",
    "password", "hashed_password",
    "email", "test@example.com"
))`
      },
      {
        name: 'dbupdate',
        description: '更新记录',
        returnType: 'Number - 影响的行数',
        params: [
          { name: 'table', type: 'String', required: true, desc: '表名' },
          { name: 'data', type: 'Map', required: true, desc: '要更新的数据' },
          { name: 'condition', type: 'Object', required: true, desc: '更新条件' }
        ],
        example: `n rows = dbupdate("users", map("status", "inactive"), map("id", 1))`
      },
      {
        name: 'dbdelete',
        description: '删除记录',
        returnType: 'Number - 影响的行数',
        params: [
          { name: 'table', type: 'String', required: true, desc: '表名' },
          { name: 'condition', type: 'Object', required: true, desc: '删除条件' }
        ],
        example: `n rows = dbdelete("users", map("id", 1))`
      },
      {
        name: 'dbsearch',
        description: '搜索记录',
        returnType: 'Map - 分页搜索结果',
        params: [
          { name: 'table', type: 'String', required: true, desc: '表名' },
          { name: 'fields', type: 'String/List', required: true, desc: '搜索字段' },
          { name: 'keyword', type: 'String', required: true, desc: '搜索关键词' },
          { name: 'page', type: 'Number', required: true, desc: '页码' },
          { name: 'size', type: 'Number', required: true, desc: '每页数量' }
        ],
        example: `s result = dbsearch("posts", "title,content", "关键词", 1, 10)`
      },
      {
        name: 'dbexec',
        description: '执行原生 SQL',
        returnType: 'Boolean - 是否执行成功',
        params: [
          { name: 'sql', type: 'String', required: true, desc: 'SQL 语句' }
        ],
        example: `dbexec("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, username TEXT)")`
      }
    ]
  },
  {
    id: 'file',
    name: '文件上传函数',
    apis: [
      {
        name: 'file',
        description: '获取上传的文件',
        returnType: 'UploadedFile - 文件对象',
        params: [
          { name: 'name', type: 'String', required: true, desc: '表单字段名' }
        ],
        example: `s avatar = file("avatar")`
      },
      {
        name: 'files',
        description: '获取所有上传的文件',
        returnType: 'Map<String, UploadedFile> - 文件对象映射',
        params: []
      },
      {
        name: 'gfn',
        description: '获取文件名',
        returnType: 'String - 原始文件名',
        params: [
          { name: 'fileObj', type: 'UploadedFile', required: true, desc: '文件对象' }
        ],
        example: `s filename = gfn(avatar)`
      },
      {
        name: 'gfs',
        description: '获取文件大小',
        returnType: 'Number - 文件大小（字节）',
        params: [
          { name: 'fileObj', type: 'UploadedFile', required: true, desc: '文件对象' }
        ],
        example: `n size = gfs(avatar)`
      },
      {
        name: 'gft',
        description: '获取文件类型',
        returnType: 'String - MIME 类型',
        params: [
          { name: 'fileObj', type: 'UploadedFile', required: true, desc: '文件对象' }
        ],
        example: `s type = gft(avatar)`
      },
      {
        name: 'gfe',
        description: '获取文件扩展名',
        returnType: 'String - 文件扩展名（如 .jpg）',
        params: [
          { name: 'fileObj', type: 'UploadedFile', required: true, desc: '文件对象' }
        ],
        example: `s ext = gfe(avatar)`
      },
      {
        name: 'sf',
        description: '保存文件',
        returnType: 'Map - 保存结果',
        params: [
          { name: 'fileObj', type: 'UploadedFile', required: true, desc: '文件对象' },
          { name: 'savePath', type: 'String', required: true, desc: '保存目录' },
          { name: 'generateName', type: 'Boolean', required: false, desc: '是否生成随机文件名' }
        ],
        example: `s result = sf(avatar, "./uploads/avatar", true)

f(mget(result, "success") == true)
{
    json(map("code", 0, "msg", "上传成功", "path", mget(result, "path")))
}`
      }
    ]
  },
  {
    id: 'auth',
    name: '认证函数',
    apis: [
      {
        name: 'register',
        description: '用户注册',
        returnType: 'Map - 注册结果',
        params: [
          { name: 'table', type: 'String', required: true, desc: '用户表名' },
          { name: 'username', type: 'String', required: true, desc: '用户名' },
          { name: 'password', type: 'String', required: true, desc: '密码（明文，会自动加密）' },
          { name: 'extra', type: 'Map', required: true, desc: '额外字段' }
        ],
        example: `s result = register("users", username, password, map("email", email))`
      },
      {
        name: 'login',
        description: '用户登录',
        returnType: 'Map - 登录结果',
        params: [
          { name: 'table', type: 'String', required: true, desc: '用户表名' },
          { name: 'username', type: 'String', required: true, desc: '用户名' },
          { name: 'password', type: 'String', required: true, desc: '密码' }
        ],
        example: `s result = login("users", username, password)

f(mget(result, "success") == true)
{
    setcookie("token", mget(result, "token"), 86400)
    json(map("code", 0, "msg", "登录成功"))
}`
      },
      {
        name: 'verify',
        description: '验证 Token',
        returnType: 'Map - 验证结果',
        params: [
          { name: 'token', type: 'String', required: true, desc: '登录返回的 Token' }
        ],
        example: `s token = getcookie("token")
s v = verify(token)

f(mget(v, "valid") != true)
{
    redirect("/login")
    endcode
}`
      },
      {
        name: 'logout',
        description: '退出登录',
        returnType: 'Boolean - 是否成功',
        params: [
          { name: 'token', type: 'String', required: true, desc: '要注销的 Token' }
        ],
        example: `s token = getcookie("token")
logout(token)
delcookie("token")`
      },
      {
        name: 'hashpassword',
        description: '密码加密',
        returnType: 'String - 加密后的密码',
        params: [
          { name: 'password', type: 'String', required: true, desc: '明文密码' }
        ],
        example: `s hashed = hashpassword("123456")`
      },
      {
        name: 'verifypassword',
        description: '验证密码',
        returnType: 'Boolean - 是否匹配',
        params: [
          { name: 'password', type: 'String', required: true, desc: '明文密码' },
          { name: 'hash', type: 'String', required: true, desc: '加密后的密码' }
        ],
        example: `f(verifypassword(password, storedHash))
{
    syso("密码正确")
}`
      }
    ]
  },
  {
    id: 'tool',
    name: '工具函数',
    apis: [
      {
        name: 'map',
        description: '创建 Map 对象',
        returnType: 'Map<String, Object> - Map 对象',
        params: [
          { name: 'key, value', type: 'Object', required: false, desc: '键值对（交替传入）' }
        ],
        example: `s user = map("name", "张三", "age", 25, "city", "北京")`
      },
      {
        name: 'mget',
        description: '获取 Map 中的值',
        returnType: 'Object - 对应的值',
        params: [
          { name: 'map', type: 'Map', required: true, desc: 'Map 对象' },
          { name: 'key', type: 'String', required: true, desc: '键名' }
        ],
        example: `s name = mget(user, "name")`
      },
      {
        name: 'mset',
        description: '设置 Map 中的值',
        returnType: '无',
        params: [
          { name: 'map', type: 'Map', required: true, desc: 'Map 对象' },
          { name: 'key', type: 'String', required: true, desc: '键名' },
          { name: 'value', type: 'Object', required: true, desc: '值' }
        ],
        example: `mset(user, "age", 26)`
      },
      {
        name: 'mkeys',
        description: '获取 Map 的所有键',
        returnType: 'List<String> - 键名列表',
        params: [
          { name: 'map', type: 'Map', required: true, desc: 'Map 对象' }
        ],
        example: `s keys = mkeys(user)`
      },
      {
        name: 'mhas',
        description: '检查 Map 是否包含某个键',
        returnType: 'Boolean - 是否包含',
        params: [
          { name: 'map', type: 'Map', required: true, desc: 'Map 对象' },
          { name: 'key', type: 'String', required: true, desc: '键名' }
        ],
        example: `f(mhas(user, "email"))
{
    syso("有邮箱")
}`
      },
      {
        name: 'arr',
        description: '创建数组',
        returnType: 'List<Object> - 数组',
        params: [
          { name: 'elements', type: 'Object', required: false, desc: '数组元素' }
        ],
        example: `s list = arr("a", "b", "c")`
      },
      {
        name: 'arrpush',
        description: '向数组添加元素',
        returnType: 'List - 新数组',
        params: [
          { name: 'array', type: 'List', required: true, desc: '数组' },
          { name: 'element', type: 'Object', required: true, desc: '要添加的元素' }
        ],
        example: `s list = arr("a", "b")
s newList = arrpush(list, "c")`
      },
      {
        name: 'length',
        description: '获取长度',
        returnType: 'Number - 长度',
        params: [
          { name: 'obj', type: 'Object', required: true, desc: '字符串/数组/Map' }
        ],
        example: `n len = length("hello")
n arrLen = length(arr(1, 2, 3))`
      }
    ]
  },
  {
    id: 'crypto',
    name: '加密函数',
    apis: [
      {
        name: 'md5',
        description: '计算 MD5 哈希值',
        returnType: 'String - 32位十六进制哈希值',
        params: [
          { name: 'data', type: 'String', required: true, desc: '要哈希的数据' }
        ],
        example: `s hash = md5("hello world")`
      },
      {
        name: 'sha256',
        description: '计算 SHA-256 哈希值',
        returnType: 'String - 64位十六进制哈希值',
        params: [
          { name: 'data', type: 'String', required: true, desc: '要哈希的数据' }
        ],
        example: `s hash = sha256("hello world")`
      },
      {
        name: 'hmacsha256',
        description: '计算 HMAC-SHA256 哈希值',
        returnType: 'String - 64位十六进制哈希值',
        params: [
          { name: 'data', type: 'String', required: true, desc: '要哈希的数据' },
          { name: 'key', type: 'String', required: true, desc: '密钥' }
        ],
        example: `s signature = hmacsha256("message", "secret_key")`
      },
      {
        name: 'base64encode',
        description: 'Base64 编码',
        returnType: 'String - Base64 编码字符串',
        params: [
          { name: 'data', type: 'String', required: true, desc: '要编码的数据' }
        ],
        example: `s encoded = base64encode("hello world")`
      },
      {
        name: 'base64decode',
        description: 'Base64 解码',
        returnType: 'String - 解码后的字符串',
        params: [
          { name: 'data', type: 'String', required: true, desc: 'Base64 编码字符串' }
        ],
        example: `s decoded = base64decode("aGVsbG8gd29ybGQ=")`
      },
      {
        name: 'aesencrypt',
        description: 'AES 加密',
        returnType: 'String - Base64 编码的加密数据',
        params: [
          { name: 'data', type: 'String', required: true, desc: '要加密的数据' },
          { name: 'key', type: 'String', required: true, desc: '加密密钥（16字节）' }
        ],
        example: `s encrypted = aesencrypt("敏感数据", "mysecretkey12345")`
      },
      {
        name: 'aesdecrypt',
        description: 'AES 解密',
        returnType: 'String - 解密后的原始数据',
        params: [
          { name: 'data', type: 'String', required: true, desc: '加密数据（Base64编码）' },
          { name: 'key', type: 'String', required: true, desc: '加密密钥（16字节）' }
        ],
        example: `s decrypted = aesdecrypt(encrypted, "mysecretkey12345")`
      }
    ]
  },
  {
    id: 'jwt',
    name: 'JWT 函数',
    apis: [
      {
        name: 'jwtencode',
        description: '生成 JWT Token',
        returnType: 'String - JWT Token',
        params: [
          { name: 'payload', type: 'Map', required: true, desc: 'JWT 载荷数据' },
          { name: 'secret', type: 'String', required: true, desc: '签名密钥' }
        ],
        example: `s token = jwtencode(map("userId", 1, "username", "admin"), "my_secret_key")`
      },
      {
        name: 'jwtdecode',
        description: '解码 JWT Token（不验证签名）',
        returnType: 'Map - 解码后的载荷数据',
        params: [
          { name: 'token', type: 'String', required: true, desc: 'JWT Token' }
        ],
        example: `s payload = jwtdecode(token)
s userId = mget(payload, "userId")`
      },
      {
        name: 'jwtverify',
        description: '验证并解码 JWT Token',
        returnType: 'Map - 验证成功返回载荷数据',
        params: [
          { name: 'token', type: 'String', required: true, desc: 'JWT Token' },
          { name: 'secret', type: 'String', required: true, desc: '签名密钥' }
        ],
        example: `s payload = jwtverify(token, "my_secret_key")
s userId = mget(payload, "userId")`
      }
    ]
  },
  {
    id: 'session',
    name: 'Session 函数',
    apis: [
      {
        name: 'session',
        description: '获取 Session 值',
        returnType: 'Object - Session 值',
        params: [
          { name: 'key', type: 'String', required: true, desc: 'Session 键名' }
        ],
        example: `s userId = session("userId")`
      },
      {
        name: 'setsession',
        description: '设置 Session 值',
        returnType: 'Boolean - 是否设置成功',
        params: [
          { name: 'key', type: 'String', required: true, desc: 'Session 键名' },
          { name: 'value', type: 'Object', required: true, desc: 'Session 值' },
          { name: 'ttl', type: 'Number', required: false, desc: '有效期（秒），默认1800秒' }
        ],
        example: `setsession("userId", 1)
setsession("cart", map("items", arr()), 3600)`
      },
      {
        name: 'delsession',
        description: '删除 Session 值',
        returnType: 'Boolean - 是否删除成功',
        params: [
          { name: 'key', type: 'String', required: true, desc: 'Session 键名' }
        ],
        example: `delsession("userId")`
      },
      {
        name: 'hassession',
        description: '检查 Session 是否存在',
        returnType: 'Boolean - 是否存在',
        params: [
          { name: 'key', type: 'String', required: true, desc: 'Session 键名' }
        ],
        example: `f(hassession("userId"))
{
    s userId = session("userId")
}`
      }
    ]
  },
  {
    id: 'mail',
    name: '邮件函数',
    apis: [
      {
        name: 'mailconfig',
        description: '配置邮件服务器',
        returnType: 'Boolean - 是否配置成功',
        params: [
          { name: 'host', type: 'String', required: true, desc: 'SMTP 服务器地址' },
          { name: 'port', type: 'Number', required: true, desc: 'SMTP 端口' },
          { name: 'username', type: 'String', required: true, desc: '用户名' },
          { name: 'password', type: 'String', required: true, desc: '密码' },
          { name: 'ssl', type: 'Boolean', required: false, desc: '是否启用SSL' }
        ],
        example: `mailconfig("smtp.example.com", 587, "user@example.com", "password", true)`
      },
      {
        name: 'sendmail',
        description: '发送邮件',
        returnType: 'Boolean - 是否发送成功',
        params: [
          { name: 'to', type: 'String', required: true, desc: '收件人邮箱' },
          { name: 'subject', type: 'String', required: true, desc: '邮件主题' },
          { name: 'body', type: 'String', required: true, desc: '邮件内容' },
          { name: 'html', type: 'Boolean', required: false, desc: '是否HTML格式' }
        ],
        example: `s result = sendmail("user@example.com", "欢迎注册", "<h1>欢迎</h1><p>感谢注册</p>", true)`
      }
    ]
  },
  {
    id: 'env',
    name: '环境变量函数',
    apis: [
      {
        name: 'env',
        description: '获取环境变量',
        returnType: 'String - 环境变量值或默认值',
        params: [
          { name: 'key', type: 'String', required: true, desc: '环境变量名' },
          { name: 'default', type: 'String', required: false, desc: '默认值' }
        ],
        example: `s dbHost = env("DB_HOST", "localhost")
s dbPort = env("DB_PORT", "3306")`
      },
      {
        name: 'loadenv',
        description: '加载 .env 文件',
        returnType: 'Boolean - 是否加载成功',
        params: [
          { name: 'path', type: 'String', required: false, desc: '.env 文件路径，默认项目根目录' }
        ],
        example: `loadenv()
loadenv("./config/.env")`
      }
    ]
  },
  {
    id: 'cors',
    name: 'CORS 函数',
    apis: [
      {
        name: 'cors',
        description: '配置 CORS 跨域',
        returnType: 'Boolean - 是否配置成功',
        params: [
          { name: 'config', type: 'Map', required: true, desc: 'CORS 配置' }
        ],
        example: `cors(map(
    "origins", arr("http://localhost:3000", "https://example.com"),
    "methods", arr("GET", "POST", "PUT", "DELETE"),
    "headers", arr("Content-Type", "Authorization"),
    "credentials", true,
    "maxAge", 3600
))`
      }
    ]
  },
  {
    id: 'server',
    name: '服务器配置函数',
    apis: [
      {
        name: 'port',
        description: '设置服务器端口',
        returnType: '无',
        params: [
          { name: 'port', type: 'Number', required: true, desc: '端口号（1-65535）' }
        ],
        example: `port(8080)`
      },
      {
        name: 'config',
        description: '设置服务器配置',
        returnType: 'Boolean - 是否设置成功',
        params: [
          { name: 'key', type: 'String', required: true, desc: '配置项名称' },
          { name: 'value', type: 'Object', required: true, desc: '配置值' }
        ],
        example: `config("port", 8080)
config("debug", true)
config("poolsize", 100)
config("compression", true)`
      },
      {
        name: 'upc',
        description: '配置文件上传',
        returnType: 'Boolean - 是否设置成功',
        params: [
          { name: 'action', type: 'String', required: true, desc: '操作类型' },
          { name: 'value', type: 'Object', required: false, desc: '配置值' }
        ],
        example: `upc("extensions", arr(".jpg", ".png", ".gif"))
upc("maxsize", 10 * 1024 * 1024)
upc("add_extensions", arr(".pdf", ".doc"))
upc("reset")`
      },
      {
        name: 'info',
        description: '显示服务器信息页面',
        returnType: '无（直接输出HTML页面）',
        params: [],
        example: `info()`
      }
    ]
  },
  {
    id: 'async',
    name: '异步函数',
    apis: [
      {
        name: 'async',
        description: '执行异步任务',
        returnType: 'Number - 任务ID',
        params: [
          { name: 'task', type: 'Object', required: true, desc: '任务对象' },
          { name: 'timeout', type: 'Number', required: false, desc: '超时时间（毫秒）' }
        ],
        example: `s taskId = async(task, 60000)`
      },
      {
        name: 'asyncwait',
        description: '等待异步任务完成',
        returnType: 'Object - 任务结果',
        params: [
          { name: 'taskId', type: 'Number', required: true, desc: '任务ID' },
          { name: 'timeout', type: 'Number', required: false, desc: '超时时间（毫秒）' }
        ],
        example: `s result = asyncwait(taskId, 60000)`
      }
    ]
  }
]

const handleScroll = () => {
  const sections = document.querySelectorAll('.api-section')
  let current = ''
  
  sections.forEach(section => {
    const sectionTop = section.offsetTop
    if (window.scrollY >= sectionTop - 200) {
      current = section.getAttribute('id')
    }
  })
  
  activeCategory.value = current
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
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

.api-layout {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 3rem;
  padding: 2rem 0;
}

.api-nav {
  position: sticky;
  top: calc(var(--header-height) + 2rem);
  height: fit-content;
}

.api-nav ul {
  list-style: none;
}

.api-nav li {
  margin-bottom: 0.25rem;
}

.api-nav a {
  display: block;
  padding: 0.5rem 0.75rem;
  font-size: 0.875rem;
  color: var(--text-secondary);
  border-left: 2px solid transparent;
  transition: all 0.2s ease;
}

.api-nav a:hover {
  color: var(--primary);
}

.api-nav a.active {
  color: var(--primary);
  border-left-color: var(--primary);
  background-color: var(--bg-secondary);
}

.api-section {
  padding-bottom: 2rem;
  margin-bottom: 2rem;
  border-bottom: 1px solid var(--border);
}

.api-section:last-child {
  border-bottom: none;
}

.category-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 1.5rem;
  padding-bottom: 0.75rem;
  border-bottom: 2px solid var(--primary);
}

@media (max-width: 1024px) {
  .api-layout {
    grid-template-columns: 1fr;
  }
  
  .api-nav {
    display: none;
  }
}

@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }
}
</style>
