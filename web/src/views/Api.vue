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
        name: 'dbinsert',
        description: '插入记录',
        returnType: 'Number - 插入记录的 ID',
        params: [
          { name: 'table', type: 'String', required: true, desc: '表名' },
          { name: 'data', type: 'Map', required: true, desc: '要插入的数据' }
        ],
        example: `s userId = dbinsert("users", map(
    "username", "test",
    "password", "hashed_password"
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

.api-layout {
  display: grid;
  grid-template-columns: 220px 1fr;
  gap: 3rem;
  padding: 2rem 0;
}

.api-nav {
  position: sticky;
  top: calc(var(--header-height) + var(--navbar-margin) * 2 + 2rem);
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
  padding: 0.625rem 1rem;
  font-size: 0.875rem;
  color: var(--text-secondary);
  border-left: 2px solid transparent;
  border-radius: 0 6px 6px 0;
  transition: all var(--transition-fast) ease;
}

.api-nav a:hover {
  color: var(--text-primary);
  background-color: var(--bg-secondary);
}

.api-nav a.active {
  color: var(--cta);
  border-left-color: var(--cta);
  background-color: var(--bg-secondary);
  font-weight: 500;
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
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 1.5rem;
  padding-bottom: 0.75rem;
  border-bottom: 2px solid var(--cta);
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
