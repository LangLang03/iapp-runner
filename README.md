# iApp Runner

<div align="center">

![Java](https://img.shields.io/badge/Java-25+-orange?style=for-the-badge&logo=openjdk)
![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux-blue?style=for-the-badge)

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

## REPL 交互模式

REPL (Read-Eval-Print Loop) 是一个交互式解释器，支持实时输入和执行代码。

### 启动 REPL

```bash
# 无参数启动 REPL
java -jar build/libs/iAppPC.jar
```

### REPL 命令

| 命令 | 说明 |
|------|------|
| `/help` | 显示帮助信息 |
| `/vars` | 显示所有变量 |
| `/funcs` | 显示所有函数 |
| `/reset` | 重置环境 |
| `/load <file>` | 加载并执行脚本文件 |
| `/mjava <dir>` | 加载 mjava 模块目录 |
| `/cd <dir>` | 切换当前目录 |
| `/pwd` | 显示当前目录 |
| `/clear` | 清屏 |
| `/exit` | 退出 REPL |

### 多行输入

REPL 支持多行输入，自动检测代码块完整性：

```java
> fn add(a, b)
..   syso(a + b)
.. end fn
> add(1, 2)
3

> f(1 < 2)
.. {
..   syso("对的")
.. }
对的

> w(i < 5)
.. {
..   syso(i)
..   i = i + 1
.. }
0
1
2
3
4
```

缩进提示符说明：
- `> ` - 等待新输入
- `.. ` - 一层嵌套
- `.... ` - 两层嵌套
- 以此类推...

### REPL 示例

```java
> s a = 114
> f(a < 514)
.. {
..   syso("条件成立")
..   f(a > 100)
..   {
..     syso("嵌套条件")
..   }
.. }
条件成立
嵌套条件

> /vars
变量:
  a = 114

> fn test()
..   syso("Hello from function")
.. end fn
> /funcs
内置函数:
  syso
  tw
  ss
  ...
用户函数:
  test

> /exit
再见!
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

## 架构设计文档

### 整体架构流程图

```mermaid
flowchart TB
    subgraph Input["输入层"]
        SRC["源代码字符串"]
        FILE["脚本文件 .iyu"]
    end

    subgraph LexerModule["词法分析模块 (Lexer)"]
        LEXER["Lexer"]
        TOKEN["Token 流"]
        TOKENTYPE["TokenType 枚举"]
        LEXER --> TOKEN
        TOKENTYPE --> TOKEN
    end

    subgraph ParserModule["语法分析模块 (Parser)"]
        PARSER["Parser"]
        AST["抽象语法树 AST"]
        PARSER --> AST
        TOKEN --> PARSER
    end

    subgraph ASTModule["抽象语法树模块 (AST)"]
        STMT["Statement 语句节点"]
        EXPR["Expression 表达式节点"]
        PROGRAM["Program 程序根节点"]
        STMT --> PROGRAM
        EXPR --> STMT
    end

    subgraph InterpreterModule["解释器模块 (Interpreter)"]
        INTERP["Interpreter"]
        VISITOR["Visitor 访问者模式"]
        INTERP --> VISITOR
        AST --> INTERP
    end

    subgraph RuntimeMethod["运行时模块 (Runtime)"]
        CONTEXT["RuntimeContext<br/>运行时上下文"]
        VARMGR["VariableManager<br/>变量管理器"]
        FUNCREG["FunctionRegistry<br/>函数注册表"]
        CONTEXT --> VARMGR
        CONTEXT --> FUNCREG
    end

    subgraph FunctionsModule["内置函数模块 (Functions)"]
        STRINGFUNC["字符串函数<br/>ss, sr, sj, sl..."]
        MATHFUNC["数学函数<br/>s+, s-, s*, s/..."]
        FILEFUNC["文件函数<br/>fr, fw, fe, fd..."]
        NETFUNC["网络函数<br/>hs, hd, hw..."]
        JAVAFUNC["Java互操作<br/>javanew, javax..."]
        OTHERFUNC["其他函数<br/>syso, tw, call..."]
    end

    subgraph APIModule["API 模块"]
        IAPPSCRIPT["IAppScript<br/>脚本引擎入口"]
        IAPPVAR["IAppVariable<br/>变量封装"]
        IAPPFUNC["IAppFunction<br/>函数封装"]
        IAPPVALUE["IAppValue<br/>值封装"]
    end

    subgraph ModuleLoader["模块加载器"]
        MJAVA["MjavaModuleLoader<br/>mjava模块加载"]
    end

    SRC --> LEXER
    FILE --> LEXER
    VISITOR --> CONTEXT
    FUNCREG --> STRINGFUNC
    FUNCREG --> MATHFUNC
    FUNCREG --> FILEFUNC
    FUNCREG --> NETFUNC
    FUNCREG --> JAVAFUNC
    FUNCREG --> OTHERFUNC
    CONTEXT --> MJAVA
    IAPPSCRIPT --> CONTEXT
    IAPPSCRIPT --> INTERP
    IAPPSCRIPT --> LEXER
    IAPPSCRIPT --> PARSER
```

### 核心执行流程图

```mermaid
flowchart TD
    START([开始]) --> LOAD["加载脚本源码"]
    LOAD --> LEXER["词法分析<br/>Lexer.tokenize()"]
    
    subgraph LexerProcess["词法分析过程"]
        L1["扫描源码字符"]
        L2["识别关键字/标识符"]
        L3["识别运算符"]
        L4["识别字面量"]
        L5["生成Token"]
        L1 --> L2 --> L3 --> L4 --> L5
    end
    
    LEXER --> LexerProcess
    LexerProcess --> TOKENS["Token流"]
    
    TOKENS --> PARSER["语法分析<br/>Parser.parse()"]
    
    subgraph ParserProcess["语法分析过程"]
        P1["解析变量声明"]
        P2["解析控制流语句"]
        P3["解析函数定义"]
        P4["解析表达式"]
        P5["构建AST节点"]
        P1 --> P2 --> P3 --> P4 --> P5
    end
    
    PARSER --> ParserProcess
    ParserProcess --> AST["抽象语法树"]
    
    AST --> INTERP["解释执行<br/>Interpreter.execute()"]
    
    subgraph ExecProcess["执行过程"]
        E1["遍历AST节点"]
        E2["访问语句节点"]
        E3["计算表达式"]
        E4["调用内置函数"]
        E5["变量赋值/读取"]
        E1 --> E2 --> E3 --> E4 --> E5
    end
    
    INTERP --> ExecProcess
    ExecProcess --> RESULT["执行结果"]
    RESULT --> END([结束])
```

### 词法分析详细流程图

```mermaid
flowchart TD
    START([开始词法分析]) --> INIT["初始化 Lexer<br/>position=0, line=1, column=1"]
    INIT --> SKIPWS["跳过空白字符<br/>skipWhitespace()"]
    
    SKIPWS --> CHECKEND{"是否到达<br/>源码末尾?"}
    CHECKEND -->|是| EOF["添加 EOF Token"]
    CHECKEND -->|否| SCAN["扫描下一个Token<br/>scanToken()"]
    
    EOF --> RETURN["返回 Token 列表"]
    RETURN --> END([结束])
    
    SCAN --> SWITCH{"字符类型判断"}
    
    SWITCH -->|括号类| BRACKET["生成括号Token<br/>( ) { } [ ]"]
    SWITCH -->|运算符类| OPERATOR["生成运算符Token<br/>+ - * / = == 等"]
    SWITCH -->|数字| NUMBER["扫描数字<br/>scanNumber()"]
    SWITCH -->|字母| IDENT["扫描标识符<br/>scanIdentifier()"]
    SWITCH -->|引号| STRING["扫描字符串<br/>scanString()"]
    SWITCH -->|换行| NEWLINE["生成换行Token"]
    SWITCH -->|注释| COMMENT["跳过注释<br/>skipLineComment()<br/>skipBlockComment()"]
    
    BRACKET --> ADDTOKEN
    OPERATOR --> ADDTOKEN
    NUMBER --> ADDTOKEN
    IDENT --> KEYWORD{"是关键字?"}
    STRING --> ADDTOKEN
    NEWLINE --> ADDTOKEN
    COMMENT --> SKIPWS
    
    KEYWORD -->|是| KEYTOK["生成关键字Token"]
    KEYWORD -->|否| IDTOK["生成标识符Token"]
    KEYTOK --> ADDTOKEN
    IDTOK --> ADDTOKEN
    
    ADDTOKEN["添加Token到列表"] --> SKIPWS
```

### 语法分析详细流程图

```mermaid
flowchart TD
    START([开始语法分析]) --> INIT["初始化 Parser<br/>current=0"]
    INIT --> LOOP{"是否到达<br/>Token末尾?"}
    
    LOOP -->|是| RETURN["返回 Program"]
    LOOP -->|否| PARSE["解析语句<br/>parseStatement()"]
    
    RETURN --> END([结束])
    
    PARSE --> SWITCH{"Token类型判断"}
    
    SWITCH -->|s/ss/sss| VARDECL["解析变量声明<br/>parseVariableDeclaration()"]
    SWITCH -->|f| IFSTMT["解析if语句<br/>parseIfStatement()"]
    SWITCH -->|w| WHILESTMT["解析while语句<br/>parseWhileStatement()"]
    SWITCH -->|for| FORSTMT["解析for语句<br/>parseForStatement()"]
    SWITCH -->|break| BREAKSTMT["解析break语句<br/>parseBreakStatement()"]
    SWITCH -->|endcode| ENDCODESTMT["解析endcode语句<br/>parseEndCodeStatement()"]
    SWITCH -->|fn| FUNCDEF["解析函数定义<br/>parseFunctionDefinition()"]
    SWITCH -->|t| THREADSTMT["解析线程语句<br/>parseThreadStatement()"]
    SWITCH -->|标识符| IDENTSTMT["解析标识符语句<br/>parseIdentifierStatement()"]
    SWITCH -->|左花括号| BLOCKSTMT["解析块语句<br/>parseBlockStatement()"]
    
    VARDECL --> ADDSTMT
    IFSTMT --> ADDSTMT
    WHILESTMT --> ADDSTMT
    FORSTMT --> ADDSTMT
    BREAKSTMT --> ADDSTMT
    ENDCODESTMT --> ADDSTMT
    FUNCDEF --> ADDSTMT
    THREADSTMT --> ADDSTMT
    IDENTSTMT --> ADDSTMT
    BLOCKSTMT --> ADDSTMT
    
    ADDSTMT["添加语句到Program"] --> LOOP
```

### 表达式解析优先级流程图

```mermaid
flowchart TD
    START(["开始解析表达式"]) --> OR["解析OR表达式<br/>parseOrExpression()"]
    
    OR --> AND["解析AND表达式<br/>parseAndExpression()"]
    AND --> ORLOOP{"遇到 || ?"}
    ORLOOP -->|是| AND
    ORLOOP -->|否| EQ["解析相等表达式<br/>parseEqualityExpression()"]
    
    EQ --> CMP["解析比较表达式<br/>parseComparisonExpression()"]
    CMP --> EQLOOP{"遇到 == 或 != ?"}
    EQLOOP -->|是| CMP
    EQLOOP -->|否| STRMATCH["解析字符串匹配表达式<br/>parseStringMatchExpression()"]
    
    STRMATCH --> ADD["解析加减表达式<br/>parseAdditiveExpression()"]
    ADD --> STRLOOP{"遇到 ?* 或 *? 或 ???"}
    STRLOOP -->|是| ADD
    STRLOOP -->|否| MUL["解析乘除表达式<br/>parseMultiplicativeExpression()"]
    
    MUL --> UNARY["解析一元表达式<br/>parseUnaryExpression()"]
    UNARY --> MULLOOP{"遇到 * 或 / 或 % ?"}
    MULLOOP -->|是| UNARY
    MULLOOP -->|否| PRIMARY["解析主表达式<br/>parsePrimaryExpression()"]
    
    PRIMARY --> PRIMARYSWITCH{"Token类型"}
    PRIMARYSWITCH -->|NUMBER| NUMLIT["数字字面量"]
    PRIMARYSWITCH -->|STRING| STRLIT["字符串字面量"]
    PRIMARYSWITCH -->|true/false| BOOLLIT["布尔字面量"]
    PRIMARYSWITCH -->|null| NULLLIT["null字面量"]
    PRIMARYSWITCH -->|标识符| VAREXPR["变量表达式"]
    PRIMARYSWITCH -->|左括号| PARENEXPR["括号表达式"]
    
    NUMLIT --> RETURN
    STRLIT --> RETURN
    BOOLLIT --> RETURN
    NULLLIT --> RETURN
    VAREXPR --> RETURN
    PARENEXPR --> RETURN
    
    RETURN(["返回表达式节点"])
```

### 解释器执行流程图

```mermaid
flowchart TD
    START([开始执行]) --> EXEC["Interpreter.execute()"]
    EXEC --> LOOP{"遍历Program<br/>中的语句"}
    
    LOOP -->|有下一条| STMT["执行语句<br/>executeStatement()"]
    LOOP -->|结束| END([结束])
    
    STMT --> VISITOR["使用Visitor模式<br/>访问语句节点"]
    
    VISITOR --> STMTSWITCH{"语句类型"}
    
    STMTSWITCH -->|变量声明| VARDECL["visitVariableDeclaration()<br/>设置变量值"]
    STMTSWITCH -->|赋值语句| ASSIGN["visitAssignment()<br/>更新变量值"]
    STMTSWITCH -->|if语句| IF["visitIf()<br/>条件判断执行"]
    STMTSWITCH -->|while语句| WHILE["visitWhile()<br/>循环执行"]
    STMTSWITCH -->|for语句| FOR["visitFor()<br/>循环执行"]
    STMTSWITCH -->|函数调用| FUNCCALL["visitFunctionCall()<br/>调用函数"]
    STMTSWITCH -->|break| BREAK["visitBreak()<br/>设置break标志"]
    STMTSWITCH -->|endcode| ENDCODE["visitEndCode()<br/>设置结束标志"]
    STMTSWITCH -->|函数定义| FUNCDEF["visitFunctionDefinition()<br/>注册用户函数"]
    STMTSWITCH -->|线程语句| THREAD["visitThread()<br/>创建新线程执行"]
    STMTSWITCH -->|块语句| BLOCK["visitBlock()<br/>执行块内语句"]
    
    VARDECL --> LOOP
    ASSIGN --> LOOP
    IF --> LOOP
    WHILE --> LOOP
    FOR --> LOOP
    FUNCCALL --> LOOP
    BREAK --> LOOP
    ENDCODE --> LOOP
    FUNCDEF --> LOOP
    THREAD --> LOOP
    BLOCK --> LOOP
```

### 函数调用流程图

```mermaid
flowchart TD
    START([函数调用]) --> LOOKUP["查找函数"]
    
    LOOKUP --> CHECKUSER{"是用户<br/>自定义函数?"}
    
    CHECKUSER -->|是| USERFUNC["获取用户函数定义<br/>FunctionDefinitionStatement"]
    CHECKUSER -->|否| CHECKREG{"是注册的<br/>内置函数?"}
    
    CHECKREG -->|是| BUILTINFUNC["获取内置函数<br/>IFunction"]
    CHECKREG -->|否| CHECKMJAVA{"是mjava<br/>模块方法?"}
    
    CHECKMJAVA -->|是| MJAVAMETHOD["调用mjava方法<br/>executeMjavaMethod()"]
    CHECKMJAVA -->|否| ERROR["抛出错误<br/>函数未找到"]
    
    USERFUNC --> PREPAREARGS["准备参数"]
    BUILTINFUNC --> PREPAREARGS
    MJAVAMETHOD --> PREPAREARGS
    
    PREPAREARGS --> EVALARGS["计算参数表达式<br/>evaluateExpression()"]
    EVALARGS --> CALL["调用函数<br/>function.call()"]
    
    CALL --> CHECKOUTPUT{"有输出<br/>变量?"}
    CHECKOUTPUT -->|是| SETOUTPUT["设置输出变量<br/>setVariable()"]
    CHECKOUTPUT -->|否| RETURN["返回结果"]
    SETOUTPUT --> RETURN
    
    RETURN --> END([结束])
    ERROR --> END
```

### 变量管理流程图

```mermaid
flowchart TD
    subgraph Scope["变量作用域"]
        LOCAL["局部变量 (s)<br/>scopeStack 栈顶"]
        INTERFACE["界面变量 (ss)<br/>interfaceVariables"]
        GLOBAL["全局变量 (sss)<br/>globalVariables"]
    end
    
    SETVAR["设置变量"] --> CHECKSCOPE{"作用域类型"}
    CHECKSCOPE -->|s| SETLOCAL["存入 scopeStack.peek()"]
    CHECKSCOPE -->|ss| SETINTERFACE["存入 interfaceVariables"]
    CHECKSCOPE -->|sss| SETGLOBAL["存入 globalVariables"]
    
    GETVAR["获取变量"] --> CHECKPREFIX{"变量名前缀"}
    CHECKPREFIX -->|sss.| GETGLOBAL["从 globalVariables 获取"]
    CHECKPREFIX -->|ss.| GETINTERFACE["从 interfaceVariables 获取"]
    CHECKPREFIX -->|无前缀| SEARCHSCOPE["从作用域栈搜索"]
    
    SEARCHSCOPE --> LOOPSCOPE{"遍历作用域栈<br/>从顶到底"}
    LOOPSCOPE -->|找到| RETURNVAR["返回变量值"]
    LOOPSCOPE -->|未找到| CHECKINTERFACE["检查 interfaceVariables"]
    CHECKINTERFACE -->|找到| RETURNVAR
    CHECKINTERFACE -->|未找到| CHECKGLOBAL2["检查 globalVariables"]
    CHECKGLOBAL2 -->|找到| RETURNVAR
    CHECKGLOBAL2 -->|未找到| RETURNNULL["返回 null"]
    
    PUSHSCOPE["进入新作用域<br/>pushScope()"] --> NEWSCOPE["压入新的 HashMap"]
    POPSCOPE["退出作用域<br/>popScope()"] --> DELSCOPE["弹出栈顶 HashMap"]
```

---

## 类图

### 核心类图

```mermaid
classDiagram
    class Main {
        +main(args) void
        -printUsage() void
    }
    
    class IAppScript {
        -RuntimeContext context
        -Interpreter interpreter
        -Lexer lexer
        -Program loadedProgram
        -String loadedSource
        +create() IAppScript
        +create(RuntimeContext) IAppScript
        +loadString(String) IAppScript
        +loadFile(String) IAppScript
        +loadFile(File) IAppScript
        +loadMjava(String) IAppScript
        +eval() Object
        +eval(String) Object
        +evalFile(String) Object
        +getFunction(String) IAppFunction
        +registerFunction(IAppFunction) IAppScript
        +registerFunction(String, IAppFunctionHandler) IAppScript
        +getVariable(String) IAppVariable
        +setVariable(String, Object) IAppScript
        +setVariable(String, Object, VariableScope) IAppScript
        +getContext() RuntimeContext
        +reset() IAppScript
    }
    
    Main --> IAppScript : 使用
    IAppScript --> RuntimeContext : 包含
    IAppScript --> Interpreter : 使用
    IAppScript --> Lexer : 使用
```

### 词法分析器类图

```mermaid
classDiagram
    class ILexer {
        <<interface>>
        +tokenize(String) List~Token~
    }
    
    class Lexer {
        -String source
        -int position
        -int line
        -int column
        -int length
        -Map~String,TokenType~ KEYWORDS
        +Lexer(String)
        +tokenize(String) List~Token~
        +tokenizeInternal() List~Token~
        -scanToken() Token
        -scanString(int, int) Token
        -scanNumber(int, int) Token
        -scanIdentifier(int, int) Token
        -skipWhitespace() void
        -skipLineComment() void
        -skipBlockComment() void
        -isAtEnd() boolean
        -advance() char
        -peek() char
        -peekNext() char
        -match(char) boolean
        -isDigit(char) boolean
        -isAlpha(char) boolean
        -isAlphaNumeric(char) boolean
    }
    
    class Token {
        -TokenType type
        -String value
        -int line
        -int column
        +Token(TokenType, String, int, int)
        +getType() TokenType
        +getValue() String
        +getLine() int
        +getColumn() int
        +toString() String
    }
    
    class TokenType {
        <<enumeration>>
        KEYWORD_S
        KEYWORD_SS
        KEYWORD_SSS
        KEYWORD_IF
        KEYWORD_ELSE
        KEYWORD_WHILE
        KEYWORD_FOR
        KEYWORD_BREAK
        KEYWORD_ENDCODE
        KEYWORD_FN
        KEYWORD_END
        KEYWORD_TRUE
        KEYWORD_FALSE
        KEYWORD_NULL
        KEYWORD_T
        IDENTIFIER
        NUMBER
        STRING
        PLUS
        MINUS
        STAR
        SLASH
        PERCENT
        PLUS_PLUS
        MINUS_MINUS
        EQUALS
        PLUS_EQUALS
        MINUS_EQUALS
        STAR_EQUALS
        SLASH_EQUALS
        EQ
        NE
        LT
        GT
        LE
        GE
        AND
        OR
        NOT
        STARTS_WITH
        ENDS_WITH
        CONTAINS
        LPAREN
        RPAREN
        LBRACE
        RBRACE
        LBRACKET
        RBRACKET
        COMMA
        DOT
        SEMICOLON
        COLON
        NEWLINE
        EOF
        UNKNOWN
    }
    
    class LexerException {
        -int line
        -int column
        +LexerException(String, int, int)
        +getLine() int
        +getColumn() int
    }
    
    ILexer <|.. Lexer : 实现
    Lexer --> Token : 生成
    Token --> TokenType : 包含
    Lexer ..> LexerException : 抛出
```

### 语法分析器类图

```mermaid
classDiagram
    class IParser {
        <<interface>>
        +parse(List~Token~) Program
    }
    
    class Parser {
        -int MAX_ITERATIONS
        -int iterationCount
        -List~Token~ tokens
        -int current
        -Set~String~ definedFunctions
        -FunctionRegistry functionRegistry
        +Parser()
        +Parser(List~Token~)
        +setFunctionRegistry(FunctionRegistry) void
        +parse(List~Token~) Program
        +parse() Program
        -parseStatement() Statement
        -parseFunctionCallStatement(Token) Statement
        -parseVariableDeclaration() Statement
        -parseIfStatement() Statement
        -parseWhileStatement() Statement
        -parseForStatement() Statement
        -parseForUpdateStatement() Statement
        -parseBreakStatement() Statement
        -parseEndCodeStatement() Statement
        -parseFunctionDefinition() Statement
        -parseFunctionBody() List~Statement~
        -parseThreadStatement() Statement
        -parseBlockStatement() Statement
        -parseBlock() List~Statement~
        -parseIdentifierStatement() Statement
        -parseMemberAccessOrCall(Token) Statement
        -parseMethodCall(String, String, int) Statement
        -parseFunctionCallWithIdentifier(String, int) Statement
        -parseExpression() Expression
        -parseOrExpression() Expression
        -parseAndExpression() Expression
        -parseEqualityExpression() Expression
        -parseComparisonExpression() Expression
        -parseStringMatchExpression() Expression
        -parseAdditiveExpression() Expression
        -parseMultiplicativeExpression() Expression
        -parseUnaryExpression() Expression
        -parsePrimaryExpression() Expression
        -parseFunctionCallExpression(String, int) Expression
        -parseMemberAccessExpression(String, int) Expression
        -parseChainedMemberAccess(Expression, int) Expression
        -parseMethodCallExpression(String, String, int) Expression
        -skipNewlines() void
        -isAtEnd() boolean
        -peek() Token
        -peekNext() Token
        -previous() Token
        -advance() Token
        -check(TokenType) boolean
        -match(TokenType) boolean
        -consume(TokenType, String) Token
        -isOutputParameter(String, int) boolean
        -isMathFunctionOperator(TokenType) boolean
        -parseMathFunctionCallStatement(Token, Token) Statement
    }
    
    class ParserException {
        -int line
        -int column
        +ParserException(String, int, int)
        +getLine() int
        +getColumn() int
    }
    
    IParser <|.. Parser : 实现
    Parser --> Program : 生成
    Parser --> Statement : 生成
    Parser --> Expression : 生成
    Parser ..> ParserException : 抛出
```

### AST语句节点类图

```mermaid
classDiagram
    class Statement {
        <<abstract>>
        -int line
        #Statement(int)
        +getLine() int
        +accept~T~(StatementVisitor~T~) T
    }
    
    class StatementVisitor {
        <<interface>>
        +visitVariableDeclaration(VariableDeclarationStatement) T
        +visitAssignment(AssignmentStatement) T
        +visitIf(IfStatement) T
        +visitWhile(WhileStatement) T
        +visitFor(ForStatement) T
        +visitFunctionCall(FunctionCallStatement) T
        +visitBreak(BreakStatement) T
        +visitEndCode(EndCodeStatement) T
        +visitFunctionDefinition(FunctionDefinitionStatement) T
        +visitThread(ThreadStatement) T
        +visitBlock(BlockStatement) T
    }
    
    class Program {
        -List~Statement~ statements
        +Program()
        +addStatement(Statement) void
        +getStatements() List~Statement~
    }
    
    class VariableDeclarationStatement {
        -TokenType scope
        -String variableName
        -Expression initialValue
        +VariableDeclarationStatement(int, TokenType, String, Expression)
        +getScope() TokenType
        +getVariableName() String
        +getInitialValue() Expression
    }
    
    class AssignmentStatement {
        -String variableName
        -Expression value
        -TokenType scope
        -Expression index
        +AssignmentStatement(int, String, Expression, TokenType)
        +AssignmentStatement(int, String, Expression, Expression, TokenType)
        +getVariableName() String
        +getValue() Expression
        +getScope() TokenType
        +getIndex() Expression
        +isArrayAssignment() boolean
    }
    
    class IfStatement {
        -Expression condition
        -List~Statement~ thenStatements
        -List~ElseIfClause~ elseIfClauses
        -List~Statement~ elseStatements
        +IfStatement(int, Expression, List~Statement~)
        +getCondition() Expression
        +getThenStatements() List~Statement~
        +getElseIfClauses() List~ElseIfClause~
        +getElseStatements() List~Statement~
        +addElseIfClause(ElseIfClause) void
        +setElseStatements(List~Statement~) void
    }
    
    class ElseIfClause {
        <<record>>
        -Expression condition
        -List~Statement~ statements
    }
    
    class WhileStatement {
        -Expression condition
        -List~Statement~ body
        +WhileStatement(int, Expression, List~Statement~)
        +getCondition() Expression
        +getBody() List~Statement~
    }
    
    class ForStatement {
        -Expression start
        -Expression end
        -Expression step
        -String variableName
        -List~Statement~ body
        -ForType forType
        -Statement initStatement
        -Expression condition
        -Statement updateStatement
        +ForStatement(int, Expression, Expression, Expression, List~Statement~)
        +ForStatement(int, String, Expression, List~Statement~)
        +ForStatement(int, Statement, Expression, Statement, List~Statement~)
        +getStart() Expression
        +getEnd() Expression
        +getStep() Expression
        +getVariableName() String
        +getBody() List~Statement~
        +getForType() ForType
        +getInitStatement() Statement
        +getCondition() Expression
        +getUpdateStatement() Statement
    }
    
    class ForType {
        <<enumeration>>
        RANGE
        ARRAY_ITERATION
        C_STYLE
    }
    
    class FunctionCallStatement {
        -String functionName
        -List~Expression~ arguments
        -List~String~ outputVariables
        -TokenType resultScope
        +FunctionCallStatement(int, String, List~Expression~, List~String~, TokenType)
        +getFunctionName() String
        +getArguments() List~Expression~
        +getOutputVariables() List~String~
        +getResultScope() TokenType
        +hasOutputVariables() boolean
    }
    
    class FunctionDefinitionStatement {
        -String moduleName
        -String functionName
        -List~String~ parameters
        -List~Statement~ body
        +FunctionDefinitionStatement(int, String, String, List~String~, List~Statement~)
        +getModuleName() String
        +getFunctionName() String
        +getParameters() List~String~
        +getBody() List~Statement~
        +getFullName() String
    }
    
    class BreakStatement {
        +BreakStatement(int)
    }
    
    class EndCodeStatement {
        +EndCodeStatement(int)
    }
    
    class ThreadStatement {
        -List~Statement~ body
        +ThreadStatement(int, List~Statement~)
        +getBody() List~Statement~
    }
    
    class BlockStatement {
        -List~Statement~ statements
        +BlockStatement(int, List~Statement~)
        +getStatements() List~Statement~
    }
    
    Statement <|-- VariableDeclarationStatement
    Statement <|-- AssignmentStatement
    Statement <|-- IfStatement
    Statement <|-- WhileStatement
    Statement <|-- ForStatement
    Statement <|-- FunctionCallStatement
    Statement <|-- FunctionDefinitionStatement
    Statement <|-- BreakStatement
    Statement <|-- EndCodeStatement
    Statement <|-- ThreadStatement
    Statement <|-- BlockStatement
    
    Statement --> StatementVisitor : 使用
    IfStatement --> ElseIfClause : 包含
    ForStatement --> ForType : 使用
    Program --> Statement : 包含
```

### AST表达式节点类图

```mermaid
classDiagram
    class Expression {
        <<abstract>>
        -int line
        #Expression(int)
        +getLine() int
        +accept~T~(ExpressionVisitor~T~) T
    }
    
    class ExpressionVisitor {
        <<interface>>
        +visitNumberLiteral(NumberLiteralExpression) T
        +visitStringLiteral(StringLiteralExpression) T
        +visitBooleanLiteral(BooleanLiteralExpression) T
        +visitNullLiteral(NullLiteralExpression) T
        +visitVariable(VariableExpression) T
        +visitBinary(BinaryExpression) T
        +visitUnary(UnaryExpression) T
        +visitFunctionCall(FunctionCallExpression) T
        +visitArrayAccess(ArrayAccessExpression) T
        +visitMemberAccess(MemberAccessExpression) T
    }
    
    class NumberLiteralExpression {
        -Number value
        +NumberLiteralExpression(int, Number)
        +getValue() Number
    }
    
    class StringLiteralExpression {
        -String value
        +StringLiteralExpression(int, String)
        +getValue() String
    }
    
    class BooleanLiteralExpression {
        -boolean value
        +BooleanLiteralExpression(int, boolean)
        +getValue() boolean
    }
    
    class NullLiteralExpression {
        +NullLiteralExpression(int)
    }
    
    class VariableExpression {
        -String name
        -TokenType scope
        +VariableExpression(int, String, TokenType)
        +getName() String
        +getScope() TokenType
    }
    
    class BinaryExpression {
        -Expression left
        -TokenType operator
        -Expression right
        +BinaryExpression(int, Expression, TokenType, Expression)
        +getLeft() Expression
        +getOperator() TokenType
        +getRight() Expression
    }
    
    class UnaryExpression {
        -TokenType operator
        -Expression operand
        -boolean prefix
        +UnaryExpression(int, TokenType, Expression, boolean)
        +getOperator() TokenType
        +getOperand() Expression
        +isPrefix() boolean
    }
    
    class FunctionCallExpression {
        -String functionName
        -List~Expression~ arguments
        +FunctionCallExpression(int, String, List~Expression~)
        +getFunctionName() String
        +getArguments() List~Expression~
    }
    
    class ArrayAccessExpression {
        -Expression array
        -Expression index
        +ArrayAccessExpression(int, Expression, Expression)
        +getArray() Expression
        +getIndex() Expression
    }
    
    class MemberAccessExpression {
        -Expression object
        -String memberName
        +MemberAccessExpression(int, Expression, String)
        +getObject() Expression
        +getMemberName() String
    }
    
    Expression <|-- NumberLiteralExpression
    Expression <|-- StringLiteralExpression
    Expression <|-- BooleanLiteralExpression
    Expression <|-- NullLiteralExpression
    Expression <|-- VariableExpression
    Expression <|-- BinaryExpression
    Expression <|-- UnaryExpression
    Expression <|-- FunctionCallExpression
    Expression <|-- ArrayAccessExpression
    Expression <|-- MemberAccessExpression
    
    Expression --> ExpressionVisitor : 使用
    BinaryExpression --> Expression : 包含
    UnaryExpression --> Expression : 包含
    FunctionCallExpression --> Expression : 包含
    ArrayAccessExpression --> Expression : 包含
    MemberAccessExpression --> Expression : 包含
```

### 解释器类图

```mermaid
classDiagram
    class IInterpreter {
        <<interface>>
        +execute(Program, RuntimeContext) Object
        +executeStatement(Statement, RuntimeContext) Object
        +evaluateExpression(Expression, RuntimeContext) Object
    }
    
    class Interpreter {
        -RuntimeContext context
        -Logger logger
        +Interpreter(RuntimeContext)
        +execute(Program, RuntimeContext) Object
        +executeStatement(Statement, RuntimeContext) Object
        +evaluateExpression(Expression, RuntimeContext) Object
        -isTruthy(Object) boolean
        -isEqual(Object, Object) boolean
        -compare(Object, Object) int
        -toLong(Object) long
        -toInt(Object) int
        -executeUserFunction(FunctionDefinitionStatement, List~Object~, RuntimeContext) Object
    }
    
    class InterpreterException {
        -int line
        +InterpreterException(String)
        +InterpreterException(String, int)
        +getLine() int
    }
    
    IInterpreter <|.. Interpreter : 实现
    Interpreter --> RuntimeContext : 使用
    Interpreter --> Program : 执行
    Interpreter --> Statement : 执行
    Interpreter --> Expression : 计算
    Interpreter ..> InterpreterException : 抛出
```

### 运行时类图

```mermaid
classDiagram
    class RuntimeContext {
        -VariableManager variableManager
        -FunctionRegistry functionRegistry
        -Interpreter beanShellInterpreter
        -MjavaModuleLoader mjavaModuleLoader
        -Stack~BreakContext~ breakContextStack
        -Map~String,Object~ javaObjects
        -Map~String,FunctionDefinitionStatement~ userFunctions
        -String currentDirectory
        -boolean endCodeRequested
        -Thread currentThread
        +RuntimeContext()
        -registerBuiltinFunctions() void
        -initializeBeanShell() void
        +getVariableManager() VariableManager
        +getFunctionRegistry() FunctionRegistry
        +getBeanShellInterpreter() Interpreter
        +getMjavaModuleLoader() MjavaModuleLoader
        +setVariable(String, Object) void
        +setVariable(String, Object, TokenType) void
        +getVariable(String) Object
        +hasVariable(String) boolean
        +pushBreakContext(BreakContext) void
        +popBreakContext() BreakContext
        +getCurrentBreakContext() BreakContext
        +registerJavaObject(String, Object) void
        +getJavaObject(String) Object
        +removeJavaObject(String) void
        +getCurrentDirectory() String
        +setCurrentDirectory(String) void
        +resolvePath(String) String
        +requestEndCode() void
        +isEndCodeRequested() boolean
        +resetEndCodeRequest() void
        +loadMjavaModules(String) void
        +executeMjavaMethod(String, String, Object[]) Object
        +registerUserFunction(String, FunctionDefinitionStatement) void
        +getUserFunction(String) FunctionDefinitionStatement
        +hasUserFunction(String) boolean
        +getUserFunctions() Map
    }
    
    class BreakContext {
        -String type
        -boolean shouldBreak
        +BreakContext(String)
        +getType() String
        +shouldBreak() boolean
        +setShouldBreak(boolean) void
    }
    
    class VariableManager {
        -Stack~Map~String,Object~~ scopeStack
        -Map~String,Object~ interfaceVariables
        -Map~String,Object~ globalVariables
        +VariableManager()
        +setVariable(String, Object) void
        +getVariable(String) Object
        +setVariable(String, Object, TokenType) void
        +hasVariable(String) boolean
        +pushScope() void
        +popScope() void
        +clearLocalVariables() void
        +clearInterfaceVariables() void
        +clearGlobalVariables() void
        +getLocalVariables() Map
        +getInterfaceVariables() Map
        +getGlobalVariables() Map
    }
    
    class FunctionRegistry {
        -Map~String,IFunction~ functions
        +FunctionRegistry()
        +registerFunction(IFunction) void
        +getFunction(String) IFunction
        +hasFunction(String) boolean
        +getFunctionNames() Set~String~
        +unregisterFunction(String) void
        +clear() void
    }
    
    class IFunction {
        <<interface>>
        +getName() String
        +getMinParameters() int
        +getMaxParameters() int
        +call(RuntimeContext, List~Object~) Object
        +isSupported() boolean
        +getUnsupportedReason() String
        +getParamTypes() List~ParamType~
        +getParamTypeLists() List~List~ParamType~~
    }
    
    class AbstractFunction {
        <<abstract>>
        +isSupported() boolean
        +getUnsupportedReason() String
        #types(ParamType...) List~ParamType~
        +getParamTypeLists() List~List~ParamType~~
        #typeLists(List~ParamType~...) List~List~ParamType~~
    }
    
    class ParamType {
        <<enumeration>>
        STRING
        INT
        LONG
        DOUBLE
        BOOLEAN
        OBJECT
        ARRAY
        OUTPUT
    }
    
    class FunctionException {
        +FunctionException(String)
        +FunctionException(String, Throwable)
    }
    
    RuntimeContext --> VariableManager : 包含
    RuntimeContext --> FunctionRegistry : 包含
    RuntimeContext --> BreakContext : 包含
    RuntimeContext --> MjavaModuleLoader : 包含
    FunctionRegistry --> IFunction : 管理
    IFunction <|.. AbstractFunction : 实现
    AbstractFunction --> ParamType : 使用
    IFunction ..> FunctionException : 抛出
```

### 内置函数类图

```mermaid
classDiagram
    class AbstractFunction {
        <<abstract>>
        +getName() String
        +getMinParameters() int
        +getMaxParameters() int
        +call(RuntimeContext, List~Object~) Object
        +isSupported() boolean
        +getUnsupportedReason() String
        +getParamTypes() List~ParamType~
        +getParamTypeLists() List~List~ParamType~~
        #types(ParamType...) List~ParamType~
        #typeLists(List~ParamType~...) List~List~ParamType~~
    }
    
    class SysoFunction {
        +getName() String
        +getMinParameters() int
        +getMaxParameters() int
        +call(RuntimeContext, List~Object~) Object
        +getParamTypes() List~ParamType~
    }
    
    class TwFunction {
        +getName() String
        +getMinParameters() int
        +getMaxParameters() int
        +call(RuntimeContext, List~Object~) Object
        +getParamTypes() List~ParamType~
    }
    
    class SsFunction {
        +getName() String
        +getMinParameters() int
        +getMaxParameters() int
        +call(RuntimeContext, List~Object~) Object
        +getParamTypes() List~ParamType~
    }
    
    class SAddFunction {
        +getName() String
        +getMinParameters() int
        +getMaxParameters() int
        +call(RuntimeContext, List~Object~) Object
        +getParamTypes() List~ParamType~
    }
    
    class FrFunction {
        +getName() String
        +getMinParameters() int
        +getMaxParameters() int
        +call(RuntimeContext, List~Object~) Object
        +getParamTypes() List~ParamType~
    }
    
    class HsFunction {
        +getName() String
        +getMinParameters() int
        +getMaxParameters() int
        +call(RuntimeContext, List~Object~) Object
        +getParamTypes() List~ParamType~
        +getParamTypeLists() List~List~ParamType~~
    }
    
    class JavanewFunction {
        +getName() String
        +getMinParameters() int
        +getMaxParameters() int
        +call(RuntimeContext, List~Object~) Object
        +getParamTypes() List~ParamType~
        -parseType(String) Class~?~
        -findConstructor(Class~?~, List~Class~?~~) Constructor~?~
    }
    
    class CallFunction {
        +getName() String
        +getMinParameters() int
        +getMaxParameters() int
        +call(RuntimeContext, List~Object~) Object
        +getParamTypes() List~ParamType~
    }
    
    AbstractFunction <|-- SysoFunction
    AbstractFunction <|-- TwFunction
    AbstractFunction <|-- SsFunction
    AbstractFunction <|-- SAddFunction
    AbstractFunction <|-- FrFunction
    AbstractFunction <|-- HsFunction
    AbstractFunction <|-- JavanewFunction
    AbstractFunction <|-- CallFunction
    
    note for SysoFunction "输出函数\nsyso(obj)"
    note for SsFunction "字符串函数\nss(obj, output)"
    note for SAddFunction "数学函数\ns+(a, b, output)"
    note for FrFunction "文件函数\nfr(path, output)"
    note for HsFunction "网络函数\nhs(url, ...)"
    note for JavanewFunction "Java互操作\njavanew(class, ...)"
    note for CallFunction "模块调用\ncall(output, lang, method, ...)"
```

### API类图

```mermaid
classDiagram
    class IAppScript {
        -RuntimeContext context
        -Interpreter interpreter
        -Lexer lexer
        -Program loadedProgram
        -String loadedSource
        +create() IAppScript
        +create(RuntimeContext) IAppScript
        +loadString(String) IAppScript
        +loadFile(String) IAppScript
        +loadFile(File) IAppScript
        +loadMjava(String) IAppScript
        +loadMjavaFile(String) IAppScript
        +eval() Object
        +eval(String) Object
        +evalFile(String) Object
        +getFunction(String) IAppFunction
        +hasFunction(String) boolean
        +registerFunction(IAppFunction) IAppScript
        +registerFunction(String, IAppFunctionHandler) IAppScript
        +getFunctionNames() Set~String~
        +getVariable(String) IAppVariable
        +setVariable(String, Object) IAppScript
        +setVariable(String, Object, VariableScope) IAppScript
        +hasVariable(String) boolean
        +removeVariable(String) IAppScript
        +getVariableNames() Set~String~
        +getUserFunction(String) IAppFunction
        +hasUserFunction(String) boolean
        +getUserFunctionNames() Set~String~
        +getContext() RuntimeContext
        +setCurrentDirectory(String) IAppScript
        +getCurrentDirectory() String
        +valueOf(Object) IAppValue
        +nil() IAppValue
        +reset() IAppScript
    }
    
    class IAppVariable {
        -String name
        -Object value
        -VariableScope scope
        +IAppVariable(String, Object)
        +IAppVariable(String, Object, VariableScope)
        +getName() String
        +getValue() Object
        +getScope() VariableScope
        +isNil() boolean
        +isString() boolean
        +isNumber() boolean
        +isBoolean() boolean
        +isArray() boolean
        +isList() boolean
        +getType() Class~?~
        +asString() String
        +asString(String) String
        +asInt() int
        +asInt(int) int
        +asLong() long
        +asLong(long) long
        +asDouble() double
        +asDouble(double) double
        +asBoolean() boolean
        +asBoolean(boolean) boolean
        +asArray() Object[]
        +asList() List~?~
        +toValue() IAppValue
    }
    
    class VariableScope {
        <<enumeration>>
        LOCAL s
        INTERFACE ss
        GLOBAL sss
        -String keyword
        +getKeyword() String
        +fromKeyword(String) VariableScope
    }
    
    class IAppValue {
        -Object value
        -ValueType type
        -IAppValue NIL$
        +getType() ValueType
        +toObject() Object
        +isNil() boolean
        +isString() boolean
        +isNumber() boolean
        +isBoolean() boolean
        +isArray() boolean
        +isList() boolean
        +isFunction() boolean
        +asString() String
        +asString(String) String
        +asInt() int
        +asInt(int) int
        +asLong() long
        +asLong(long) long
        +asDouble() double
        +asDouble(double) double
        +asBoolean() boolean
        +asBoolean(boolean) boolean
        +asArray() Object[]
        +asList() List~?~
        +asFunction() IAppFunction
        +valueOf(Object) IAppValue
        +nil() IAppValue
        +valueOf(String) IAppValue
        +valueOf(int) IAppValue
        +valueOf(long) IAppValue
        +valueOf(double) IAppValue
        +valueOf(boolean) IAppValue
        +valueOf(Object[]) IAppValue
        +valueOf(List~?~) IAppValue
    }
    
    class ValueType {
        <<enumeration>>
        NIL
        STRING
        NUMBER
        BOOLEAN
        ARRAY
        LIST
        FUNCTION
        OBJECT
    }
    
    class IAppFunction {
        -String name
        -int minParameters
        -int maxParameters
        -List~ParamType~ paramTypes
        -List~List~ParamType~~ paramTypeLists
        -IAppFunctionHandler handler
        -IFunction wrappedFunction
        -boolean isUserFunction
        -FunctionDefinitionStatement userFunctionDef
        -boolean supported
        -String unsupportedReason
        +getName() String
        +getMinParameters() int
        +getMaxParameters() int
        +getParamTypes() List~ParamType~
        +getParamTypeLists() List~List~ParamType~~
        +getParamTypeInfo() String
        +call(Object...) Object
        +call(IAppScript, Object...) Object
        +isSupported() boolean
        +getUnsupportedReason() String
        +isUserFunction() boolean
        +create(String, IAppFunctionHandler) IAppFunction
        +create(String, int, int, IAppFunctionHandler) IAppFunction
        +wrap(IFunction) IAppFunction
        +fromUserFunction(FunctionDefinitionStatement) IAppFunction
    }
    
    class IAppFunctionHandler {
        <<interface>>
        +call(IAppScript, Object[]) Object
    }
    
    class IAppScriptException {
        +IAppScriptException(String)
        +IAppScriptException(String, Throwable)
        +IAppScriptException(Throwable)
    }
    
    IAppScript --> IAppVariable : 创建
    IAppScript --> IAppFunction : 管理
    IAppScript --> IAppValue : 创建
    IAppScript --> RuntimeContext : 包含
    IAppVariable --> VariableScope : 使用
    IAppVariable --> IAppValue : 转换
    IAppValue --> ValueType : 使用
    IAppFunction --> IAppFunctionHandler : 使用
    IAppFunction ..> IAppScriptException : 抛出
```

### 模块加载器类图

```mermaid
classDiagram
    class MjavaModuleLoader {
        -Map~String,String~ loadedModules
        +MjavaModuleLoader()
        +loadModules(String, Interpreter) void
        +loadModule(File, Interpreter) void
        -readFileContent(File) String
        -getModuleName(String) String
        -wrapModuleCode(String, String) String
        +executeMethod(String, String, Object[], Interpreter) Object
        +hasModule(String) boolean
        +getModuleContent(String) String
        +getLoadedModuleNames() Set~String~
    }
    
    class Interpreter {
        <<external>>
        +eval(String) Object
        +set(String, Object) void
    }
    
    MjavaModuleLoader --> Interpreter : 使用
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
