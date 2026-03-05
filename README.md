# iApp Runner

<div align="center">

![Java](https://img.shields.io/badge/Java-25+-orange?style=for-the-badge&logo=openjdk)
![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux-blue?style=for-the-badge)
![License](https://img.shields.io/badge/License-GPL%203.0-blue?style=for-the-badge)

**基于裕语言 V3 语法的 跨平台解释器**

[English](#english) | 简体中文

</div>

---

## 免责声明

**本项目为非官方第三方实现**，与 iApp 官方团队无任何关联。

本项目仅用于学习交流目的，旨在让开发者能够在 PC 端运行和调试裕语言脚本。请勿用于商业用途。

---

## 特性

- 完美支持裕语言 V3 全部语法
- 跨平台运行（Windows / macOS / Linux）
- 完整的词法分析、语法分析、解释执行
- 支持自定义函数与模块化开发
- 支持 Java 互操作
- 支持网络请求与文件操作

---

## 语法概览

### 变量声明

```java
s a = 123           // 局部变量
ss b = "hello"      // 界面变量
sss c = true        // 全局变量
```

### 控制流

```java
// 条件判断
f(a == 1) {
    syso("等于1")
}
else f(a == 2) {
    syso("等于2")
}
else {
    syso("其他")
}

// while 循环
w(a > 0) {
    syso(a)
    s-(1, a)
}

// for 循环
for(1; 10) {
    syso("循环10次")
}

// C风格 for 循环
for(s i=1; i<10; i++) {
    syso(i)
}
```

### 函数定义

```java
fn myFunc(a, b)
    ss(a + b, c)
    syso(c)
end fn

// 调用
myFunc("hello", "world")
```

### 线程

```java
t() {
    syso("新线程执行")
}
```

### 字符串操作

```java
ss("hello" + "world", result)     // 拼接
sr("abc123", "123", "456", result) // 替换
sj("abc123", "abc", "123", result) // 截取
sl("a;b;c", ";", result)           // 分割为数组
slg("hello", len)                   // 获取长度
```

### 数学运算

```java
s+(1, 2, result)    // 加法 -> 3
s-(5, 2, result)    // 减法 -> 3
s*(3, 4, result)    // 乘法 -> 12
s/(10, 2, result)   // 除法 -> 5
s%(10, 3, result)   // 取余 -> 1
sran(1, 100, result) // 随机数 1-100
```

### 数组操作

```java
nsz(5, arr)           // 创建长度为5的数组
sssz(arr, 0, "hello") // 设置数组元素
sgsz(arr, 0, value)   // 获取数组元素
sgszl(arr, len)       // 获取数组长度
```

### 文件操作

```java
fr("%test.txt", content)      // 读取文件
fw("%test.txt", "content")    // 写入文件
fe("%test.txt", exists)       // 判断文件是否存在
fd("%test.txt")               // 删除文件
fl("%dir", list)              // 获取目录列表
```

### 网络请求

```java
t() {
    hs("https://example.com", html)
    syso(html)
}
```

### Java 互操作

```java
// 获取类
cls("java.lang.Math", mathClass)

// 调用静态方法
javax(result, null, mathClass, "abs", "int", -10)

// 创建对象
javanew(list, "java.util.ArrayList")

// 获取/设置字段
javags(value, obj, objClass, "fieldName")
javass(null, obj, objClass, "fieldName", newValue)
```

---

## 快速开始

### 环境要求

- Java 21 或更高版本
- Gradle 8.x（或使用项目自带的 Gradle Wrapper）

### 编译运行

```bash
# 克隆项目
git clone https://gitee.com/TieScript/iapp-runner.git
cd iapp-runner

# 编译
./gradlew build

# 运行脚本
java -jar build/libs/iAppPC.jar your_script.iyu
```

### 示例脚本

创建 `hello.iyu`：

```java
s name = "iApp Runner"
ss("Hello, " + name + "!", greeting)
tw(greeting)

for(s i=1; i<=5; i++) {
    syso("第 " + i + " 次循环")
}
```

运行：

```bash
java -jar build/libs/iAppPC.jar hello.iyu
```

---

## mjava 模块

**mjava** 是裕语言的模块化扩展机制，允许你用标准 Java 语法编写可复用的模块。

### 创建 mjava 模块

在脚本目录下创建 `mjava` 文件夹，然后创建 `.mjava` 文件：

```
your_project/
├── main.iyu
└── mjava/
    └── test.mjava
```

`test.mjava` 示例：

```java
// 标准 Java 语法
public String greet(String name) {
    return "Hello, " + name + "!";
}

public int add(int a, int b) {
    return a + b;
}

public void printMessage(String message) {
    System.out.println("[MJAVA] " + message);
}
```

### 使用 mjava 模块

使用 `call` 函数调用 mjava 模块方法：

```java
// call(结果变量, "mjava", "模块名.方法名", 参数...)

// 调用 greet 方法，返回字符串
call(greetResult, "mjava", "test.greet", "iApp")
syso(greetResult)  // 输出: Hello, iApp!

// 调用 add 方法，返回整数
call(addResult, "mjava", "test.add", 5, 3)
syso(addResult)    // 输出: 8

// 调用无返回值方法
call(printResult, "mjava", "test.printMessage", "测试消息")
```

---

## 不支持的函数

由于本项目运行在 PC 端，部分依赖 Android 系统接口的函数无法实现。

**查看完整的不支持函数列表：[del.txt](del.txt)**

主要包括：
- UI 控件操作（`ug`、`us`、`uigo`、`utw` 等）
- 系统功能（`usms`、`ucall`、`ftz` 等）
- 硬件相关（`swh`、`simei`、`simsi` 等）
- 多媒体（`bfm`、`bfv` 等）
- 图像处理（`sbp`、`tzz`、`tsf` 等）

**除此之外的所有函数均已完美支持！**

---

## 项目结构

```
src/main/java/cn/langlang/iapp/
├── Main.java              # 程序入口
├── lexer/                 # 词法分析器
│   ├── Lexer.java
│   ├── Token.java
│   └── TokenType.java
├── parser/                # 语法分析器
│   └── Parser.java
├── ast/                   # 抽象语法树
│   ├── Expression.java
│   ├── Statement.java
│   └── ...
├── interpreter/           # 解释器
│   └── Interpreter.java
├── runtime/               # 运行时
│   ├── RuntimeContext.java
│   ├── FunctionRegistry.java
│   └── VariableManager.java
├── functions/             # 内置函数
│   ├── string/            # 字符串函数
│   ├── math/              # 数学函数
│   ├── file/              # 文件函数
│   ├── net/               # 网络函数
│   ├── array/             # 数组函数
│   ├── list/              # 列表函数
│   ├── java/              # Java互操作
│   └── ...
└── module/                # 模块加载
    └── MjavaModuleLoader.java
```

---

## 参与贡献

欢迎提交 Issue 和 Pull Request！

---

## 开源协议

本项目基于 [GPL 3.0](LICENSE) 协议开源。

---

## English

A PC interpreter based on Yulang V3 syntax.

### Features

- Full Yulang V3 syntax support
- Cross-platform (Windows / macOS / Linux)
- Complete lexer, parser, and interpreter
- Custom functions and modular development
- Java interoperability
- Network requests and file operations

### Unsupported Functions

Some Android-specific functions are not supported. See [del.txt](del.txt) for details.

### License

GPL 3.0
